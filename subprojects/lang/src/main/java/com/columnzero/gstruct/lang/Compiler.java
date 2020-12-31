package com.columnzero.gstruct.lang;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilerConfiguration;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import io.vavr.Tuple2;
import io.vavr.collection.Queue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Compiler {

    public static Compiler withSource(String src) {
        return new Compiler((DelegatingScript) setupGroovyShell().parse(src), src);
    }

    public static Compiler withSource(File file) throws IOException {
        return new Compiler((DelegatingScript) setupGroovyShell().parse(file), file);
    }

    private static GroovyShell setupGroovyShell() {
        final CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(DelegatingScript.class.getName());
        return new GroovyShell(config);
    }

    private final @NonNull DelegatingScript script;
    private final @NonNull Object source;

    private final @NonNull Context context = new Context();

    public <T> T configure(T delegate) {
        synchronized (script) {
            script.setDelegate(delegate);
            script.run();
            context.visitConfigurers();
        }
        return delegate;
    }

    public static class Context {

        private @NonNull Search<Closure<?>> configurers = BreadthFirst.empty();

        public <T> T configure(T delegate, Closure<?> configurer) {
            final var cl = configurer.rehydrate(delegate, delegate, delegate);
            cl.setResolveStrategy(Closure.DELEGATE_ONLY);
            configurers = configurers.put(cl);
            return delegate;
        }

        private void visitConfigurers() {
            while (!configurers.isDone()) {
                var next = configurers.get();
                configurers = next._2;
                next._1.call();
            }
        }
    }

    private interface Search<T> {
        Search<T> put(T element);

        Tuple2<T, ? extends Search<T>> get();

        boolean isDone();
    }

    @AllArgsConstructor
    private static class BreadthFirst<T> implements Search<T> {

        public static <T> BreadthFirst<T> empty() {
            return new BreadthFirst<>(Queue.empty());
        }

        private final @NonNull Queue<T> nodes;

        @Override
        public BreadthFirst<T> put(T element) {
            return new BreadthFirst<>(nodes.enqueue(element));
        }

        @Override
        public Tuple2<T, BreadthFirst<T>> get() {
            return nodes.dequeue().map2(BreadthFirst::new);
        }

        @Override
        public boolean isDone() {
            return nodes.isEmpty();
        }
    }
}
