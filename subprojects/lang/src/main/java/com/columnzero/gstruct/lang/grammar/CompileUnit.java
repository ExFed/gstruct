package com.columnzero.gstruct.lang.grammar;

import com.columnzero.gstruct.lang.compile.ClosureUtil;
import com.columnzero.gstruct.model.Identifier;
import com.columnzero.gstruct.model.NominalModel;
import com.columnzero.gstruct.model.Type;
import groovy.lang.Closure;
import groovy.util.Expando;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.columnzero.gstruct.model.Extern.extern;

@AllArgsConstructor
public final class CompileUnit extends Expando {

    @lombok.Builder(access = AccessLevel.PRIVATE,
                    builderClassName = "KeywordMapBuilder",
                    builderMethodName = "kwBuilder")
    private static Map<String, Object> keywords(@Singular Map<String, Object> keywords) {
        keywords.forEach((k, v) -> Identifier.local(k));
        return keywords;
    }

    private final @NonNull NominalModel model;
    private final @NonNull Identifier.Name namespace;

    private final @NonNull Map<String, Object> userKeywords = new HashMap<>();

    @Getter
    private final @NonNull Map<String, Object> keywords =
            kwBuilder().keyword("bind", asClosure(this::bind))
                       .keyword("extern", asClosure((String name) -> {
                           Logger.debug(() -> "initializing extern(" + name + ")");
                           return extern(name);
                       }))
                       .build();


    public void bind(Map<String, Type> bindings) {
        bindings.forEach((id, type) -> model.bind(namespace.child(id), type));
    }

    public Object typeSpecifier(Map<String, Object> typeKeywords) {
        var kws = new HashMap<>(typeKeywords);
        return new Expando(kws);
    }



    private <T> Closure<Void> asClosure(Consumer<T> fn) {
        return ClosureUtil.asClosure(this, fn);
    }

    private <T, R> Closure<R> asClosure(Function<T, R> fn) {
        return ClosureUtil.asClosure(this, fn);
    }
}
