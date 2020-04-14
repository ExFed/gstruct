package com.columnzero.gstruct.lang

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.InheritConstructors

@CompileStatic
class Scopes {
    static final FQName UNSET = new FQName('', null)
    static final FQName GLOBAL = new FQName('', null)
}

@CompileStatic
class Keywords {
    static final FQName PRIMITIVE = new FQName('Primitive', Scopes.GLOBAL)
    static final FQName STRUCT = new FQName('struct', Scopes.GLOBAL)
}

@CompileStatic
class Relationships {
    static final FQName TYPE = new FQName('is', Scopes.GLOBAL)
    static final FQName DESCRIPTION = new FQName('describedBy', Scopes.GLOBAL)
    static final FQName FIELD = new FQName('has', Scopes.GLOBAL)
}

@CompileStatic
@Immutable
class SpecParams {
    FQName name
    Closure configurator
}

@CompileStatic
@InheritConstructors
class DslException extends GroovyRuntimeException {}
