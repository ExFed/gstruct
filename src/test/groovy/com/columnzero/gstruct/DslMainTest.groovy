package com.columnzero.gstruct

import spock.lang.*

import com.columnzero.gstruct.graph.*

import static com.columnzero.gstruct.Keywords.*
import static com.columnzero.gstruct.Relationships.*

class DslMainTest extends Specification {
    // shorthand helper for names in the global namespace
    private static def gn(String name) {
        return new FQName(name, Scopes.GLOBAL)
    }

    // shorthand helper to get a gStruct file
    private static def gStruct(String filename) {
        return new File(DslMainTest.getResource(filename).toURI())
    }

    def 'typedefs parse'() {
        given:
            def dslFile = gStruct('typedefs.gstruct')
            def expect = new Graph()
                .put(gn('string'), TYPE, PRIMITIVE)
                .put(gn('number'), TYPE, PRIMITIVE)
                .put(gn('bool'), TYPE, PRIMITIVE)
                .put(gn('data'), TYPE, gn('string'))
                .put(gn('value'), TYPE, gn('number'))
                .put(gn('fakeNews'), TYPE, gn('bool'))
                .put(gn('fakeNews'), DESCRIPTION, 'full of alternative facts')
        when:
            def actual = DslMain.parse(dslFile)
        then:
            actual == expect
    }

    def 'namespace parses'() {
        given:
            def dslFile = gStruct('namespace.gstruct')
            def namespace = FQName.of('/x/y/z')
            def name = new FQName('foobar', namespace)
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
            def name = FQName.of('/x/y/z/foobar')
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
                .put(gn('empty'), TYPE, STRUCT)

        when:
            def actual = DslMain.parse(dslFile)

        then:
            actual == expect
    }

    def 'struct parses'() {
        given:
            def dslFile = gStruct 'struct.gstruct'
            def objName = gn 'object'
            def fieldName = FQName.of('/object/data')
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
