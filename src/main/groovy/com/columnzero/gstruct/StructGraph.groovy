package com.columnzero.gstruct

import groovy.transform.*

@EqualsAndHashCode
class StructGraph {
    final static StructGraph sg = new StructGraph()

    static void edge(CName subject, CName predicate, CName object) {
        sg.put(subject, predicate, object)
    }

    // [ Subject : [ Object : [PredicateSet] ] ]
    // e.g.: sop['parent']['child'] += 'isType'
    private final def sop = [:].withDefault {[:].withDefault {[] as Set}}

    public void put(CName subject, CName predicate, CName object) {
        sop[subject][object] += predicate
    }

    public Map getSop() {
        return  sop
    }
}
