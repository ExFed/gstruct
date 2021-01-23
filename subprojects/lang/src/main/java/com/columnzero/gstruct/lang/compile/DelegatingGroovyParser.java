package com.columnzero.gstruct.lang.compile;

import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import lombok.Value;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.IOException;

@Value
public class DelegatingGroovyParser {

    private static GroovyShell setupDelegatingShell() {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(DelegatingScript.class.getName());
        return new GroovyShell(config);
    }

    GroovyShell shell = setupDelegatingShell();

    public DelegatingScript parse(File file) throws CompilationFailedException, IOException {
        return (DelegatingScript) shell.parse(file);
    }

    public DelegatingScript parse(String source) {
        return (DelegatingScript) shell.parse(source);
    }
}
