package com.columnzero.gstruct.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrieTest {

    private Trie<Integer, String> cut;
    private Map<Path<Integer>, String> data;

    @BeforeEach
    void setUp() {
        cut = new Trie<>();

        data = new LinkedHashMap<>();
        data.put(Path.getRoot(), "the root");
        data.put(Path.of(1), "the first level");
        data.put(Path.of(2), "the first level, part deux");
        data.put(Path.of(1, 1), "the second level");
        data.put(Path.of(1, 2), "the second level part deux");
        data.put(Path.of(1, 1, 1, 1, 1), "the penthouse");
    }

    @Test
    void size() {
        final Stream.Builder<Executable> exec = Stream.builder();

        final AtomicInteger i = new AtomicInteger(0);
        for (Entry<Path<Integer>, String> d : data.entrySet()) {
            exec.add(() -> {
                cut.put(d.getKey(), d.getValue());
                assertThat(cut.size()).isEqualTo(i.incrementAndGet());
            });
        }

        assertAll(exec.build());
    }

    @Test
    void get() {
        cut.putAll(data);

        final Stream.Builder<Executable> exec = Stream.builder();

        for (Entry<Path<Integer>, String> e : data.entrySet()) {
            exec.add(() -> assertThat(cut.get(e.getKey())).isEqualTo(e.getValue()));
        }

        // mappings not found
        exec.add(() -> assertThat(cut.get(null)).isEqualTo(null))
            .add(() -> assertThat(cut.get("not a path")).isEqualTo(null))
            .add(() -> assertThat(cut.get(Path.of("not", "an", "int"))).isEqualTo(null))
            .add(() -> assertThat(cut.get(Path.of(1337))).isEqualTo(null))
            .add(() -> assertThat(cut.get(Path.of(1, 3, 3, 7))).isEqualTo(null))
            .add(() -> assertThat(cut.get(Path.of(1, 1, 1, 1))).isEqualTo(null));

        assertAll(exec.build());
    }

    @Test
    void put() {
        final Stream.Builder<Executable> exec = Stream.builder();

        // insert data into empty trie
        for (Entry<Path<Integer>, String> e : data.entrySet()) {
            exec.add(() -> assertThat(cut.put(e.getKey(), e.getValue())).isEqualTo(null));
        }

        // overwrite existing elements
        for (Entry<Path<Integer>, String> e : data.entrySet()) {
            exec.add(() -> assertThat(cut.put(e.getKey(), null)).isEqualTo(e.getValue()));
        }

        assertAll(exec.build());
    }

    @Test
    void putAllMap() {
        final var expect = data.entrySet();

        cut.putAll(data);

        final var actual = cut.entrySet();

        assertThat(actual).containsExactlyElementsIn(expect);
    }

    @Test
    void putAllTrie() {
        final var expect = data.entrySet();
        final var trie = new Trie<Integer, String>();
        trie.putAll(data);

        cut.putAll(trie);

        final var actual = cut.entrySet();

        assertThat(actual).containsExactlyElementsIn(expect);
    }

    @Test
    void remove() {
        // remove from empty trie
        assertThat(cut.remove(Path.of(1))).isEqualTo(null);

        cut.putAll(data);

        final Stream.Builder<Executable> exec = Stream.builder();

        // remove existing data
        for (Entry<Path<Integer>, String> e : data.entrySet()) {
            exec.add(() -> assertThat(cut.remove(e.getKey())).isEqualTo(e.getValue()));
        }

        // remove removed data
        for (Entry<Path<Integer>, String> e : data.entrySet()) {
            exec.add(() -> assertThat(cut.remove(e.getKey())).isEqualTo(null));
        }

        assertAll(exec.build());
    }

    @Test
    void entrySetEmpty() {
        assertThat(cut.entrySet()).isEqualTo(Collections.emptySet());
    }

    @Test
    void entrySet() {
        cut.putAll(data);
        final Set<Entry<Path<Integer>, String>> actual = cut.entrySet();
        final Set<Entry<Path<Integer>, String>> expect = data.entrySet();

        assertThat(actual).containsExactlyElementsIn(expect);
    }

    @Test
    void containsKey() {
        final Stream.Builder<Executable> exec = Stream.builder();

        cut.putAll(data);

        for (Entry<Path<Integer>, String> e : data.entrySet()) {
            exec.add(() -> assertThat(cut.containsKey(e.getKey())).isTrue());
        }

        exec.add(() -> assertThat(cut.containsKey(null)).isFalse())
            .add(() -> assertThat(cut.containsKey("not a path")).isFalse())
            .add(() -> assertThat(cut.containsKey(Path.of("not", "an", "int"))).isFalse())
            .add(() -> assertThat(cut.containsKey(Path.of(1337))).isFalse())
            .add(() -> assertThat(cut.containsKey(Path.of(1, 3, 3, 7))).isFalse())
            .add(() -> assertThat(cut.containsKey(Path.of(1, 1, 1, 1))).isFalse());

        assertAll(exec.build());
    }

    @Test
    void errors() {
        assertThrows(NullPointerException.class, () -> cut.put(null, ""));
    }

    @Test
    void string() {
        cut.putAll(data);

        final var actual = cut.toString();

        final Stream.Builder<Executable> exec = Stream.builder();
        for (Entry<Path<Integer>, String> entry : data.entrySet()) {
            exec.add(() -> assertThat(actual).contains(entry.getKey().toString()))
                .add(() -> assertThat(actual).contains(entry.getValue()));
        }
        assertAll(exec.build());
    }

    @Test
    void getNode() {
        cut.putAll(data);

        var root = cut.getNode();
        assertThat(root.getValue()).isEqualTo(data.get(Path.getRoot()));

        for (Entry<Path<Integer>, String> e : data.entrySet()) {
            var node = cut.getNode(e.getKey());
            assertThat(node.map(PrefixNode::getValue)).isEqualTo(Optional.of(e.getValue()));
        }
    }
}
