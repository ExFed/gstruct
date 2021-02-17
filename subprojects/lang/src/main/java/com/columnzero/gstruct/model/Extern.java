package com.columnzero.gstruct.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class Extern implements Type {

    // NOTE: Lombok appears to catastrophically fail when trying to generate this factory
    public static Extern extern(String name) {
        return new Extern(name);
    }

    @NonNull String name;
}
