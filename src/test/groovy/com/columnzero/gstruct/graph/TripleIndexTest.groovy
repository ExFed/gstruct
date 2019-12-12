package com.columnzero.gstruct.graph

import com.columnzero.gstruct.FQName

import spock.lang.*

class TripleIndexTest extends Specification {

    static final def a = FQName.of('a')
    static final def b = FQName.of('b')
    static final def c = FQName.of('c')
    static final def x = FQName.of('x')
    static final def y = FQName.of('y')
    static final def z = FQName.of('z')

    static final def abc = new GraphTriple(a, b, c)
    static final def cba = new GraphTriple(c, b, a)
    static final def cxx = new GraphTriple(c, x, x)
    static final def xyz = new GraphTriple(x, y, z)
    static final def yzy = new GraphTriple(y, z, y)

    @Unroll
    def 'index finds #filter -> #expected'() {
        setup:
        def sg = new StructGraph()
            .put(abc as List)
            .put(cba as List)

        expect:
        new TripleIndex(sg).findAll(filter) == expected as Set

        where:
        filter                  | expected

        [subj: a]             | [abc]
        [subj: b]             | []
        [subj: c]             | [cba]

        [pred: a]             | []
        [pred: b]             | [abc, cba]
        [pred: c]             | []

        [obj: a]              | [cba]
        [obj: b]              | []
        [obj: c]              | [abc]

        [subj: a, pred: a]  | []
        [subj: a, pred: b]  | [abc]
        [subj: a, pred: c]  | []
        [subj: b, pred: a]  | []
        [subj: b, pred: b]  | []
        [subj: b, pred: c]  | []
        [subj: c, pred: a]  | []
        [subj: c, pred: b]  | [cba]
        [subj: c, pred: c]  | []

        [subj: a, obj: a]  | []
        [subj: a, obj: b]  | []
        [subj: a, obj: c]  | [abc]
        [subj: b, obj: a]  | []
        [subj: b, obj: b]  | []
        [subj: b, obj: c]  | []
        [subj: c, obj: a]  | [cba]
        [subj: c, obj: b]  | []
        [subj: c, obj: c]  | []

        [pred: a, obj: a]  | []
        [pred: a, obj: b]  | []
        [pred: a, obj: c]  | []
        [pred: b, obj: a]  | [cba]
        [pred: b, obj: b]  | []
        [pred: b, obj: c]  | [abc]
        [pred: c, obj: a]  | []
        [pred: c, obj: b]  | []
        [pred: c, obj: c]  | []

        [subj: a, pred: a, obj: a]  | []
        [subj: a, pred: a, obj: b]  | []
        [subj: a, pred: a, obj: c]  | []
        [subj: a, pred: b, obj: a]  | []
        [subj: a, pred: b, obj: b]  | []
        [subj: a, pred: b, obj: c]  | [abc]
        [subj: a, pred: c, obj: a]  | []
        [subj: a, pred: c, obj: b]  | []
        [subj: a, pred: c, obj: c]  | []

        [subj: b, pred: a, obj: a]  | []
        [subj: b, pred: a, obj: b]  | []
        [subj: b, pred: a, obj: c]  | []
        [subj: b, pred: b, obj: a]  | []
        [subj: b, pred: b, obj: b]  | []
        [subj: b, pred: b, obj: c]  | []
        [subj: b, pred: c, obj: a]  | []
        [subj: b, pred: c, obj: b]  | []
        [subj: b, pred: c, obj: c]  | []

        [subj: c, pred: a, obj: a]  | []
        [subj: c, pred: a, obj: b]  | []
        [subj: c, pred: a, obj: c]  | []
        [subj: c, pred: b, obj: a]  | [cba]
        [subj: c, pred: b, obj: b]  | []
        [subj: c, pred: b, obj: c]  | []
        [subj: c, pred: c, obj: a]  | []
        [subj: c, pred: c, obj: b]  | []
        [subj: c, pred: c, obj: c]  | []
    }
}
