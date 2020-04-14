package com.columnzero.gstruct.lang


import groovy.transform.CompileStatic
import groovy.transform.Immutable

@CompileStatic
@Immutable(includePackage = false)
class FQName {
    static final FQName ROOT = new FQName('', null)

    static final String DELIMITER = '/'

    /**
     * Creates a new instance from the given absolute path string.
     */
    static FQName of(String absolutePath) {
        if (absolutePath.isEmpty()) {
            throw new IllegalArgumentException("Cannot parse empty path")
        }

        if (!absolutePath.startsWith(DELIMITER)) {
            throw new IllegalArgumentException("Cannot parse relative path: $absolutePath")
        }

        return ROOT.path(absolutePath.substring(DELIMITER.length()))
    }

    String basename
    FQName namespace

    FQName div(String basename) {
        return new FQName(basename, this)
    }

    String toString() {
        return this.toPath().join(DELIMITER)
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

    /**
     * Creates a path list.
     */
    List<String> toPath() {
        return (namespace?.toPath() ?: []) + basename
    }

    /**
     * Creates a new name from the given path string. Path may be relative or absolute.
     */
    FQName path(String path) {
        // if the path explicitly declares it is absolute, parse relative to ROOT
        if (path.startsWith(DELIMITER)) {
            return ROOT.path(path.substring(DELIMITER.length()))
        }

        return path.tokenize(DELIMITER).inject(this) { ns, bn -> new FQName(bn, ns) }
    }
}
