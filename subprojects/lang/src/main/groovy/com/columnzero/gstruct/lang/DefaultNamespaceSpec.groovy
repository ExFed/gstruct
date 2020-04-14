package com.columnzero.gstruct.lang

import static groovy.lang.Closure.DELEGATE_ONLY

class DefaultNamespaceSpec implements NamespaceSpec {

    private final GraphContext $context

    DefaultNamespaceSpec(GraphContext context) {
        this.$context = context
    }

    // assume unknown properties are FQNames
    def propertyMissing(String name) {
        return new FQName(name, $context.name)
    }

    // assume unknown methods are FQNames and Spec closures
    def methodMissing(String methodName, Object argsObj) {
        def args = argsObj as Object[]
        def typeName = new FQName(methodName, $context.name)
        if (args.size() == 1 && args[0] instanceof Closure) {
            return new SpecParams(typeName, args[0] as Closure)
        }
        throw new MissingMethodException(methodName, this.getClass(), args)
    }

    @Override
    void setDescription(String body) {
        $context.putStr(Relationships.DESCRIPTION, body)
    }

    @Override
    void namespace(SpecParams params) {
        namespace(params.name, params.configurator)
    }

    @Override
    void namespace(FQName name, Closure cfgtor) {
        def spec = new DefaultNamespaceSpec($context.rescope(name))
        def configurator = cfgtor.rehydrate(spec, this, this)
        configurator.resolveStrategy = DELEGATE_ONLY
        configurator()
    }

    @Override
    void type(Map names) {
        names.each { n, p ->
            // coerce name into FQName
            def name = n instanceof FQName ? n : new FQName(n as String, $context.name)

            // coerce param into FQName if it's a String
            def param = p instanceof String ? new FQName(p, $context.name) : p

            // map name to spec
            if (param instanceof FQName) {
                // store the name mapping
                $context.graph.put(name, Relationships.TYPE, param)
            } else if (param instanceof SpecParams) {
                param = (SpecParams) param

                // store the name mapping
                $context.graph.put(name, Relationships.TYPE, param.name)

                // spec needs to be configured; delegate to TypeSpec
                def spec = new DefaultTypeSpec($context.rescope(name))
                def configurator = param.configurator.rehydrate(spec, this, this)
                configurator.resolveStrategy = DELEGATE_ONLY
                configurator()
            } else {
                throw new DslException("Cannot map name <$name> to type: $param")
            }
        }
    }

    @Override
    void struct(Map names) {
        names.each { n, cfgtor ->
            // coerce name into FQName
            def name = n instanceof FQName ? n : new FQName(n as String, $context.name)

            $context.graph.put(name, Relationships.TYPE, Keywords.STRUCT)
            def spec = new DefaultStructSpec($context.rescope(name))
            def configurator = (cfgtor as Closure).rehydrate(spec, this, this)
            configurator.resolveStrategy = DELEGATE_ONLY
            configurator()
        }
    }
}
