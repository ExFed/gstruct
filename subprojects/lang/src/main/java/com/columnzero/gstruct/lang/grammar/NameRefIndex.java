package com.columnzero.gstruct.lang.grammar;

import com.columnzero.gstruct.model.Identifier;
import com.columnzero.gstruct.model.NameRef;
import com.columnzero.gstruct.model.NominalModel;
import com.columnzero.gstruct.model.Type;
import com.columnzero.gstruct.model.Type.Ref;
import com.columnzero.gstruct.util.PrefixNode;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public final class NameRefIndex implements GroovyObject {

/*
    public static NameRefIndex of(Iterable<NameRef> nameRefs) {
        var tree = new NameTree();
        for (NameRef ref : nameRefs) {
            List<String> path =
                    ref.getName()
                       .getPath()
                       .foldLeft(List.empty(), (ls, l) -> ls.append(l.getId()));
            try {
                tree.put(path, ref);
            } catch (NameTreeException e) {
                throw new IllegalArgumentException(
                        "could not index (" + e.getMessage() + "): " + ref);
            }
        }
        return new NameRefIndex(tree);
    }
*/

    private final GroovyObjectDelegate god = new GroovyObjectDelegate(NameRefIndex.class);

    private final @NonNull PrefixNode<Identifier, Ref<? extends Type>> tree;
    private final @NonNull NominalModel model;

    @Override
    public Object getProperty(String id) {
        var node = tree.getChild(id);
        if (node == null) {
            throw new MissingPropertyException(id, NameRefIndex.class);
        } else if (node.isLeaf()) {
        }
        return node instanceof PrefixNode ? new NameRefIndex(node) : node;
    }

    @Override
    public @NonNull MetaClass getMetaClass() {
        return god.getMetaClass();
    }

    @Override
    public void setMetaClass(@NonNull MetaClass metaClass) {
        god.setMetaClass(metaClass);
    }

}

@AllArgsConstructor
class NameTree implements GroovyObject {

    private final GroovyObjectDelegate god = new GroovyObjectDelegate(NameTree.class);
}

class NameTreeMeh {

    private final @NonNull Map<String, Object> nodes = new HashMap<>();

    public void put(List<String> path, NameRef ref) throws NameTreeException {
        if (path.isEmpty()) {
            throw new NameTreeException("empty path");
        }
        var id = path.head();
        if (path.size() == 1) {
            if (nodes.containsKey(id)) {
                throw new NameTreeException("leaf path not unique");
            }
            nodes.put(id, ref);
        } else {
            Object node;
            if (nodes.containsKey(id)) {
                node = nodes.get(id);
                if (!(node instanceof NameTreeMeh)) {
                    throw new NameTreeException("leaf path conflicts with tree");
                }
            } else {
                node = new NameTreeMeh();
                nodes.put(id, node);
            }
            ((NameTreeMeh) node).put(path.pop(), ref);
        }
    }

    public Optional<Object> get(String id) {
        return Optional.ofNullable(nodes.get(id));
    }
}

class NameTreeException extends Exception {

    public NameTreeException(String message) {
        super(message);
    }
}
