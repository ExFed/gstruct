package com.columnzero.gstruct

class DefaultTypeSpec implements TypeSpec {
    private final GraphContext $context

    String description

    DefaultTypeSpec(GraphContext context) {
        this.$context = context
    }

    @Override
    void description(String body) {
        this.description = body
    }
}
