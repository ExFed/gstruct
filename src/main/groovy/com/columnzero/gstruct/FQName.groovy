package com.columnzero.gstruct

import groovy.transform.*

@CompileStatic
@Immutable(includePackage = false)
class FQName {
    static final String DELIMITER = '/'

    static FQName of(String path) {
        return of(path.split(DELIMITER))
    }

    static FQName of(String... path) {
        return of(path as List)
    }

    static FQName of(List<String> path) {
        if (path.size() == 0) {
            return null
        }
        if (path.size() == 1) {
            return new FQName(path[0], null)
        }
        return new FQName(path[-1], of(path[0..<-1]))
    }

    static List toPath(FQName cn) {
        return (cn.namespace ? toPath(cn.namespace) : []) + cn.name
    }

    String name
    FQName namespace

    FQName div(String name) {
        return new FQName(name, this)
    }

    String toString() {
        return toPath(this).join(DELIMITER)
    }

    FQName propertyMissing(String name) {
        return new FQName(name, this)
    }

    def methodMissing(String name, Object argsObj) {
        def args = argsObj as Object[]
        if (args.size() == 1 && args[0] instanceof Closure) {
            return new SpecParams(propertyMissing(name), (Closure) args[0])
        }

        throw new MissingMethodException(name, this.getClass(), args)
    }
}
