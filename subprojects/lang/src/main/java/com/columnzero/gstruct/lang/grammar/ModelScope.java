package com.columnzero.gstruct.lang.grammar;

import com.columnzero.gstruct.lang.compile.CompileAction;
import com.columnzero.gstruct.model.Identifier;
import com.columnzero.gstruct.model.NominalModel;
import com.columnzero.gstruct.model.Struct;
import com.columnzero.gstruct.model.Type;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import org.tinylog.Logger;

import java.util.Map;

@Data
public class ModelScope implements GroovyObject {

    @Delegate(types = GroovyObject.class)
    @Getter(AccessLevel.NONE)
    private final @NonNull GroovyObjectDelegate god = new GroovyObjectDelegate(ModelScope.class);

    private final @NonNull NominalModel model;
    private final @NonNull Identifier.Name namespace;

    public void bind(Map<String, Type> bindings) {
        bindings.forEach((id, type) -> model.bind(namespace.child(id), type));
    }

    public Struct struct(Closure<?> spec) {
        Logger.debug("initializing struct");
        var structBuilder = Struct.builder();
        CompileAction action = () -> {
            Logger.debug("configuring struct");
            Object scope = MixinScope.builder().scope(structBuilder).build();
        };

        throw new UnsupportedOperationException("TODO");
    }
}

