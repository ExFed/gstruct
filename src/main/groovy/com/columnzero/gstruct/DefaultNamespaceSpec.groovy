package com.columnzero.gstruct

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
    def methodMissing(String methodName, args) {
        def typeName = new FQName(methodName, $context.name)
        if (args.size() == 1 && args[0] instanceof Closure) {
            return new SpecParams(typeName, args[0])
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
    void namespace(FQName name, Closure configurator) {
        def spec = new DefaultNamespaceSpec($context.scope(name))
        configurator = configurator.rehydrate(spec, this, this)
        configurator.resolveStrategy = Closure.DELEGATE_ONLY
        configurator()
    }

    @Override
    void type(Map names) {
        names.each { name, param ->
            // coerce name into FQName
            if (!(name instanceof FQName)) {
                name = new FQName(name, $context.name)
            }

            // map name to spec
            if (param instanceof FQName) {
                // store the name mapping
                $context.graph.put(name, Relationships.TYPE, param)
            } else if (param instanceof SpecParams) {
                param = (SpecParams) param

                // store the name mapping
                $context.graph.put(name, Relationships.TYPE, param.name)

                // spec needs to be configured; delegate to TypeSpec
                def spec = new DefaultTypeSpec($context.scope(name))
                def configurator = param.configurator.rehydrate(spec, this, this)
                configurator.resolveStrategy = Closure.DELEGATE_ONLY
                configurator()
            } else {
                throw new DslException("Cannot map name <$name> to type: $param")
            }
        }
    }

    @Override
    void struct(Map names) {
        names.each { name, configurator ->
            // coerce name into FQName
            if (!(name instanceof FQName)) {
                name = new FQName(name, $context.name)
            }

            $context.graph.put(name, Relationships.TYPE, Keywords.STRUCT)
            def spec = new DefaultStructSpec($context.scope(name))
            configurator = configurator.rehydrate(spec, this, this)
            configurator.resolveStrategy = Closure.DELEGATE_ONLY
            configurator()
        }
    }
}
