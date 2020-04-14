package com.columnzero.gstruct.lang

import groovy.transform.CompileStatic

@CompileStatic
class DefaultTypeSpec implements TypeSpec {
    private final GraphContext $context

    DefaultTypeSpec(GraphContext context) {
        this.$context = context
    }

    @Override
    void setDescription(String body) {
        $context.putStr(Relationships.DESCRIPTION, body)
    }
}
