package com.columnzero.gstruct.graph

import com.columnzero.gstruct.glang.FQName
import spock.lang.Specification

class FQNameTest extends Specification {

    def "FQName path"() {
        given:
        def fqn = new FQName('leaf', new FQName('inner2', new FQName('inner1', null)))

        expect:
        fqn.toPath() == ['inner1', 'inner2', 'leaf']
    }

    def "FQName path with empty inner1"() {
        given:
        def fqn = new FQName('leaf', new FQName('inner2', FQName.ROOT))

        expect:
        fqn.toPath() == ['', 'inner2', 'leaf']
    }

    def "absolute path parses"() {
        expect:
            FQName.of(path) == fqn

        where:
        path << [
            '/inner1/inner2/leaf',
            '/inner',
            '/',
        ]
        fqn << [
            new FQName('leaf', new FQName('inner2', new FQName('inner1', FQName.ROOT))),
            new FQName('inner', FQName.ROOT),
            FQName.ROOT,
        ]
    }

    def "cannot statically parse relative path"() {
        when:
        FQName.of('not/an/absolute')

        then:
        thrown(IllegalArgumentException)
    }

    def "cannot statically parse empty path"() {
        when:
        FQName.of('')

        then:
        thrown(IllegalArgumentException)
    }

    def "div parses"() {
        expect:
            FQName.ROOT / 'upper' / 'inner' / 'leaf' == FQName.of('/upper/inner/leaf')
    }
}
