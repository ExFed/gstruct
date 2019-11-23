package com.columnzero.gstruct

import groovy.transform.*

@Immutable(includePackage = false)
class CName {
    static final String DELIMITER = '/'

    static CName of(String path) {
        return of(path.split(DELIMITER))
    }

    static CName of(String... path) {
        return of(path as List)
    }

    static CName of(List path) {
        if (path.size() == 0) {
            return null
        }
        if (path.size() == 1) {
            return new CName(path[0], null)
        }
        return new CName(path[-1], of(path[0..-2]))
    }

    static List toPath(CName cn) {
        return (cn.namespace ? toPath(cn.namespace) : []) + cn.name
    }

    String name
    CName namespace

    String toString() {
        return toPath(this).join(DELIMITER)
    }

    CName propertyMissing(String name) {
        return new CName(name, this)
    }
}
