package com.columnzero.gstruct

import groovy.transform.*

@Immutable(includePackage = false)
class CName {
    public static final String DELIMITER = '/'

    String name
    CName namespace

    List getPath() {
        return (namespace?.path ?: []) + name
    }

    String toString() {
        return this.path.join(DELIMITER)
    }
}

@Immutable(includePackage = false, allNames = true)
class CNameBuilder {
    String $name
    CName $namespace

    def asType(Class clazz) {
        if (clazz == CName) {
            return new CName($name, $namespace)
        }

        throw new ClassCastException(clazz)
    }
}
