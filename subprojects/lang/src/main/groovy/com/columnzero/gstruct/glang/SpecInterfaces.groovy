package com.columnzero.gstruct.glang

import groovy.transform.CompileStatic

@CompileStatic
interface DocSpec {
    final FQName GLOBAL = Scopes.GLOBAL

    // description = 'lorem ipsum'
    void setDescription(String body)
}

@CompileStatic
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

@CompileStatic
interface TypeSpec extends DocSpec {
}

@CompileStatic
interface StructSpec extends DocSpec {
    void field(Map<FQName, Object> names)
}
