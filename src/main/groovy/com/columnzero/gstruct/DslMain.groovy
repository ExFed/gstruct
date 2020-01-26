package com.columnzero.gstruct

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.ResolveVisitor

import com.columnzero.gstruct.graph.Graph

@groovy.transform.CompileStatic
class DslMain {
    static def parse(File file) {
        // HACK: this (seemingly) prevents (most) default imports (e.g. java.lang.String) but still
        // lets java.math.BigInteger and java.math.BigDecmial slip through!
        Arrays.fill(ResolveVisitor.DEFAULT_IMPORTS, "")

        def config = new CompilerConfiguration(scriptBaseClass: DelegatingScript.class.name)
        def script = new GroovyShell(config).parse(file) as DelegatingScript

        def rootContext = new GraphContext(new Graph(), Scopes.GLOBAL)
        def root = new DefaultNamespaceSpec(rootContext)
        script.setDelegate(root)
        script.run()
        return rootContext.graph
    }
}
