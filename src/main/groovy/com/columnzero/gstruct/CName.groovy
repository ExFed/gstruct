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
