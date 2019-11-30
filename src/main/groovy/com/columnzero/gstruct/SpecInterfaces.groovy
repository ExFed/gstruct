package com.columnzero.gstruct

interface DocSpec {
    final FQName GLOBAL = Scopes.GLOBAL

    // description = 'lorem ipsum'
    void setDescription(String body)
}

interface NamespaceSpec extends DocSpec {
    // namespace x.y.z { ... }
    void namespace(SpecParams params)
    void namespace(FQName name, Closure configurator)

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
