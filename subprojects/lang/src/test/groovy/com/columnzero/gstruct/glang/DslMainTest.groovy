package com.columnzero.gstruct.glang

import com.columnzero.gstruct.graph.Graph
import spock.lang.Ignore
import spock.lang.Specification

import static com.columnzero.gstruct.glang.Keywords.PRIMITIVE
import static com.columnzero.gstruct.glang.Keywords.STRUCT
import static com.columnzero.gstruct.glang.Relationships.DESCRIPTION
import static com.columnzero.gstruct.glang.Relationships.FIELD
import static com.columnzero.gstruct.glang.Relationships.TYPE

class DslMainTest extends Specification {
    // shorthand helper for names in the global namespace
    private static def gn(String name) {
        return new FQName(name, Scopes.GLOBAL)
    }

    // shorthand helper to get a gStruct file
    private static def gStruct(String filename) {
        return new File(DslMainTest.getResource(filename).toURI())
    }

    @Ignore("doesn't play nicely with java implementation")
    def 'typedefs parse'() {
        given:
        def dslFile = gStruct('typedefs.gstruct')
        def expect = new Graph()
                .put(gn('String'), TYPE, PRIMITIVE)
                .put(gn('Number'), TYPE, PRIMITIVE)
                .put(gn('Bool'), TYPE, PRIMITIVE)
                .put(gn('StringLike'), TYPE, gn('String'))
                .put(gn('NumberLike'), TYPE, gn('Number'))
                .put(gn('Data'), TYPE, gn('String'))
                .put(gn('Value'), TYPE, gn('Number'))
                .put(gn('FakeNews'), TYPE, gn('Bool'))
                .put(gn('FakeNews'), DESCRIPTION, 'full of alternative facts')
        when:
        def actual = DslMain.parse(dslFile)
        then:
        actual == expect
    }

    def 'namespace parses'() {
        given:
        def dslFile = gStruct('namespace.gstruct')
        def namespace = FQName.of('/x/y/z')
        def name = new FQName('FooBar', namespace)
        def expect = new Graph()
                .put(name, TYPE, PRIMITIVE)
                .put(namespace, DESCRIPTION, 'lorem ipsum')

        when:
        def actual = DslMain.parse(dslFile)

        then:
        actual == expect
    }

    def 'nested namespaces parse'() {
        given:
        def dslFile = gStruct('nested_namespaces.gstruct')
        def name = FQName.of('/x/y/z/FooBar')
        def expect = new Graph()
                .put(name, TYPE, PRIMITIVE)

        when:
        def actual = DslMain.parse(dslFile)

        then:
        actual == expect
    }

    def 'empty struct parses'() {
        given:
        def dslFile = gStruct 'empty_struct.gstruct'
        def expect = new Graph()
                .put(gn('Empty'), TYPE, STRUCT)

        when:
        def actual = DslMain.parse(dslFile)

        then:
        actual == expect
    }

    def 'struct parses'() {
        given:
        def dslFile = gStruct 'struct.gstruct'
        def objName = gn 'Object'
        def fieldName = FQName.of('/Object/data')
        def expect = new Graph()
                .put(objName, TYPE, STRUCT)
                .put(fieldName, TYPE, PRIMITIVE)
                .put(objName, FIELD, fieldName)
                .put(objName, DESCRIPTION, 'stuff and things')

        when:
        def actual = DslMain.parse(dslFile)

        then:
        actual == expect
    }
}
