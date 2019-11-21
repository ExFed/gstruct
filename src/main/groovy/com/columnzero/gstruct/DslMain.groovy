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
        script.setDelegate(dsl.root)
        script.run()
        return StructGraph.sg
    }

    Scope root = new Scope()
}
