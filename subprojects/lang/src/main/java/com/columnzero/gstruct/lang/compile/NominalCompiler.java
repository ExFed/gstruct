package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.SourceFile;
import com.columnzero.gstruct.SourceTree;
import com.columnzero.gstruct.model.Extern;
import com.columnzero.gstruct.model.Identifier;
import com.columnzero.gstruct.model.Identifier.Name;
import com.columnzero.gstruct.model.NominalModel;
import com.columnzero.gstruct.model.Ref;
import com.columnzero.gstruct.model.Struct;
import com.columnzero.gstruct.model.Tuple;
import com.columnzero.gstruct.model.Type;
import com.columnzero.gstruct.util.Path;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FirstParam;
import groovy.util.DelegatingScript;
import io.vavr.Tuple2;
import lombok.NonNull;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.columnzero.gstruct.lang.compile.ClosureUtil.asClosure;
import static com.columnzero.gstruct.lang.compile.ClosureUtil.asListClosure;
import static com.columnzero.gstruct.model.Extern.extern;
import static com.columnzero.gstruct.model.Identifier.local;
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

    private static Consumer<Map<String, Ref<Type>>> binder(State state) {
        var namespace = state.getNamespace();
        var model = state.getModel();
        return binder((s, typeRef) -> model.bind(namespace.child(local(s)), typeRef));
    }

    public static NominalModel compile(@NonNull SourceTree tree) throws IOException {

        var model = new NominalModel();
        for (Tuple2<Path<String>, SourceFile> entry : tree.mapByNamespace()) {
            doCompile(entry._2.getFile(), Identifier.name(entry._1), model);
        }
        return model;
    }

    @lombok.Builder(builderMethodName = "configure",
                    buildMethodName = "compile",
                    builderClassName = "Config")
    public static NominalModel compile(@NonNull File source, @NonNull Name namespace)
            throws IOException {

        return doCompile(source, namespace, new NominalModel());
    }

    private static NominalModel doCompile(File source, Name namespace, NominalModel model)
            throws IOException {
        DelegatingScript script = new DelegatingGroovyParser().parse(source);

        State compileState = State.builder()
                                  .keywordsInjector(NominalCompiler::getDefaultKeywords)
                                  .namespace(namespace)
                                  .model(model)
                                  .build();

        Runnable firstAction = () -> {
            script.setDelegate(compileState.getScope());
            script.run();
        };

        Queue<Runnable> actions = compileState.getActions();

        actions.offer(firstAction); // get the traversal started
        while (!actions.isEmpty()) {
            actions.poll().run(); // traverse each syntax node
        }

        return compileState.getModel();
    }

    private static Map<String, Closure<?>> getDefaultKeywords(State state) {
        var scope = state.getScope();
        return Map.of(
                "bind", asClosure(scope, binder(state)),
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
                final var namedRefs = refBindings(state);
                Scope scope = Scope.inherit(state.getScope(),
                                            Scope.of(namedRefs));
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
                Scope scope = Scope.inherit(state.getScope(), Scope.of(refBindings(state)));
                Closure<Void> fieldAssigner = asClosure(scope, binder(struct::field));
                scope.getKeywords().put("field", fieldAssigner);
                with(scope, cl);
            };
            state.getActions().offer(task);
            return ref(struct::build);
        };
    }

    private static Map<String, Ref<Type>> refBindings(State state) {
        return state.getModel()
                    .getNameRefs()
                    .toJavaMap(nr -> io.vavr.Tuple.of(
                            nr.getName().getPath().getValue().getId(), nr));
    }
}
