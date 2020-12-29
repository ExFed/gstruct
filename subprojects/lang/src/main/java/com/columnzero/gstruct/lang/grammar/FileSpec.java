package com.columnzero.gstruct.lang.grammar;

import java.util.Map;

/** Specifies a source file. May contain multiple specifications. */
public interface FileSpec extends PackageSpec {

    /** Declares aliases to use for fully-qualified names. */
    void using(Map<String, RefSpec> aliases);
}
