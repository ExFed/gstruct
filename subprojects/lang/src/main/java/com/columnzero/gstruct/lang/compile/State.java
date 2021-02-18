package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.model.Identifier;
import com.columnzero.gstruct.model.NominalModel;
import groovy.lang.Closure;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
class State {

    @lombok.Builder
    public static State state(@NonNull Function<State, Map<String, Closure<?>>> keywordsInjector,
                              @NonNull Identifier.Name namespace,
                              NominalModel model) {

        final var state = new State(model != null ? model : new NominalModel(), namespace);

        // set up keywords
        state.scope.getKeywords().putAll(keywordsInjector.apply(state));

        return state;
    }

    @NonNull Queue<Runnable> actions = new LinkedList<>();
    @NonNull Scope scope = new Scope();

    @NonNull NominalModel model;
    @NonNull Identifier.Name namespace;
}
