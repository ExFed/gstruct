package com.columnzero.gstruct.dsl

import groovy.transform.*

enum Primitive {
    BOOLEAN, NUMBER, STRING
}

@Canonical(includePackage=false, excludes='owner')
@TupleConstructor(excludes='')
class PrimitiveSpec {
    StructSpec owner
    Primitive type
}

@Canonical(includePackage=false, excludes='owner')
class StructSpec {
    StructSpec owner
    Map<String, Object> fields = [:]

    StructSpec(StructSpec owner) {
        this.owner = owner
    }

    PrimitiveSpec bool(String name) {
        fields[name] = new PrimitiveSpec(this, Primitive.BOOLEAN)
    }

    PrimitiveSpec number(String name) {
        fields[name] = new PrimitiveSpec(this, Primitive.NUMBER)
    }

    PrimitiveSpec string(String name) {
        fields[name] = new PrimitiveSpec(this, Primitive.STRING)
    }
}
