package com.columnzero.gstruct

interface DocSpec {
    // description = 'lorem ipsum'
    void setDescription(String body)
}

interface NamespaceSpec extends DocSpec {
    final CName GLOBAL = Scopes.GLOBAL

    // namespace x.y.z { ... }
    void namespace(SpecParams params)
    void namespace(CName name, Closure configurator)

    // type foo: bar
    // type foo: bar { ... }
    void type(Map names)

    // struct foo: { ... }
    void struct(Map names)
}

interface TypeSpec extends DocSpec {
}

interface StructSpec extends DocSpec {
    void field(Map names)
}