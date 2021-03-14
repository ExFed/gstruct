package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.model.Identifier;
import com.columnzero.gstruct.model.NominalModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@With
@Value
class CompileContext {

    @NonNull NominalModel model;
    @NonNull Identifier.Name namespace;

    @lombok.Builder.Default
    @NonNull Queue<CompileAction> actions = new LinkedList<>();

    @lombok.Builder.Default
    @NonNull Scope scope = new Scope(new HashMap<>());
}

