package com.columnzero.gstruct;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class TestSourceParser {

    private static final DummyClosure PRIMITIVE = new DummyClosure();

    public static TestSourceParser withSource(String src) {
        return new TestSourceParser(setupGroovyShell().parse(src));
    }

    public static TestSourceParser withSource(File file) throws IOException {
        return new TestSourceParser(setupGroovyShell().parse(file));
    }

    private static GroovyShell setupGroovyShell() {
        final CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(DelegatingScript.class.getName());
        final Binding binding = new Binding();
        bindKeywords(binding);
        return new GroovyShell(binding, config);
    }

    private static void bindKeywords(Binding binding) {
        binding.setVariable("primitive", PRIMITIVE);
    }

    private final DelegatingScript script;

    private TestSourceParser(Script script) {
        this.script = (DelegatingScript) script;
    }

    public Binding getBinding() {
        return script.getBinding();
    }

    public <T> T run(T delegate) {
        synchronized (script) {
            script.setDelegate(delegate);
            script.run();
            return delegate;
        }
    }

    public <T> T run(Function<Binding, T> delegateFactory) {
        return run(delegateFactory.apply(getBinding()));
    }

    private static class DummyClosure extends Closure<Void> {
        public DummyClosure() {
            super(null);
        }
    }
}
