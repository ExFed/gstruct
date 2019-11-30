package com.columnzero.gstruct

import spock.lang.Specification

class FQNameTest extends Specification {

    def "FQName path"() {
        given:
            def cn = new FQName('leaf', new FQName('inner', new FQName('root', null)))

        expect:
            FQName.toPath(cn) == ['root', 'inner', 'leaf']
    }

    def "FQName path with empty root"() {
        given:
            def cn = new FQName('leaf', new FQName('inner', new FQName('', null)))

        expect:
            FQName.toPath(cn) == ['', 'inner', 'leaf']
    }

    def "path parses to FQName"() {
        expect:
            FQName.of(path) == cn

        where:
            path << ['root/inner/leaf', '/root']
            cn << [
                new FQName('leaf', new FQName('inner', new FQName('root', null))),
                new FQName('root', new FQName('', null))
            ]
    }
}
