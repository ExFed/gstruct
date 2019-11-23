package com.columnzero.gstruct

import spock.lang.Specification

class DslMainTest extends Specification {
    // shorthand helper for names in the global namespace
    def gn(String cName) {
        return new CName(cName, Scopes.GLOBAL)
    }

    def "primitives parse"() {
        given:
            def dslFile = new File(this.getClass().getResource("primitives.gstruct").toURI())
            def expect = new StructGraph()
            expect.put(gn('string'), gn('isType'), gn('primitive'))
            expect.put(gn('number'), gn('isType'), gn('primitive'))
            expect.put(gn('bool'), gn('isType'), gn('primitive'))
            expect.put(gn('data'), gn('isType'), gn('string'))
            expect.put(gn('value'), gn('isType'), gn('number'))
            expect.put(gn('lies'), gn('isType'), gn('bool'))
        when:
            def actual = DslMain.parse(dslFile)
        then:
            actual.sop == expect.sop
    }
}
