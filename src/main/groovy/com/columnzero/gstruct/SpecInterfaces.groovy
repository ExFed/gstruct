package com.columnzero.gstruct

interface NamespaceSpec {
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

interface TypeSpec {
    // description 'lorem ipsum'
    void description(String body)

    // description = 'lorem ipsum'
    void setDescription(String body)
}

interface StructSpec {
    void field(Map names)
}