package kaczorowski.lendingapp;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import kaczorowski.lendingapp.util.TimeProvider;
import lombok.SneakyThrows;
import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.mockito.BDDMockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static com.jayway.restassured.RestAssured.*;
import static kaczorowski.lendingapp.TestUtils.json;
import static kaczorowski.lendingapp.domain.Loan.MAX_AMOUNT_STRING_REPRESENTATION;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

@SpringApplicationConfiguration(classes = LendingApplicationAcceptanceTest.Config.class)
@WebIntegrationTest("server.port:0")
@TestExecutionListeners(inheritListeners = false, listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class LendingApplicationAcceptanceTest extends AbstractTestNGSpringContextTests {
    private static final String JOHN = "John";
    private static final String DOE = "Doe";
    private static final String MARIAN = "Marian";
    private static final String KOWALSKI = "Kowalski";
    private static final float SAMPLE_VALID_AMOUNT = 20.40f;
    private static final float SAMPLE_EXTENDED_VALID_AMOUNT = 30.60f;
    private static final String SAMPLE_TERM_STRING = "2005-02-12";
    private static final String SAMPLE_EXTENDED_TERM_STRING = "2005-02-19";
    private static final DateTime DATE_BETWEEN_0000_AND_0600AM = new DateTime("2010-06-30T01:20");
    private static final DateTime DATE_AFTER_0060AM = new DateTime("2010-06-30T18:20");

    @Value("${local.server.port}")
    private int port;

    @Autowired
    TimeProvider timeProvider;

    @BeforeMethod
    public void setUp() {
        RestAssured.port = port;
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
    }

    @Test
    public void should_create_new_loan_when_no_risk_associated_with_application() {
        BDDMockito.given(timeProvider.getCurrentDate()).willReturn(DATE_AFTER_0060AM);
        String loanLocation =
            given()
                .body(json(
                "{" +
                "   firstName: '" + JOHN + "'," +
                "   lastName: '" + DOE + "'," +
                "   amount: " + SAMPLE_VALID_AMOUNT + "," +
                "   term: '" + SAMPLE_TERM_STRING + "'" +
                "}"))
            .when()
                .post("/loans")
            .then()
                .statusCode(HttpStatus.SC_CREATED)
            .extract().header("Location");

            when()
                .get(URI.create(loanLocation))
            .then()
                .statusCode(HttpStatus.SC_OK)
                .body("firstName", is(JOHN))
                .body("lastName", is(DOE))
                .body("amount", is(SAMPLE_VALID_AMOUNT))
                .body("term", is(SAMPLE_TERM_STRING));

    }

    @Test
    public void should_reject_loan_application_when_is_made_between_0000_and_0600AM_with_max_possible_amount() {
        BDDMockito.given(timeProvider.getCurrentDate()).willReturn(DATE_BETWEEN_0000_AND_0600AM.plusDays(1));
        given()
            .body(json(
                "{" +
                "   firstName: '" + JOHN + "'," +
                "   lastName: '" + DOE + "'," +
                "   amount: " + MAX_AMOUNT_STRING_REPRESENTATION + "," +
                "   term: '" + SAMPLE_TERM_STRING + "'" +
                "}"))
        .when()
            .post("/loans")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("errorMessage", is("Loan application with max allowed amount cannot be placed between 00.00 and 06.00 AM."));
    }

    @Test
    public void should_reject_loan_application_when_to_many_applications_per_day() {
        //given
        BDDMockito.given(timeProvider.getCurrentDate()).willReturn(DATE_AFTER_0060AM.plusDays(2));
        createSampleLoan("Chuck", "Norris");
        createSampleLoan("Zbigniew", "Buła");
        createSampleLoan(JOHN, DOE);

        given()
                .body(json(
                        "{" +
                                "   firstName: '" + JOHN + "'," +
                                "   lastName: '" + DOE + "'," +
                                "   amount: " + SAMPLE_VALID_AMOUNT + "," +
                                "   term: '" + SAMPLE_TERM_STRING + "'" +
                                "}"))
                .when()
            .post("/loans")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("errorMessage", is("Exceeded maximum number of applications per day."));
    }

    @Test
    public void should_extend_loan_term_by_one_week_and_increase_loan_interest_by_factor_1_5() {
        //given
        BDDMockito.given(timeProvider.getCurrentDate()).willReturn(DATE_AFTER_0060AM.plusDays(3));
        Number loanId = createSampleLoan(JOHN, DOE);

        when()
            .post("/loans/{id}/extend", loanId)
        .then()
            .statusCode(HttpStatus.SC_OK);

        when()
           .get("/loans/{id}", loanId)
        .then()
           .body("amount", is(SAMPLE_EXTENDED_VALID_AMOUNT))
           .body("term", is(SAMPLE_EXTENDED_TERM_STRING));
    }

    @Test
    @SneakyThrows
    public void should_return_loan_history_with_loan_extensions_included() {
        //given
        BDDMockito.given(timeProvider.getCurrentDate()).willReturn(DATE_AFTER_0060AM.plusDays(4));
        Number firstLoanId = createSampleLoan(MARIAN, KOWALSKI);
        Number secondLoanId = createSampleLoan(MARIAN, KOWALSKI);

        extendLoan(firstLoanId);
        extendLoan(firstLoanId);
        extendLoan(secondLoanId);

        String response =
            given()
                .parameter("firstName", MARIAN)
                .parameter("lastName", KOWALSKI)
            .when()
                .get("/loans")
            .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().body().asString();

        JSONAssert.assertEquals(
                json("[" +
                        "   {" +
                        "      firstName:'Marian'," +
                        "      lastName:'Kowalski'," +
                        "      amount:45.90," +
                        "      term:'2005-02-26'," +
                        "      extensions:[" +
                        "         {" +
                        "            amount:30.60," +
                        "            term:'2005-02-19'," +
                        "            date:'2010-07-04'" +
                        "         }," +
                        "         {" +
                        "            amount:45.90," +
                        "            term:'2005-02-26'," +
                        "            date:'2010-07-04'" +
                        "         }" +
                        "       ]" +
                        "   }," +
                        "   {" +
                        "      firstName:'Marian'," +
                        "      lastName:'Kowalski'," +
                        "      amount:30.60," +
                        "      term:'2005-02-19'," +
                        "       extensions:[" +
                        "         {" +
                        "            amount:30.60," +
                        "            term:'2005-02-19'," +
                        "            date:'2010-07-04'" +
                        "         }" +
                        "      ]" +
                        "   }" +
                        "]")
                , response, JSONCompareMode.LENIENT);
    }

    @Configuration
    @Import(LendingApplication.class)
    public static class Config {
        @Bean
        TimeProvider timeProvider(){
            return mock(TimeProvider.class);
        }
    }

    private Number createSampleLoan(String firstName, String lastName){
        String loanLocation =
            given()
                .body(json(
                        "{" +
                    "   firstName: '" + firstName + "'," +
                    "   lastName: '" + lastName + "'," +
                    "   amount: " + SAMPLE_VALID_AMOUNT + "," +
                    "   term: '" + SAMPLE_TERM_STRING + "'" +
                    "}"))
            .when()
                .post("/loans")
            .then()
                .extract().header("Location");


        return get(URI.create(loanLocation)).path("id");
    }

    private void extendLoan(Number loanId){
        post("/loans/{id}/extend", loanId);
    }

}