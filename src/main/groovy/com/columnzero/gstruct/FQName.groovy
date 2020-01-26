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

    static List toPath(FQName fqn) {
        return (fqn.namespace ? toPath(fqn.namespace) : []) + fqn.basename
    }

    String basename
    FQName namespace

    FQName div(String basename) {
        return new FQName(basename, this)
    }

    String toString() {
        return toPath(this).join(DELIMITER)
    }

    FQName propertyMissing(String basename) {
        return new FQName(basename, this)
    }

    def methodMissing(String basename, Object argsObj) {
        def args = argsObj as Object[]
        if (args.size() == 1 && args[0] instanceof Closure) {
            return new SpecParams(propertyMissing(basename), (Closure) args[0])
        }

        throw new MissingMethodException(basename, this.getClass(), args)
    }
}
