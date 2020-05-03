package com.columnzero.gstruct.lang;

import com.columnzero.gstruct.lang.grammar.DocumentationSpec;
import com.columnzero.gstruct.lang.grammar.FieldSpec;
import com.columnzero.gstruct.lang.grammar.FileSpec;
import com.columnzero.gstruct.lang.grammar.PackageSpec;
import com.columnzero.gstruct.lang.grammar.RefSpec;
import com.columnzero.gstruct.lang.grammar.StructSpec;
import groovy.lang.Binding;
import groovy.lang.Closure;
import org.tinylog.Logger;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DeclarationScraper implements FieldSpec,
                                           FileSpec,
                                           PackageSpec,
                                           StructSpec,
                                           DocumentationSpec {

    private final Set<String> $names = new LinkedHashSet<>();
    private final Binding $binding;

    public DeclarationScraper(Binding binding) {
        this.$binding = binding;
    }

    public Set<String> $names() {
        return Collections.unmodifiableSet($names);
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
            if (!this.$names.add(name)) {
                Logger.warn("Duplicate name declared: {}", name);
            }
            this.$binding.setVariable(name, new Closure<Void>(null) {
                public void doCall(Object... args) {
                }
            });
        }
    }
}
