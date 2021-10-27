package com.columnzero.gstruct.lang.grammar;

import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import lombok.Data;
import lombok.NonNull;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

class MixinScopeTest {

    private Appender stuffAppender;
    private Prepender thingsPrepender;

    @BeforeEach
    void setUp() {
        stuffAppender = new Appender("stuff");
        thingsPrepender = new Prepender("things");
    }

    @Test
    void invokeMethodEmpty() {
        var cut = MixinScope.builder().build();
        assertThrows(MissingMethodException.class,
                     () -> cut.invokeMethod("something", InvokerHelper.EMPTY_ARGS));
    }

    @Test
    void methods() {
        var cut = MixinScope.builder()
                            .scope(stuffAppender)
                            .scope(thingsPrepender)
                            .build();

        var expectAppendRet = "1 and 2 and 3";
        var actualAppendRet = cut.invokeMethod("append", new String[]{"1", "2", "3"});
        assertThat(actualAppendRet).isEqualTo(expectAppendRet);
        assertThat(stuffAppender.toString()).isEqualTo("stuff and " + expectAppendRet);

        var expectPrependRet = "A and B and C";
        var actualPrependRet = cut.invokeMethod("prepend", new String[]{"A", "B", "C"});
        assertThat(actualPrependRet).isEqualTo(expectPrependRet);
        assertThat(thingsPrepender.toString()).isEqualTo(expectPrependRet + " and things");

        // invokes first delegate
        var expectToStringRet = stuffAppender.toString();
        var actualToStringRet = cut.invokeMethod("toString", InvokerHelper.EMPTY_ARGS);
        assertThat(actualToStringRet).isEqualTo(expectToStringRet);

        // throws on missing method
        assertThrows(MissingMethodException.class,
                     () -> cut.invokeMethod("nothing", InvokerHelper.EMPTY_ARGS));
    }

    @Test
    void properties() {
        var cut = MixinScope.builder()
                            .scope(stuffAppender)
                            .scope(thingsPrepender)
                            .build();

        assertThat(cut.getProperty("prefix")).isEqualTo(stuffAppender.getPrefix());
        assertThat(cut.getProperty("suffix")).isEqualTo(thingsPrepender.getSuffix());

        var modifiedPrefix = stuffAppender.getPrefix() + "!";
        cut.setProperty("prefix", modifiedPrefix);
        assertThat(cut.getProperty("prefix")).isEqualTo(modifiedPrefix);

        var modifiedSuffix = thingsPrepender.getSuffix() + "?";
        cut.setProperty("suffix", modifiedSuffix);
        assertThat(cut.getProperty("suffix")).isEqualTo(modifiedSuffix);

        assertThrows(MissingPropertyException.class, () -> cut.getProperty("nothing"));
        assertThrows(MissingPropertyException.class, () -> cut.setProperty("nothing", "anything"));
    }

    @Data
    static class Appender {

        private @NonNull String prefix;
        private final @NonNull List<String> appended = new ArrayList<>();

        @SuppressWarnings("unused")
        public String append(String... somethings) {
            var l = Arrays.asList(somethings);
            appended.addAll(l);
            return String.join(" and ", l);
        }

        @Override
        public String toString() {
            return prefix + " and " + String.join(" and ", appended);
        }
    }

    @Data
    static class Prepender {

        private @NonNull String suffix;
        private final @NonNull List<String> prepended = new ArrayList<>();

        @SuppressWarnings("unused")
        public String prepend(String... somethings) {
            var l = Arrays.asList(somethings);
            prepended.addAll(0, l);
            return String.join(" and ", l);
        }

        @Override
        public String toString() {
            return String.join(" and ", prepended) + " and " + suffix;
        }
    }
}
