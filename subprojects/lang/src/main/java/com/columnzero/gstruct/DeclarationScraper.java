package com.columnzero.gstruct;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class DeclarationScraper {

    private final Set<String> names = new LinkedHashSet<>();

    public Set<String> getNames() {
        return Collections.unmodifiableSet(names);
    }
}
