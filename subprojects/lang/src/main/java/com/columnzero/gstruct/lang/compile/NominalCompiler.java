package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.SourceFile;
import com.columnzero.gstruct.model.Extern;
import com.columnzero.gstruct.model.NominalModel;
import com.columnzero.gstruct.model.Ref;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.columnzero.gstruct.lang.compile.ClosureUtil.asClosure;
import static com.columnzero.gstruct.lang.compile.ClosureUtil.asListClosure;
import static com.columnzero.gstruct.model.Extern.extern;
import static com.columnzero.gstruct.model.Ref.constRef;
import static com.columnzero.gstruct.model.Ref.ref;

public class NominalCompiler {

    private NominalCompiler() {
        throw new AssertionError("not instantiable");
    }

    private static <T, U> void with(U delegate, Closure<T> closure) {
        with(closure.getOwner(), delegate, closure);
    }

    private static <T, U> void with(
            Object owner,
            @DelegatesTo.Target("delegate") U delegate,
            @DelegatesTo(target = "delegate", strategy = Closure.DELEGATE_ONLY)
            @ClosureParams(FirstParam.class) Closure<T> closure) {

        final Closure<T> clonedClosure = closure.rehydrate(delegate, owner, delegate);
        clonedClosure.setResolveStrategy(Closure.DELEGATE_ONLY);
        clonedClosure.call(delegate);
    }

    private static Consumer<Map<String, Ref<Type>>> binder(BiConsumer<String, Ref<Type>> bindFunc) {
        return mapping -> mapping.forEach(bindFunc);
    }

    public static NominalModel compile(@NonNull File source) throws IOException {
        DelegatingScript script = new DelegatingGroovyParser().parse(source);

        return doCompile(delegate -> () -> {
            script.setDelegate(delegate);
            script.run();
        });
    }

    public static NominalModel compile(@NonNull SourceFile source) throws IOException {
        return compile(source.getFile());
    }

    private static NominalModel doCompile(Function<Scope, Runnable> firstActionDelegator) {
        State compileState = new State(NominalCompiler::getDefaultKeywords);
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
        var model = state.getModel();
        return Map.of(
                "bind", asClosure(scope, binder(model::bind)),
                "extern", asClosure(scope, externCons()),
                "tuple", asClosure(scope, tupleCons(state)),
                "struct", asClosure(scope, structCons(state))
        );
    }

    private static Function<String, Ref<Extern>> externCons() {
        return name -> {
            Logger.debug(() -> "initializing extern(" + name + ")");
            return constRef(extern(name));
        };
    }

    private static Function<Closure<?>, Ref<Tuple>> tupleCons(State state) {
        return cl -> {
            Logger.debug("initializing tuple");
            var tupleBuilder = Tuple.builder();
            Runnable task = () -> {
                Logger.debug("configuring tuple");
                Scope scope = Scope.inherit(state.getScope(),
                                            Scope.of(state.getModel().getNamedRefs()));
                Closure<Void> typesAssigner =
                        asListClosure(scope, (List<Ref<Type>> t) -> t.forEach(tupleBuilder::type));
                scope.getKeywords().put("types", typesAssigner);
                with(scope, cl);
            };
            state.getActions().offer(task);
            return ref(tupleBuilder::build);
        };
    }

    private static Function<Closure<?>, Ref<Struct>> structCons(State state) {
        return cl -> {
            Logger.debug("initializing struct");
            var struct = Struct.builder();
            Runnable task = () -> {
                Logger.debug("configuring struct");
                Scope scope = Scope.inherit(state.getScope(),
                                            Scope.of(state.getModel().getNamedRefs()));
                Closure<Void> fieldAssigner = asClosure(scope, binder(struct::field));
                scope.getKeywords().put("field", fieldAssigner);
                with(scope, cl);
            };
            state.getActions().offer(task);
            return ref(struct::build);
        };
    }

    @Value
    private static class State {
        NominalModel model = new NominalModel();
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
            Map<String, Object> keywords = new HashMap<>();
            for (Scope scope : scopes) {
                keywords.putAll(scope.getKeywords());
            }
            return new Scope(keywords);
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
                Closure<?> cl = (Closure<?>) value;
                Closure<?> clonedClosure = cl.rehydrate(this, cl.getOwner(), this);
                clonedClosure.setResolveStrategy(Closure.DELEGATE_ONLY);
                return clonedClosure.call((Object[]) args);
            } else {
                Object[] argArray = args instanceof Object[]
                        ? (Object[]) args
                        : new Object[]{args};
                throw new MissingMethodException(name, Scope.class, argArray);
            }
        }

        @Override
        public String toString() {
            return "Scope(" + keywords + ')';
        }
    }
}
