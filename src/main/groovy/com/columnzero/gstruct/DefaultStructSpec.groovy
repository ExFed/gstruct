package com.columnzero.gstruct

class DefaultStructSpec implements StructSpec {
    private final GraphContext $context

    DefaultStructSpec(GraphContext context) {
        this.$context = context
    }

    @Override
    void setDescription(String body) {
        $context.putStr(Relationships.DESCRIPTION, body)
    }

    @Override
    void field(Map names) {
        names.each { name, param ->
            // coerce name into CName
            if (!(name instanceof CName)) {
                name = new CName(name, $context.name)
            }

            $context.put(Relationships.FIELD, name)
            $context.graph.put(name, Relationships.TYPE, param)
        }
    }
}