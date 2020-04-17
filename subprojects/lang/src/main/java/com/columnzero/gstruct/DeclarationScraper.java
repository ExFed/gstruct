package com.columnzero.gstruct;

import com.columnzero.gstruct.lang.grammar.*;
import com.columnzero.gstruct.util.Path;
import groovy.lang.Closure;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeclarationScraper
        implements FieldSpec, FileSpec, PackageSpec, StructSpec, DocumentationSpec {

    private final Path<String> namespace;

    private final Set<String> typedefs = new LinkedHashSet<>();
    private final Set<String> structs = new LinkedHashSet<>();
    private final Set<String> fields = new LinkedHashSet<>();

    public DeclarationScraper(Path<String> namespace) {
        this.namespace = namespace;
    }

    public Set<Path<String>> getAllDeclarations() {
        final LinkedHashSet<String> decls = new LinkedHashSet<>();
        decls.addAll(typedefs);
        decls.addAll(structs);
        decls.addAll(fields);
        return decls.stream().map(namespace::child).collect(Collectors.toSet());
    }

    @Override
    public void typedef(Map<String, Closure<RefSpec>> typeDef) {
        typedefs.addAll(typeDef.keySet());
    }

    @Override
    public void struct(Map<String, Closure<StructSpec>> structDef) {
        structs.addAll(structDef.keySet());
    }

    @Override
    public void field(Map<String, Closure<FieldSpec>> fieldDef) {
        fields.addAll(fieldDef.keySet());
    }

    @Override
    public void include(String included) {
        // noop
    }

    @Override
    public void inherit(RefSpec id) {
        // noop
    }

    @Override
    public void type(RefSpec spec) {
        // noop
    }

    @Override
    public void setDescription(String description) {
        // noop
    }
}
