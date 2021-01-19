package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.model.Extern;
import com.columnzero.gstruct.model.NameBindings;
import com.columnzero.gstruct.model.Struct;
import com.columnzero.gstruct.model.Tuple;
import com.columnzero.gstruct.model.Type;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FirstParam;
import groovy.util.DelegatingScript;
import groovy.util.Expando;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private static <T, R> Closure<R> asClosure(Object owner, Function<T, R> function) {
        return new Closure<R>(owner) {
            @SuppressWarnings("unused")
            public R doCall(T arg) {
                return function.apply(arg);
            }
        };
    }

    private static <T, R> Closure<R> asListClosure(Object owner, Function<List<T>, R> function) {
        return new Closure<R>(owner) {
            @SuppressWarnings({"unused", "unchecked"})
            public final R doCall(Object... args) {
                List<T> argList = Arrays.stream(args).map(a -> (T) a).collect(Collectors.toList());
                return function.apply(argList);
            }
        };
    }

    public static NameBindings compile(@NonNull File source) throws IOException {
        DelegatingScript script = new DelegatingGroovyParser().parse(source);

        return doCompile(delegate -> () -> {
            script.setDelegate(delegate);
            script.run();
        });
    }

    private static NameBindings doCompile(Function<Flexi, Runnable> firstActionDelegator) {
        CompilerState state = new CompilerState();
        Runnable firstAction = firstActionDelegator.apply(state.getScope());
        Queue<Runnable> actions = state.getActions();

        actions.offer(firstAction); // get the traversal started
        while (!actions.isEmpty()) {
            actions.poll().run(); // traverse each syntax node
        }

        return state.getModel();
    }

    private static Function<String, Extern> externCons() {
        return (String name) -> {
            Logger.debug(() -> "initializing extern(" + name + ")");
            return extern(name);
        };
    }

    @SuppressWarnings("unchecked")
    private static Function<Closure<?>, Tuple> tupleCons(Queue<Runnable> actions, NameBindings model) {
        return cl -> {
            Logger.debug("initializing tuple");
            var tuple = new Tuple();
            Runnable task = () -> {
                Logger.debug("configuring tuple");
                Flexi scope = new Flexi(model.getRefs(), true);
                Closure<List<Type>> typesAssigner = asListClosure(scope, (List<Type> t) -> {
                    tuple.getTypes().addAll(t);
                    return tuple.getTypes();
                });
                scope.getProperties().put("types", typesAssigner);
                with(scope, cl);
            };
            actions.offer(task);
            return tuple;
        };
    }

    @SuppressWarnings("unchecked")
    private static Function<Closure<?>, Struct> structCons(Queue<Runnable> actions, NameBindings model) {
        return cl -> {
            Logger.debug("initializing struct");
            var struct = new Struct();
            Runnable task = () -> {
                Logger.debug("configuring struct");
                Flexi scope = new Flexi(model.getRefs(), true);
                Closure<Void> fieldAssigner = asClosure(scope, binder(struct.getFields()));
                scope.getProperties().put("field", fieldAssigner);
                with(scope, cl);
            };
            actions.offer(task);
            return struct;
        };
    }

    @Value
    private static class CompilerState {
        NameBindings model = new NameBindings();
        Queue<Runnable> actions = new LinkedList<>();

        Flexi scope = new Flexi(false);

        @SuppressWarnings("unchecked")
        public CompilerState() {
            // set up keywords
            scope.getProperties().putAll(Map.of(
                    "bind", asClosure(scope, binder(model.getBindings())),
                    "extern", asClosure(scope, externCons()),
                    "tuple", asClosure(scope, tupleCons(actions, model)),
                    "struct", asClosure(scope, structCons(actions, model))
            ));
        }

    }

    @AllArgsConstructor
    private static class Flexi extends Expando {

        private final boolean useSuper;

        public Flexi(Map<?, ?> propertyMap, boolean useSuper) {
            super(propertyMap);
            this.useSuper = useSuper;
        }

        @Override
        public Object getProperty(String property) {
            var properties = super.getProperties();
            if (!properties.containsKey(property)) {
                if (useSuper) {
                    return super.getProperty(property);
                }
                throw new MissingPropertyException("property not found: " + property);
            }
            return properties.get(property);
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
                if (useSuper) {
                    return super.invokeMethod(name, args);
                } else {
                    Object[] argArray = args instanceof Object[]
                            ? (Object[]) args
                            : new Object[]{args};
                    throw new MissingMethodException(name, Flexi.class, argArray);
                }
            }
        }
    }

    public static class BindingException extends RuntimeException {
        private BindingException(String message) {
            super(message);
        }
    }
}
