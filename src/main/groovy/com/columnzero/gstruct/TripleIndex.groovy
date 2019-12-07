package com.columnzero.gstruct

class TripleIndex {
    private final def s = [:].withDefault { [] as Set }
    private final def p = [:].withDefault { [] as Set }
    private final def o = [:].withDefault { [] as Set }

    private final def sp = [:].withDefault { [] as Set }
    private final def po = [:].withDefault { [] as Set }
    private final def os = [:].withDefault { [] as Set }

    private final def spo

    TripleIndex(StructGraph graph) {
        spo = ([] as Set) + graph.triples // copy the triple set
        spo.each { t ->
            s[t.subject] << t
            p[t.predicate] << t
            o[t.object] << t
            sp[[t.subject, t.predicate]] << t
            po[[t.predicate, t.object]] << t
            os[[t.object, t.subject]] << t
        }

    }

    Set<GraphTriple> findAll(Map filter) {
        return findAll(filter.subj, filter.pred, filter.obj)
    }

    Set<GraphTriple> findAll(subj = null, pred = null, obj = null) {
        return findAllPrivate(subj, pred, obj).asUnmodifiable()
    }

    private Set<GraphTriple> findAllPrivate(subj, pred, obj) {
        def q = (subj != null ? 0b100 : 0) \
            + (pred != null ? 0b010 : 0) \
            + (obj != null ? 0b001 : 0)

        switch(q) {
            case 0b000:
                return [] as Set
            case 0b100:
                return s[subj]
            case 0b010:
                return p[pred]
            case 0b001:
                return o[obj]
            case 0b110:
                return sp[[subj, pred]]
            case 0b011:
                return po[[pred, obj]]
            case 0b101:
                return os[[obj, subj]]
            case 0b111:
                def triple = new GraphTriple(subj, pred, obj)
                return (triple in spo ? [triple] : []) as Set
            default:
                throw new IndexOutOfBoundsException("Query index out of bounds: $q")
        }
    }
}
