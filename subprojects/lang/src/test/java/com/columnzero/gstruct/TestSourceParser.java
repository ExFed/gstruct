package com.columnzero.gstruct;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilerConfiguration;

public class TestSourceParser {

    private static final DummyClosure PRIMITIVE = new DummyClosure();

    private static DelegatingScript makeDelegatingScript(String src) {
        final CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(DelegatingScript.class.getName());
        final Binding binding = new Binding();
        bindKeywords(binding);
        final GroovyShell shell = new GroovyShell(binding, config);
        return (DelegatingScript) shell.parse(src);
    }

    private static void bindKeywords(Binding binding) {
        binding.setVariable("primitive", PRIMITIVE);
    }

    private final String src;

    public TestSourceParser(String src) {
        this.src = src;
    }

    public void parse(Object delegate) {
        final DelegatingScript script = makeDelegatingScript(src);

        script.setDelegate(delegate);
        script.run();
    }

    private static class DummyClosure extends Closure<Void> {
        public DummyClosure() {
            super(null);
        }
    }
}
