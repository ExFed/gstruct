package com.columnzero.gstruct.lang;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import com.columnzero.gstruct.lang.grammar.RefSpec;
import com.columnzero.gstruct.util.Path;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Parser<S> {

    public static Parser<String> withSource(String src) {
        return new Parser<>((DelegatingScript) setupGroovyShell().parse(src), src);
    }

    public static Parser<File> withSource(File file) throws IOException {
        return new Parser<>((DelegatingScript) setupGroovyShell().parse(file), file);
    }

    private static GroovyShell setupGroovyShell() {
        final CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(DelegatingScript.class.getName());
        return new GroovyShell(config);
    }

    private final @NonNull DelegatingScript script;
    private final @NonNull S source;

    public Binding getBinding() {
        return script.getBinding();
    }

    /**
     * Runs the script against the given delegate.
     *
     * @param delegate Delegate object to run the script against.
     * @param <T>      Type of the delegate object.
     *
     * @return The delegate object.
     */
    public <T> T run(T delegate) {
        synchronized (script) {
            script.setDelegate(delegate);
            script.run();
            return delegate;
        }
    }

    /**
     * Runs the script and binding against a delegate produced by the given factory.
     *
     * @param delegateFactory Factory that provides a delegate with the script binding.
     * @param <T>             Type of the delegate object.
     *
     * @return The delegate object.
     */
    public <T> T run(Function<Binding, T> delegateFactory) {
        return run(delegateFactory.apply(getBinding()));
    }

    private static class DummyClosure extends Closure<Void> {
        public DummyClosure() {
            super(null);
        }

        @Override
        public Void call() {
            return null;
        }

        @Override
        public Void call(Object... args) {
            return null;
        }

        @Override
        public Void call(Object arguments) {
            return null;
        }
    }
}
