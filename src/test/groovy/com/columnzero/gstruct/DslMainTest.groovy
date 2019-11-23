package com.columnzero.gstruct

import spock.lang.Specification

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

    def 'primitives parse'() {
        given:
            def dslFile = gStruct('primitives.gstruct')
            def expect = new StructGraph()
                .put(gn('string'), TYPE, gn('primitive'))
                .put(gn('number'), TYPE, gn('primitive'))
                .put(gn('bool'), TYPE, gn('primitive'))
                .put(gn('data'), TYPE, gn('string'))
                .put(gn('value'), TYPE, gn('number'))
                .put(gn('lies'), TYPE, gn('bool'))
        when:
            def actual = DslMain.parse(dslFile)
        then:
            actual.sop == expect.sop
    }
}
