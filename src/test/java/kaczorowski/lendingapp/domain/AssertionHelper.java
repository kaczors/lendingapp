package kaczorowski.lendingapp.domain;

import org.assertj.core.api.AbstractAssert;

import java.util.Objects;

public abstract class AssertionHelper<S extends AbstractAssert<S, A>, A> extends AbstractAssert<S, A> {
    protected AssertionHelper(A actual, Class<?> selfType) {
        super(actual, selfType);
    }

    protected S failIfNotEqual(String fieldName, Object actualValue, Object expectedValue) {
        if(!Objects.deepEquals(actualValue, expectedValue)) {
            failWithMessage(
                    "\nExpected %s of:\n  <%s>\nto be:\n  <%s> but was:\n  <%s>",
                    fieldName, actual, expectedValue, actualValue);
        }
        return (S) this;
    }

}
