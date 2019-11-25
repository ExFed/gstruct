package com.columnzero.gstruct

import org.codehaus.groovy.control.CompilerConfiguration

class DslMain {
    static def parse(File file) {
        def script = new GroovyShell(
            new CompilerConfiguration(
                scriptBaseClass:DelegatingScript.class.name
            )
        ).parse(file)

        def dsl = new DslMain()

        def sg = new StructGraph()
        def root = new NamedScope(Scopes.GLOBAL, sg)
        script.setDelegate(root)
        script.run()
        return sg
    }
}
