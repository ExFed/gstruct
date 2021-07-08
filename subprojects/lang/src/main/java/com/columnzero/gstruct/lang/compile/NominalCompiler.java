package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.model.Extern;
import com.columnzero.gstruct.model.Identifier.Name;
import com.columnzero.gstruct.model.NominalModel;
import com.columnzero.gstruct.model.Struct;
import com.columnzero.gstruct.model.Tuple;
import com.columnzero.gstruct.model.Type;
import com.columnzero.gstruct.model.Type.Ref;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FirstParam;
import groovy.util.DelegatingScript;
import lombok.NonNull;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.columnzero.gstruct.lang.compile.ClosureUtil.asClosure;
import static com.columnzero.gstruct.lang.compile.ClosureUtil.asListClosure;
import static com.columnzero.gstruct.model.Extern.extern;
import static com.columnzero.gstruct.model.Identifier.local;
import static com.columnzero.gstruct.model.Type.constRef;
import static com.columnzero.gstruct.model.Type.ref;

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

    private static Consumer<Map<String, Ref<Type>>> binder(BiConsumer<String, Type> bindFunc) {
        return mapping -> mapping.forEach(bindFunc);
    }

    private static Consumer<Map<String, Ref<Type>>> binder(CompileContext context) {
        var namespace = context.getNamespace();
        var model = context.getModel();
        return binder((s, typeRef) -> model.bind(namespace.child(local(s)), typeRef));
    }

    public static NominalModel compile(@NonNull File source, @NonNull Name namespace)
            throws IOException {

        CompileContext context = CompileContext.builder()
                                               .namespace(namespace)
                                               .model(new NominalModel())
                                               .build();

        var actions = context.getActions();
        var visitFile = visitFileAction(source, context);

        actions.offer(visitFile); // get the traversal started

        compile(context);

        return context.getModel();
    }

    private static void compile(CompileContext context) throws IOException {
        final var defaultKeywords = getDefaultKeywords(context);
        context.getScope().getKeywords().putAll(defaultKeywords);

        final var actions = context.getActions();
        while (!actions.isEmpty()) {
            actions.poll().execute(); // traverse each syntax node
        }
    }

    private static CompileAction visitFileAction(File source, CompileContext context) {
        final var scope = context.getScope();
        return () -> {
            DelegatingScript script = new DelegatingGroovyParser().parse(source);
            script.setDelegate(scope);
            script.run();
        };
    }

    private static Map<String, Object> getDefaultKeywords(CompileContext context) {
        var scope = context.getScope();
        return Map.of(
                "bind", asClosure(scope, binder(context)),
                "extern", asClosure(scope, externCons()),
                "tuple", asClosure(scope, tupleCons(context)),
                "struct", asClosure(scope, structCons(context))
        );
    }

    private static Function<String, Extern> externCons() {
        return name -> {
            Logger.debug(() -> "initializing extern(" + name + ")");
            return extern(name);
        };
    }

    private static Function<Closure<?>, Ref<Tuple>> tupleCons(CompileContext context) {
        return cl -> {
            Logger.debug("initializing tuple");
            var tupleBuilder = Tuple.builder();
            CompileAction task = () -> {
                Logger.debug("configuring tuple");
                final var namedRefs = refBindings(context);
                Scope scope = Scope.inherit(context.getScope(),
                                            Scope.of(namedRefs));
                Object typesAssigner =
                        asListClosure(scope, (List<Type> t) -> t.forEach(tupleBuilder::type));
                scope.getKeywords().put("types", typesAssigner);
                with(scope, cl);
            };
            context.getActions().offer(task);
            return ref(tupleBuilder::build);
        };
    }

    private static Function<Closure<?>, Ref<Struct>> structCons(CompileContext context) {
        return cl -> {
            Logger.debug("initializing struct");
            var structBuilder = Struct.builder();
            CompileAction task = () -> {
                Logger.debug("configuring struct");
                Scope scope = Scope.inherit(context.getScope(), Scope.of(refBindings(context)));
                Closure<Void> fieldAssigner = asClosure(scope, binder(structBuilder::field));
                scope.getKeywords().put("field", fieldAssigner);
                with(scope, cl);
            };
            context.getActions().offer(task);
            return ref(structBuilder::build);
        };
    }

    private static Map<String, Ref<Type>> refBindings(CompileContext context) {
        return context.getModel()
                      .getNameRefs()
                      .toJavaMap(nr -> io.vavr.Tuple.of(
                              nr.getName().getPath().getValue().getId(), nr));
    }
}
