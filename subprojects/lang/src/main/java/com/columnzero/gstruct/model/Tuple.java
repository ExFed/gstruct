package com.columnzero.gstruct.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tuple implements Type {

    public static Tuple tuple(Type... types) {
        return new Tuple(Arrays.asList(types));
    }

    @NonNull List<Type> types = new ArrayList<>();

    @Override
    public String toString() {
        return types.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", ", "Tuple(", ")")) + ")";
    }
}
