package com.columnzero.gstruct

class DefaultStructSpec implements StructSpec {
    private final GraphContext $context

    DefaultStructSpec(GraphContext context) {
        this.$context = context
    }

    @Override
    void field(Map names) {
        throw new UnsupportedOperationException('todo')
    }
}