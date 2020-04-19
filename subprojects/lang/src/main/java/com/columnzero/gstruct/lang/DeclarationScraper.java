package com.columnzero.gstruct.lang;

import com.columnzero.gstruct.lang.grammar.DocumentationSpec;
import com.columnzero.gstruct.lang.grammar.FieldSpec;
import com.columnzero.gstruct.lang.grammar.FileSpec;
import com.columnzero.gstruct.lang.grammar.PackageSpec;
import com.columnzero.gstruct.lang.grammar.RefSpec;
import com.columnzero.gstruct.lang.grammar.StructSpec;
import com.columnzero.gstruct.util.Path;
import groovy.lang.Closure;
import org.tinylog.Logger;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeclarationScraper implements FieldSpec,
                                           FileSpec,
                                           PackageSpec,
                                           StructSpec,
                                           DocumentationSpec {

    private final Path<String> namespace;

    private final Set<String> names = new LinkedHashSet<>();

    public DeclarationScraper(Path<String> namespace) {
        this.namespace = namespace;
    }

    public Set<Path<String>> getAllDeclarations() {
        return names.stream().map(namespace::child).collect(Collectors.toSet());
    }

    @Override
    public void typedef(Map<String, Closure<RefSpec>> typeDef) {
        addNames(typeDef.keySet());
    }

    @Override
    public void struct(Map<String, Closure<StructSpec>> structDef) {
        addNames(structDef.keySet());
    }

    @Override
    public void field(Map<String, Closure<FieldSpec>> fieldDef) {
        addNames(fieldDef.keySet());
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

    private void addNames(Set<String> names) {
        for (String name : names) {
            if (!this.names.add(name)) {
                Logger.warn("Duplicate name declared: {}", name);
            }
        }
    }
}
