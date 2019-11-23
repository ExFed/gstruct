package com.columnzero.gstruct

import groovy.transform.*

@Immutable(includePackage = false)
class CName {
    static final String DELIMITER = '/'

    static List toPath(CName cn) {
        return (cn.namespace ? toPath(cn.namespace) : []) + cn.name
    }

    String name
    CName namespace

    String toString() {
        return toPath(this).join(DELIMITER)
    }
}
