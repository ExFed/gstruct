package com.columnzero.gstruct.model;

import com.columnzero.gstruct.model.Identifier.Name;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class NameRef implements Ref<Type>, Comparable<NameRef> {

    static NameRef of(Name name, NominalModel model) {
        return new NameRef(name, model);
    }

    @NonNull Name name;

    @NonNull NominalModel model;

    @Override
    public Type get() {
        return model.getBindings()
                    .get(name)
                    .getOrElseThrow(() -> new BindingNotFoundException(name))
                    .get();
    }

    @Override
    public String toString() {
        return "NameRef->" + name;
    }

    @Override
    public int compareTo(NameRef that) {
        return this.name.compareTo(that.name);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NameRef)) {
            return false;
        }

        // assume that if the names are the same then they refer to the same type structure
        final NameRef that = (NameRef) obj;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return 59 + name.hashCode();
    }

}
