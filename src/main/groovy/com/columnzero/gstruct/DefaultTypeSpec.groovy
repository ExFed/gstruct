package com.columnzero.gstruct

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
