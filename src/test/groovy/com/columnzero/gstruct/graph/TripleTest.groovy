package com.columnzero.gstruct.graph

import com.columnzero.gstruct.FQName

import spock.lang.*

class TripleTest extends Specification {

    static final def a = FQName.of('a')
    static final def b = FQName.of('b')
    static final def c = FQName.of('c')

    def 'as String'() {
        given:
        def triple = new Triple(a, b, c)

        expect:
        triple as String == triple.toString()
    }

    def 'as List'() {
        given:
        def triple = new Triple(a, b, c)

        expect:
        triple as List == [a, b, c]
    }

    def 'asType ClassCastException'() {
        given:
        def triple = new Triple(a, b, c)

        when:
        triple as ImpossibleType

        then:
        thrown(ClassCastException)
    }

    private static class ImpossibleType {}
}
