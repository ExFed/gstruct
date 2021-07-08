package com.columnzero.gstruct.model;

import com.columnzero.gstruct.model.Identifier.Name;
import io.vavr.collection.HashSet;
import io.vavr.collection.TreeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.columnzero.gstruct.model.Extern.extern;
import static com.columnzero.gstruct.model.Identifier.name;
import static com.columnzero.gstruct.model.Type.constRef;
import static com.columnzero.gstruct.model.Struct.struct;
import static com.columnzero.gstruct.model.Tuple.tuple;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NominalModelTest {

    public static final String[] UNIT_PATH = {"Unit"};
    public static final Name UNIT_NAME = name(UNIT_PATH);
    public static final Extern VOID_EXTERN = extern("void");

    public static final String[] TUPLE0_PATH = {"Tuple0"};
    public static final Name TUPLE0_NAME = name(TUPLE0_PATH);
    public static final Tuple ZERO_TUPLE = tuple();

    public static final String[] OBJECT_PATH = {"Object"};
    public static final Name OBJECT_NAME = name(OBJECT_PATH);
    public static final Struct EMPTY_STRUCT = struct();

    NominalModel model;
    private TreeMap<Name, Type.Ref<? extends Type>> expectBindings;

    @BeforeEach
    void setUp() {
        model = new NominalModel();

        expectBindings = TreeMap.of(UNIT_NAME, constRef(VOID_EXTERN),
                                    TUPLE0_NAME, constRef(ZERO_TUPLE),
                                    OBJECT_NAME, constRef(EMPTY_STRUCT));
    }

    @Test
    void getNameRefs() {
        final var expect =
                HashSet.of(model.bind(VOID_EXTERN).to(UNIT_NAME),
                           model.bind(ZERO_TUPLE).to(TUPLE0_NAME),
                           model.bind(EMPTY_STRUCT).to(OBJECT_NAME));

        assertThat(model.getNameRefs()).containsExactlyElementsIn(expect);
        assertThat(model.getBindings()).containsExactlyElementsIn(expectBindings);
    }

    @Test
    void bind() {
        model.bind(UNIT_NAME, constRef(VOID_EXTERN));
        model.bind(TUPLE0_NAME, constRef(ZERO_TUPLE));
        model.bind(OBJECT_NAME, constRef(EMPTY_STRUCT));

        assertThat(model.getBindings()).containsExactlyElementsIn(expectBindings);
    }

    @Test
    void binderByType() {
        model.bind(VOID_EXTERN).to(UNIT_PATH);
        model.bind(ZERO_TUPLE).to(TUPLE0_PATH);
        model.bind(EMPTY_STRUCT).to(OBJECT_PATH);

        assertThat(model.getBindings()).containsExactlyElementsIn(expectBindings);
    }

    @Test
    void binderByRef() {
        model.bind(VOID_EXTERN).to(UNIT_PATH);
        model.bind(ZERO_TUPLE).to(TUPLE0_PATH);
        model.bind(EMPTY_STRUCT).to(OBJECT_PATH);

        assertThat(model.getBindings()).containsExactlyElementsIn(expectBindings);
    }

    @Test
    void duplicate() {
        model.bind(UNIT_NAME, constRef(VOID_EXTERN));
        assertThrows(DuplicateBindingException.class,
                     () -> model.bind(UNIT_NAME, constRef(VOID_EXTERN)));
    }

    @Test
    void missing() {
        assertThrows(BindingNotFoundException.class, model.ref(Identifier.name("missing"))::get);
    }
}
