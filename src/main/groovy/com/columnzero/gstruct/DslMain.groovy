package com.columnzero.gstruct

import org.codehaus.groovy.control.CompilerConfiguration

import com.columnzero.gstruct.graph.Graph

class DslMain {
    static def parse(File file) {
        def script = new GroovyShell(
            new CompilerConfiguration(
                scriptBaseClass:DelegatingScript.class.name
            )
        ).parse(file)

        def rootContext = new GraphContext(new Graph(), Scopes.GLOBAL)
        def root = new DefaultNamespaceSpec(rootContext)
        script.setDelegate(root)
        script.run()
        return rootContext.graph
    }
}
