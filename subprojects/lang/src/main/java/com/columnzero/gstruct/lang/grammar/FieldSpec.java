package com.columnzero.gstruct.lang.grammar;

/** Specifies a field element. */
public interface FieldSpec extends DocumentationSpec {

    /** Declares the field type. */
    void type(RefSpec spec);
}
