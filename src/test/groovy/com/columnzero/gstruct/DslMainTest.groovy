package com.columnzero.gstruct

import spock.lang.*

import static com.columnzero.gstruct.Keywords.*
import static com.columnzero.gstruct.Relationships.*

class DslMainTest extends Specification {
    // shorthand helper for names in the global namespace
    private static def gn(String cName) {
        return new CName(cName, Scopes.GLOBAL)
    }

    // shorthand helper to get a gStruct file
    private static def gStruct(String filename) {
        return new File(DslMainTest.getResource(filename).toURI())
    }

    def 'typedefs parse'() {
        given:
            def dslFile = gStruct('typedefs.gstruct')
            def expect = new StructGraph()
                .put(gn('string'), TYPE, PRIMITIVE)
                .put(gn('number'), TYPE, PRIMITIVE)
                .put(gn('bool'), TYPE, PRIMITIVE)
                .put(gn('data'), TYPE, gn('string'))
                .put(gn('value'), TYPE, gn('number'))
                .put(gn('fakeNews'), TYPE, gn('bool'))
        when:
            def actual = DslMain.parse(dslFile)
        then:
            actual == expect
    }

    def 'namespace parses'() {
        given:
            def dslFile = gStruct('namespace.gstruct')
            def name = CName.of('/x/y/z/foobar')
            def expect = new StructGraph()
                .put(name, TYPE, PRIMITIVE)

        when:
            def actual = DslMain.parse(dslFile)

        then:
            actual == expect
    }

    def 'nested namespaces parse'() {
        given:
            def dslFile = gStruct('nested_namespaces.gstruct')
            def namespace = CName.of('/x/y/z/foobar')
            def expect = new StructGraph()
                .put(namespace, TYPE, PRIMITIVE)

        when:
            def actual = DslMain.parse(dslFile)

        then:
            actual == expect
    }

    @Ignore
    def 'empty struct parses'() {
        given:
            def dslFile = gStruct 'empty_struct.gstruct'
            def expect = new StructGraph()
                .put(gn('empty'), TYPE, STRUCT)

        when:
            def actual = DslMain.parse(dslFile)

        then:
            actual == expect
    }

    @Ignore
    def 'struct parses'() {
        given:
            def dslFile = gStruct 'struct.gstruct'
            def expect = new StructGraph()
                .put(gn('object'), TYPE, STRUCT)
                .put(CName.of('/object/data'), TYPE, PRIMITIVE)

        when:
            def actual = DslMain.parse(dslFile)

        then:
            actual == expect
    }
}
