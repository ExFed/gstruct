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
    static final CName MEMBER = new CName('hasMember', Scopes.GLOBAL)
    static final CName TYPE = new CName('isType', Scopes.GLOBAL)
}

interface ScopeSpec {
    void primitive(CName member)
    void struct(CName name, Closure spec)
}

class FileScope implements ScopeSpec {
    private CName $namespace = Scopes.UNSET

    private final StructGraph $graph

    FileScope(StructGraph graph) {
        this.$graph = graph
    }

    @Override
    void struct(CName name, Closure spec) {
        println "${this.getClass()} struct: $name" // TODO implement
        $graph.put(name, Relationships.TYPE, Keywords.STRUCT)
    }

    @Override
    void primitive(CName member) {
        $graph.put(member, Relationships.TYPE, Keywords.PRIMITIVE)
    }

    void namespace(CName namespace) {
        if ($namespace == Scopes.UNSET) {
            $namespace = namespace
        } else {
            throw new UnsupportedOperationException("Cannot set namespace more than once!")
        }
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
