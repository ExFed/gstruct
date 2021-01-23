package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.model.Extern;
import com.columnzero.gstruct.model.NameBindings;
import com.columnzero.gstruct.model.Struct;
import com.columnzero.gstruct.model.Tuple;
import com.columnzero.gstruct.model.Type;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FirstParam;
import groovy.util.DelegatingScript;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.columnzero.gstruct.lang.compile.ClosureUtil.asClosure;
import static com.columnzero.gstruct.lang.compile.ClosureUtil.asListClosure;
import static com.columnzero.gstruct.model.Extern.extern;

public class BindingCompiler {

    private static <T, U> void with(U delegate, Closure<T> closure) {
        with(closure.getOwner(), delegate, closure);
    }

    private static <T, U> void with(
            Object owner,
            @DelegatesTo.Target("delegate") U delegate,
            @DelegatesTo(target = "delegate", strategy = Closure.DELEGATE_FIRST)
            @ClosureParams(FirstParam.class) Closure<T> closure) {

        final Closure<T> clonedClosure = closure.rehydrate(delegate, owner, delegate);
        clonedClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
        clonedClosure.call(delegate);
    }

    private static Function<Map<String, Type>, Void> binder(final Map<String, Type> bindings) {
        return mapping -> {
            Set<String> duplicates = mapping.keySet()
                                            .stream()
                                            .filter(bindings::containsKey)
                                            .collect(Collectors.toSet());
            if (!duplicates.isEmpty()) {
                throw new BindingException("cannot bind duplicate name: " + duplicates);
            }

            Logger.debug(() -> "binding " + mapping);
            bindings.putAll(mapping);
            return null;
        };
    }

    public static NameBindings compile(@NonNull File source) throws IOException {
        DelegatingScript script = new DelegatingGroovyParser().parse(source);

        return doCompile(delegate -> () -> {
            script.setDelegate(delegate);
            script.run();
        });
    }

    private static NameBindings doCompile(Function<Scope, Runnable> firstActionDelegator) {
        State compileState = new State(BindingCompiler::getDefaultKeywords);
        Runnable firstAction = firstActionDelegator.apply(compileState.getScope());
        Queue<Runnable> actions = compileState.getActions();

        actions.offer(firstAction); // get the traversal started
        while (!actions.isEmpty()) {
            actions.poll().run(); // traverse each syntax node
        }

        return compileState.getModel();
    }

    private static Map<String, Closure<?>> getDefaultKeywords(State state) {
        var scope = state.getScope();
        var bindings = state.getModel().getBindings();
        return Map.of(
                "bind", asClosure(scope, binder(bindings)),
                "extern", asClosure(scope, externCons()),
                "tuple", asClosure(scope, tupleCons(state)),
                "struct", asClosure(scope, structCons(state))
        );
    }

    private static Function<String, Extern> externCons() {
        return name -> {
            Logger.debug(() -> "initializing extern(" + name + ")");
            return extern(name);
        };
    }

    private static Function<Closure<?>, Tuple> tupleCons(State state) {
        return cl -> {
            Logger.debug("initializing tuple");
            var tuple = new Tuple();
            Runnable task = () -> {
                Logger.debug("configuring tuple");
                Scope scope = Scope.inherit(state.getScope(), Scope.of(state.getModel().getRefs()));
                Closure<List<Type>> typesAssigner = asListClosure(scope, (List<Type> t) -> {
                    tuple.getTypes().addAll(t);
                    return tuple.getTypes();
                });
                scope.getKeywords().put("types", typesAssigner);
                with(scope, cl);
            };
            state.getActions().offer(task);
            return tuple;
        };
    }

    private static Function<Closure<?>, Struct> structCons(State state) {
        return cl -> {
            Logger.debug("initializing struct");
            var struct = new Struct();
            Runnable task = () -> {
                Logger.debug("configuring struct");
                Scope scope = Scope.inherit(state.getScope(), Scope.of(state.getModel().getRefs()));
                Closure<Void> fieldAssigner = asClosure(scope, binder(struct.getFields()));
                scope.getKeywords().put("field", fieldAssigner);
                with(scope, cl);
            };
            state.getActions().offer(task);
            return struct;
        };
    }

    @Value
    private static class State {
        NameBindings model = new NameBindings();
        Queue<Runnable> actions = new LinkedList<>();
        Scope scope = new Scope();

        public State(Function<State, Map<String, Closure<?>>> keywordMapper) {
            // set up keywords
            scope.getKeywords().putAll(keywordMapper.apply(this));
        }
    }

    @AllArgsConstructor
    private static class Scope extends GroovyObjectSupport {

        @SuppressWarnings("unchecked")
        public static Scope of(Map<String, ?> properties) {
            return new Scope((Map<String, Object>) properties);
        }

        public static Scope inherit(Scope... scopes) {
            Map<String, Object> properties = new HashMap<>();
            for (Scope scope : scopes) {
                properties.putAll(scope.getKeywords());
            }
            return new Scope(properties);
        }

        @Getter
        private final @NonNull Map<String, Object> keywords;

        public Scope() {
            this(new HashMap<>());
        }

        @Override
        public Object getProperty(String property) {
            if (!keywords.containsKey(property)) {
                throw new MissingPropertyException("property not found: " + property);
            }
            return keywords.get(property);
        }

        @Override
        public void setProperty(String property, Object newValue) {
            throw new UnsupportedOperationException("cannot assign readonly property: " + property);
        }

        @Override
        public Object invokeMethod(String name, Object args) {
            var value = this.getProperty(name);
            if (value instanceof Closure) {
                Closure<?> closure = (Closure<?>) ((Closure<?>) value).clone();
                closure.setDelegate(this);
                return closure.call((Object[]) args);
            } else {
                Object[] argArray = args instanceof Object[]
                        ? (Object[]) args
                        : new Object[]{args};
                throw new MissingMethodException(name, Scope.class, argArray);
            }
        }
    }

    public static class BindingException extends RuntimeException {
        private BindingException(String message) {
            super(message);
        }
    }
}
