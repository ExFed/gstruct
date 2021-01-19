package com.columnzero.gstruct.model;

import lombok.NonNull;
import lombok.Value;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Value
public class NameBindings {

    @NonNull Map<String, Type> bindings = new TreeMap<>();

    public Map<String, Type> getRefs() {
        return bindings.entrySet()
                       .stream()
                       .collect(Collectors.toMap(Map.Entry::getKey,
                                                 e -> Ref.lazy(e.getKey(), e::getValue)));
    }
}
