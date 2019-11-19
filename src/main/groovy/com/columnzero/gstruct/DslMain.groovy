package com.columnzero.gstruct

import org.codehaus.groovy.control.CompilerConfiguration

import com.columnzero.gstruct.dsl.StructSpec

class DslMain {
    static StructSpec parse(File file) {
        def script = new GroovyShell(
            new CompilerConfiguration(
                scriptBaseClass:DelegatingScript.class.name
            )
        ).parse(file)

        def dsl = new DslMain()
        script.setDelegate(dsl.root)
        script.run()
        return dsl.root
    }

    StructSpec root = new StructSpec(null)
}
