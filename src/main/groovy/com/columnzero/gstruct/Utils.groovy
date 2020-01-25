package com.columnzero.gstruct

@groovy.transform.CompileStatic
class Scopes {
    static final FQName UNSET = new FQName('', null)
    static final FQName GLOBAL = new FQName('', null)
}

@groovy.transform.CompileStatic
class Keywords {
    static final FQName PRIMITIVE = new FQName('primitive', Scopes.GLOBAL)
    static final FQName STRUCT = new FQName('struct', Scopes.GLOBAL)
}

@groovy.transform.CompileStatic
class Relationships {
    static final FQName TYPE = new FQName('is', Scopes.GLOBAL)
    static final FQName DESCRIPTION = new FQName('describedBy', Scopes.GLOBAL)
    static final FQName FIELD = new FQName('has', Scopes.GLOBAL)
}

@groovy.transform.CompileStatic
@groovy.transform.Immutable
class SpecParams {
    FQName name
    Closure configurator
}

@groovy.transform.CompileStatic
@groovy.transform.InheritConstructors
class DslException extends GroovyRuntimeException {}
