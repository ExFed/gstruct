package com.columnzero.gstruct

class Scopes {
    static final CName UNSET = new CName('', null)
    static final CName GLOBAL = new CName('', null)
}

class Keywords {
    static final CName PRIMITIVE = new CName('primitive', Scopes.GLOBAL)
    static final CName STRUCT = new CName('struct', Scopes.GLOBAL)
}

class Relationships {
    static final CName TYPE = new CName('is', Scopes.GLOBAL)
}

interface ScopeSpec {
    void namespace(CName namespace, Closure spec)
    void primitive(CName member)
    void struct(CName name, Closure spec)
}

class NamedScope implements ScopeSpec {
    private final CName $namespace

    private final StructGraph $graph

    NamedScope(CName name, StructGraph graph) {
        this.$namespace = name
        this.$graph = graph
    }

    @Override
    void struct(CName name, Closure spec) {
        $graph.put(name, Relationships.TYPE, Keywords.STRUCT)
        def subScope = new NamedScope(name, $graph)
        spec = spec.rehydrate(subScope, this, this)
        spec.resolveStrategy = Closure.DELEGATE_ONLY
        spec()
    }

    @Override
    void primitive(CName member) {
        $graph.put(member, Relationships.TYPE, Keywords.PRIMITIVE)
    }

    @Override
    void namespace(CName name, Closure spec) {
        def subScope = new NamedScope(name, $graph)
        spec = spec.rehydrate(subScope, this, this)
        spec.resolveStrategy = Closure.DELEGATE_ONLY
        spec()
    }

    def propertyMissing(String name) {
        // "${this.getClass()} property: $namespace"
        return new CName(name, $namespace)
    }

    def methodMissing(String methodName, args) {
        // "${this.getClass()} method: $methodName($args)"
        def typeName = new CName(methodName, $namespace)
        if (args.size() == 1) {
            def arg = args[0]
            if (arg instanceof Closure) {
                return [typeName, arg]
            }

            if (arg instanceof CName) {
                $graph.put(arg, Relationships.TYPE, typeName)
                return
            }
        }

        throw new MissingMethodException(methodName, this.getClass(), args)
    }
}
