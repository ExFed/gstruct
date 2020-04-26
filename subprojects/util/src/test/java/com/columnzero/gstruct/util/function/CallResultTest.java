package com.columnzero.gstruct.util.function;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CallResultTest {

    private static <T> void assertResults(T successVal, TestException failureVal) throws Exception {
        final CallResult<T> r0 = CallResult.success(successVal);

        assertThat(r0.isSuccess()).isTrue();
        assertThat(r0.get()).isSameInstanceAs(successVal);

        final CallResult<T> r1 = r0.map(arg -> arg);

        assertThat(r1.isSuccess()).isTrue();
        assertThat(r1.get()).isSameInstanceAs(successVal);

        final CallResult<T> r2 = r0.map(arg -> {
            throw new TestException();
        });

        assertThat(r2.isSuccess()).isFalse();
        assertThrows(TestException.class, r2::get);

        final CallResult<T> r3 = CallResult.failure(failureVal);

        assertThat(r3.isSuccess()).isFalse();
        assertThrows(TestException.class, r3::get);

        final CallResult<T> r4 = r3.map(arg -> arg);

        assertThat(r4.isSuccess()).isFalse();
        assertThrows(TestException.class, r4::get);
    }

    @Test
    void successTypeIsException() throws Exception {
        final TestException exceptionAsSuccess = new TestException();
        assertResults(exceptionAsSuccess, exceptionAsSuccess);
    }

    @Test
    void successTypeIsNull() throws Exception {
        assertResults(null, new TestException());
    }

    @Test
    void of() throws Exception {
        final Object successVal = new Object();
        final CallResult<Object> r0 = CallResult.of(() -> successVal);

        assertThat(r0.isSuccess()).isTrue();
        assertThat(r0.get()).isSameInstanceAs(successVal);

        final TestException failureVal = new TestException();
        final CallResult<Object> r1 = CallResult.of(() -> {
            throw failureVal;
        });

        assertThat(r1.isSuccess()).isFalse();
        assertThrows(TestException.class, r1::get);
    }

    private final static class TestException extends Exception {
    }
}
