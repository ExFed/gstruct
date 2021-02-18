package com.columnzero.gstruct.model;

import com.columnzero.gstruct.util.TestUtil;
import org.junit.jupiter.api.Test;

class DefaultRefTest {

    private static final DefaultRef<String> A1 = new DefaultRef<>(() -> "a");
    private static final DefaultRef<String> A2 = new DefaultRef<>(() -> "a");
    private static final DefaultRef<String> B = new DefaultRef<>(() -> "b");


    @Test
    void equalsAndHashCode() {
        TestUtil.assertEqualsAndHashCode(A1, A2, B);
    }
}
