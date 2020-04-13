package com.columnzero.gstruct

import groovy.transform.CompileStatic

@CompileStatic
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
    void field(Map<FQName, Object> names) {
        names.each { name, param ->
            def fqname
            // coerce name into FQName
            if (!(name instanceof FQName)) {
                fqname = new FQName(name as String, $context.name)
            } else {
                fqname = name as FQName
            }

            $context.put(Relationships.FIELD, fqname)
            $context.graph.put(fqname, Relationships.TYPE, param)
        }
    }
}
