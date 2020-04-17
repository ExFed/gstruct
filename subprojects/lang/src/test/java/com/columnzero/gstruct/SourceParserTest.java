package com.columnzero.gstruct;

import com.columnzero.gstruct.util.Path;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceParserTest {

    @Test
    void parseNameDeclarations() {
        final String src = "" +
                "typedef String : primitive\n" +
                "typedef Number : primitive\n" +
                "typedef Bool : primitive\n";

        final Set<Path<String>> expect = Stream.of("String", "Number", "Bool")
                                               .map(Path::of)
                                               .collect(Collectors.toSet());

        final DeclarationScraper delegate = new DeclarationScraper(Path.getRoot());
        new SourceParser(src).parse(delegate);
        final Set<Path<String>> actual = delegate.getAllDeclarations();
        assertEquals(expect, actual);
    }
}
