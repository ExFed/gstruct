package com.columnzero.gstruct.codegen;

import com.columnzero.gstruct.model.Identifier.Name;
import com.columnzero.gstruct.model.NameRef;
import com.columnzero.gstruct.model.NominalModel;
import com.columnzero.gstruct.model.Type;
import groovy.lang.GroovyShell;
import groovy.text.SimpleTemplateEngine;
import groovy.text.TemplateEngine;
import io.vavr.Function1;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static io.vavr.Function1.identity;
import static io.vavr.control.Either.right;

@Value
public class SimpleGroovyCodegen {

    private static GroovyShell configureGroovyShell() {
        final var typesPackageName = Type.class.getPackage().getName();

        final var customImports = new ImportCustomizer();
        customImports.addStarImports(typesPackageName);

        final var compilerConfig = new CompilerConfiguration();
        compilerConfig.addCompilationCustomizers(customImports);

        return new GroovyShell(compilerConfig);
    }

    public static Function1<Name, Either<IOException, Writer>> nameToWriter(@NonNull File outDir,
                                                                            @NonNull String suffix)
            throws IOException {

        Files.createDirectories(outDir.toPath());

        return name -> {
            final var outputFile =
                    name.getPath()
                        .foldLeft(outDir, (dir, local) -> new File(dir, local.getId() + suffix));
            try {
                return right(new FileWriter(outputFile));
            } catch (IOException e) {
                return Either.left(e);
            }
        };
    }

    @Getter(AccessLevel.NONE)
    @NonNull TemplateEngine engine =
            new SimpleTemplateEngine(configureGroovyShell());

    public void run(@NonNull NominalModel model,
                    @NonNull Reader templateReader,
                    @NonNull Function1<Name, Either<IOException, Writer>> nameToWriter)
            throws IOException, ClassNotFoundException {

        final var template = engine.createTemplate(templateReader);

        var writers = Stream.<Writer>empty();

        for (NameRef nameRef : model.getNameRefs()) {
            final var name = nameRef.getName();
            final var type = nameRef.get();
            final var writer = nameToWriter.apply(name).getOrElseThrow(identity());
            writers = writers.append(writer);
            final var templateBindings = new HashMap<>(Map.of("name", name, "type", type));

            template.make(templateBindings).writeTo(writer);
        }

        // close all writers and accumulate errors
        final var errorsOnClose =
                writers.map(Try::success)
                       .map(w -> w.andThenTry(Writer::close))
                       .filter(Try::isFailure)
                       .map(Try::getCause)
                       .map(IOException.class::cast)
                       .reduceLeftOption((t, t2) -> {
                           t2.addSuppressed(t); // link the errors
                           return t;
                       });

        if (errorsOnClose.isDefined()) {
            throw errorsOnClose.get();
        }
    }

    public void run(@NonNull NominalModel model,
                    @NonNull Reader templateReader,
                    @NonNull File outDir,
                    @NonNull String suffix)
            throws IOException, ClassNotFoundException {

        run(model, templateReader, nameToWriter(outDir, suffix));
    }
}
