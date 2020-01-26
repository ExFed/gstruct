package com.columnzero.gstruct

import spock.lang.Specification

class FQNameTest extends Specification {

    static final def ROOT = new FQName('', null)

    def "FQName path"() {
        given:
            def fqn = new FQName('leaf', new FQName('inner2', new FQName('inner1', null)))

        expect:
            FQName.toPath(fqn) == ['inner1', 'inner2', 'leaf']
    }

    def "FQName path with empty inner1"() {
        given:
            def fqn = new FQName('leaf', new FQName('inner2', ROOT))

        expect:
            FQName.toPath(fqn) == ['', 'inner2', 'leaf']
    }

    def "path parses to FQName"() {
        expect:
            FQName.of(path) == fqn

        where:
            path << [
                'inner1/inner2/leaf',
                '/inner'
            ]
            fqn << [
                new FQName('leaf', new FQName('inner2', new FQName('inner1', null))),
                new FQName('inner', ROOT)
            ]
    }

    def "div parses"() {
        expect:
            ROOT / 'inner' / 'leaf' == FQName.of('/inner/leaf')
    }
}
