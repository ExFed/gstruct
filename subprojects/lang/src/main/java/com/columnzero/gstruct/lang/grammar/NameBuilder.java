package com.columnzero.gstruct.lang.grammar;

import com.columnzero.gstruct.model.Identifier;
import com.columnzero.gstruct.model.Identifier.Local;
import com.columnzero.gstruct.model.Identifier.Name;
import com.columnzero.gstruct.model.NominalModel;
import com.columnzero.gstruct.model.Type;
import com.columnzero.gstruct.model.Type.Ref;
import com.columnzero.gstruct.util.PrefixNode;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.codehaus.groovy.runtime.InvokerHelper;

@AllArgsConstructor
public class NameBuilder implements GroovyObject {

    public static NameBuilder root(NominalModel model) {
        var trie = model.asTrie().getNode();
        return new NameBuilder(trie, Identifier.name());
    }

    private final GroovyObjectDelegate god = new GroovyObjectDelegate(NameRefIndex.class);

    private final @NonNull PrefixNode<Local, Ref<? extends Type>> tree;
    private final @NonNull Name name;

    @Override
    public Object invokeMethod(String name, Object args) {
        throw new MissingMethodException(name, NameBuilder.class, InvokerHelper.asArray(args));
    }

    @Override
    public Object getProperty(String id) {
        if (!tree.hasChild(id)) {
            throw new MissingPropertyException(id);
        }

        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        throw new UnsupportedOperationException("read-only property: " + propertyName);
    }

    @Override
    public MetaClass getMetaClass() {
        return god.getMetaClass();
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        god.setMetaClass(metaClass);
    }
}
