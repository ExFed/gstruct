package com.columnzero.gstruct

import groovy.transform.BaseScript
import spock.lang.Specification

import com.columnzero.gstruct.dsl.StructSpec

class DslMainTest extends Specification {
    def "primitives parse correctly"() {
        given:
            def dslFile = new File(this.getClass().getResource("primitives.gstruct").toURI())
            def expectSpec = new StructSpec(null)
            expectSpec.string('data')
            expectSpec.number('value')
            expectSpec.bool('lies')

        when:
            def spec = DslMain.parse(dslFile)

        then:
            assert spec == expectSpec
    }
}
