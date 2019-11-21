package com.columnzero.gstruct

import groovy.transform.*

@Canonical(includePackage = false)
class CName {
    public static final String DELIMITER = '/'

    String name
    CName namespace

    CName(String name, CName namespace) {
        this.name = name
        this.namespace = namespace
    }

    List getPath() {
        return (namespace?.path ?: []) + name
    }

    String toString() {
        return this.path.join(DELIMITER)
    }
}
