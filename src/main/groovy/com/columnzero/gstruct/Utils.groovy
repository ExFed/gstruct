package com.columnzero.gstruct

class Scopes {
    static final CName UNSET = new CName('', null)
    static final CName GLOBAL = new CName('', null)
}

class Keywords {
    static final CName PRIMITIVE = new CName('primitive', Scopes.GLOBAL)
    static final CName STRUCT = new CName('struct', Scopes.GLOBAL)
}

class Relationships {
    static final CName TYPE = new CName('is', Scopes.GLOBAL)
    static final CName DESCRIPTION = new CName('describedBy', Scopes.GLOBAL)
    static final CName FIELD = new CName('has', Scopes.GLOBAL)
}

@groovy.transform.Immutable
class SpecParams {
    CName name
    Closure configurator
}

@groovy.transform.InheritConstructors
class DslException extends GroovyRuntimeException {}
