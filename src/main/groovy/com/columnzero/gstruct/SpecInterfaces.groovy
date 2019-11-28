package com.columnzero.gstruct

interface NamespaceSpec {
    void namespace(Map names)
    void type(Map names)
    void struct(Map names)
}

interface TypeSpec {
    void description(String body)
    void setDescription(String body)
}

interface StructSpec {
    void field(Map names)
}