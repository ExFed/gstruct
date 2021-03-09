package com.columnzero.gstruct.model;

import com.columnzero.gstruct.util.TestUtil;
import org.junit.jupiter.api.Test;

import static com.columnzero.gstruct.model.Extern.extern;

class DefaultRefTest {

    private static final DefaultRef<Extern> A1 = new DefaultRef<>(() -> extern("a"));
    private static final DefaultRef<Extern> A2 = new DefaultRef<>(() -> extern("a"));
    private static final DefaultRef<Extern> B = new DefaultRef<>(() -> extern("b"));

    @Test
    void equalsAndHashCode() {
        TestUtil.assertEqualsAndHashCode(A1, A2, B);
    }
}
