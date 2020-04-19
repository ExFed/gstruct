package com.columnzero.gstruct.graph


import com.columnzero.gstruct.glang.FQName

class TripleIndex {
    private final def s = ([:] as LinkedHashMap).withDefault { [] as LinkedHashSet }
    private final def p = ([:] as LinkedHashMap).withDefault { [] as LinkedHashSet }
    private final def o = ([:] as LinkedHashMap).withDefault { [] as LinkedHashSet }

    private final def sp = ([:] as LinkedHashMap).withDefault { [] as LinkedHashSet }
    private final def po = ([:] as LinkedHashMap).withDefault { [] as LinkedHashSet }
    private final def os = ([:] as LinkedHashMap).withDefault { [] as LinkedHashSet }

    private final Set<Triple> spo

    TripleIndex(Graph graph) {
        spo = new LinkedHashSet<>(graph.triples) // copy the triple set to mitigate mutability
        spo.each { Triple t ->
            s[t.subject] << t
            p[t.predicate] << t
            o[t.object] << t
            sp[[t.subject, t.predicate]] << t
            po[[t.predicate, t.object]] << t
            os[[t.object, t.subject]] << t
        }
    }

    Set<Triple> findAll(Map filter) {
        def subj = filter.s ?: filter.subj ?: filter.subject
        def pred = filter.p ?: filter.pred ?: filter.predicate
        def obj = filter.o ?: filter.obj ?: filter.object
        return findAll(subj as FQName, pred as FQName, obj)
    }

    Set<Triple> findAll(FQName subj = null, FQName pred = null, Object obj = null) {
        def filterFlags = (subj != null ? 0b100 : 0) \
                        + (pred != null ? 0b010 : 0) \
                        + (obj != null ? 0b001 : 0)
        return findAllPrivate(subj, pred, obj, filterFlags).asUnmodifiable()
    }

    private Set<Triple> findAllPrivate(FQName subj, FQName pred, Object obj, int filterFlags) {
        switch (filterFlags) {
            case 0b000:
                return new LinkedHashSet<>(spo as Set)
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
                def triple = new Triple(subj, pred, obj)
                return triple in spo ? Collections.singleton(triple) : Collections.emptySet() as Set<Triple>
            default:
                throw new IndexOutOfBoundsException("Filter flags out of bounds: $filterFlags")
        }
    }

    boolean isCase(Triple triple) {
        return spo.contains(triple)
    }

    boolean isCase(Collection<Triple> triples) {
        return spo.containsAll(triples)
    }

    boolean isCase(Graph graph) {
        return isCase(graph.triples)
    }
}
