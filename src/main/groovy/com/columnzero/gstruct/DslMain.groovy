package com.columnzero.gstruct

import org.codehaus.groovy.control.CompilerConfiguration

class DslMain {
    static def parse(File file) {
        def script = new GroovyShell(
            new CompilerConfiguration(
                scriptBaseClass:DelegatingScript.class.name
            )
        ).parse(file)

        def rootContext = new GraphContext(new StructGraph(), Scopes.GLOBAL)
        def root = new DefaultNamespaceSpec(rootContext)
        script.setDelegate(root)
        script.run()
        return rootContext.graph
    }
}
