package com.columnzero.gstruct;

import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.util.Set;

public class SourceParser {
    public static Set<String> parseNameDeclarations(String src) {
        final CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(DelegatingScript.class.getName());
        final DelegatingScript script = (DelegatingScript) new GroovyShell(config).parse(src);

        final DeclarationScraper declScraper = new DeclarationScraper();
        script.setDelegate(declScraper);
        script.run();
        return declScraper.getNames();
    }
}
