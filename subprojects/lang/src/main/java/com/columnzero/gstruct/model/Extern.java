package com.columnzero.gstruct.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Extern implements Type {

    public static Extern extern(String name) {
        return new Extern(name);
    }

    @NonNull String name;
}
