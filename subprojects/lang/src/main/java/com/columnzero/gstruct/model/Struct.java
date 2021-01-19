package com.columnzero.gstruct.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "struct")
public class Struct implements Type {

    @NonNull Map<String, Type> fields = new LinkedHashMap<>();

    @Override
    public String toString() {
        return fields.entrySet()
                     .stream()
                     .map(entry -> entry.getKey() + ":" + entry.getValue())
                     .collect(Collectors.joining(", ", "Struct(", ")"));
    }
}
