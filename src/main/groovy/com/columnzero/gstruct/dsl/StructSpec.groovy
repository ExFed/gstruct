package com.columnzero.gstruct.dsl

import groovy.transform.Canonical

enum Primitive {
    BOOLEAN, NUMBER, STRING
}

@Canonical(includePackage=false, excludes='owner')
class PrimitiveSpec {
    StructSpec owner
    Primitive type

    PrimitiveSpec(Primitive type) {
        this.owner = owner
        this.type = type
    }
}

@Canonical(includePackage=false, excludes='owner')
class StructSpec {
    StructSpec owner
    Map<String, Object> fields = [:]

    StructSpec(StructSpec owner) {
        this.owner = owner
    }

    PrimitiveSpec bool(String name) {
        fields[name] = new PrimitiveSpec(Primitive.BOOLEAN)
    }

    PrimitiveSpec number(String name) {
        fields[name] = new PrimitiveSpec(Primitive.NUMBER)
    }

    PrimitiveSpec string(String name) {
        fields[name] = new PrimitiveSpec(Primitive.STRING)
    }
}
