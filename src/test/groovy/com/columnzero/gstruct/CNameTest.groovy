package com.columnzero.gstruct

import spock.lang.Specification

class CNameTest extends Specification {

    def "CName path"() {
        given:
            def cn = new CName('leaf', new CName('inner', new CName('root', null)))

        expect:
            CName.toPath(cn) == ['root', 'inner', 'leaf']
    }

    def "CName path with empty root"() {
        given:
            def cn = new CName('leaf', new CName('inner', new CName('', null)))

        expect:
            CName.toPath(cn) == ['', 'inner', 'leaf']
    }

    def "path parses to CName"() {
        expect:
            CName.of(path) == cn

        where:
            path << ['root/inner/leaf', '/root']
            cn << [
                new CName('leaf', new CName('inner', new CName('root', null))),
                new CName('root', new CName('', null))
            ]
    }
}
