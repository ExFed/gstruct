package com.columnzero.gstruct.model;

import io.vavr.Lazy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.codehaus.groovy.util.HashCodeHelper;

import java.util.Objects;
import java.util.function.Supplier;

public interface Ref extends Type {
    static Ref eager(String name, Type type) {
        return new Eager(name, type);
    }

    static Ref lazy(String name, Supplier<Type> typeSupplier) {
        return new Supplied(name, Lazy.of(typeSupplier));
    }

    String getName();

    Type getType();
    @Value
    class Eager implements Ref {

        @NonNull String name;

        @NonNull Type type;

        @Override
        public String toString() {
            return name;
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object obj) {
            return Util.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return Util.hashCode(this);
        }
    }

    @Value
    class Supplied implements Ref {

        @NonNull String name;

        @Getter(AccessLevel.PRIVATE)
        @NonNull Supplier<Type> typeSupplier;

        public Ref asEager() {
            return eager(getName(), getType());
        }

        @Override
        public Type getType() {
            return typeSupplier.get();
        }

        @Override
        public String toString() {
            return name;
        }


        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object obj) {
            return Util.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return Util.hashCode(this);
        }
    }

    class Util {
        private Util() {
            throw new AssertionError("util class");
        }

        public static int hashCode(Ref ref) {
            int hash;
            hash = HashCodeHelper.initHash();
            hash = HashCodeHelper.updateHash(hash, ref.getName());
            hash = HashCodeHelper.updateHash(hash, ref.getType());
            return hash;
        }

        public static boolean equals(Ref self, Object obj) {
            if (self == obj) {
                return true;
            }

            // implicit null check
            if (!(obj instanceof Ref)) {
                return false;
            }

            Ref other = (Ref) obj;

            return Objects.equals(self.getName(), other.getName())
                    && Objects.equals(self.getType(), other.getType());
        }
    }
}
