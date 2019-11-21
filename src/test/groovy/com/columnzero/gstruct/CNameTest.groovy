package com.columnzero.gstruct

import spock.lang.Specification

class CNameTest extends Specification {
    def "path"() {
        given:
            def cn = new CName('leaf', new CName('inner', new CName('root', null)))

        expect:
            cn.path == ['root', 'inner', 'leaf']
    }

    def "path empty root"() {
        given:
            def cn = new CName('leaf', new CName('inner', new CName('', null)))

        expect:
            cn.path == ['', 'inner', 'leaf']
    }
}
