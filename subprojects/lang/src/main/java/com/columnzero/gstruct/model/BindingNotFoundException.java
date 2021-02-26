package com.columnzero.gstruct.model;

public class BindingNotFoundException extends BindingException {
    BindingNotFoundException(Identifier.Name name) {
        super("name not found: " + name);
    }
}
