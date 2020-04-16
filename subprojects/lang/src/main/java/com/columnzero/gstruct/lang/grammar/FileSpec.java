package com.columnzero.gstruct.lang.grammar;

import java.util.Map;

public interface FileSpec extends PackageSpec {
    void include(Map<String, IdentifierSpec> included);
}
