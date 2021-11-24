package com.columnzero.gstruct.model;

import lombok.NonNull;
import lombok.Value;

import java.util.LinkedHashMap;
import java.util.Map;

public interface TreeNode<T, V> {

    boolean isLeaf();

    @Value
    class Tree<T, V> implements TreeNode<T, V> {

        @NonNull Map<T, TreeNode<T, V>> children = new LinkedHashMap<>();

        boolean leaf = false;
    }

    @Value
    class Leaf<V> implements TreeNode<Object, V> {

        boolean leaf = true;

        V value;
    }
}
