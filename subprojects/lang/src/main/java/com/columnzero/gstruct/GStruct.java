package com.columnzero.gstruct;

import com.columnzero.gstruct.lang.Namespace;
import com.columnzero.gstruct.lang.Parser;
import com.columnzero.gstruct.lang.internal.NameDeclarations;
import com.columnzero.gstruct.util.function.Mapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The main interface into GStruct.
 */
public class GStruct {

    public static void main(String[] args) {
        try {
            final SourceTree.Root root = SourceTree.root(new File(args[0]));
            final SourceTree gsmlSources = root.select("gsml");
            new GStruct().compile(gsmlSources);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private static void printDeclResult(Result<Decl> declResult) {
        declResult.handle(GStruct::printDecl, System.err::println);
    }

    private static void printDecl(Decl decl) {
        decl.getDelegate()
            .$names()
            .stream()
            .map(name -> decl.getNamespace().getPath().child(name).toString())
            .forEach(System.out::println);
    }

    /**
     * Compiles a tree of source files into a type graph.
     *
     * @param sourceTree A tree of source files.
     *
     * @return A new type graph.
     */
    public TypeGraph compile(SourceTree sourceTree) {
        final Map<SourceFile, Namespace> sourceFiles = sourceTree.getNamespaces();
        sourceFiles.entrySet()
                   .stream()
                   .map(value -> Result.success(value)
                                       .map(Init::accept)
                                       .map(Decl::accept))
                   .forEach(GStruct::printDeclResult);

        return new TypeGraph();
    }

    @Value
    private static class Init {

        public static Init accept(Map.Entry<SourceFile, Namespace> entry)
                throws CompileException {
            try {
                final Parser<File> parser = Parser.withSource(entry.getKey().getFile());
                final Namespace namespace = entry.getValue();
                return new Init(parser, namespace);
            } catch (IOException e) {
                throw new CompileException(e);
            }
        }

        @NonNull Parser<File> parser;
        @NonNull Namespace namespace;
    }

    @Value
    private static class Decl {

        public static Decl accept(Init init) {
            final Parser<File> parser = init.getParser();
            return new Decl(init, parser.run(new NameDeclarations()));
        }

        @NonNull Init init;
        @NonNull NameDeclarations delegate;

        public Namespace getNamespace() {
            return init.getNamespace();
        }
    }

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter(value = AccessLevel.NONE)
    private static class Result<T> {

        public static <T> Result<T> success(T value) {
            return new Result<>(value, null);
        }

        public static <T> Result<T> failure(CompileException ex) {
            return new Result<>(null, ex);
        }

        T value;
        CompileException error;

        public <R> Result<R> map(Mapper<T, R, CompileException> mapper) {
            if (!isSuccess()) {
                return failure(error);
            }
            try {
                return Result.success(mapper.apply(value));
            } catch (CompileException e) {
                return Result.failure(e);
            }
        }

        public boolean isSuccess() {
            return error == null;
        }

        public void handle(Consumer<T> ifSuccess, Consumer<CompileException> ifError) {
            if (isSuccess()) {
                ifSuccess.accept(value);
            } else {
                ifError.accept(error);
            }
        }

    }

    public static class CompileException extends Exception {
        public CompileException() {
        }

        public CompileException(String message) {
            super(message);
        }

        public CompileException(String message, Throwable cause) {
            super(message, cause);
        }

        public CompileException(Throwable cause) {
            super(cause);
        }
    }
}
