package com.columnzero.gstruct

class Scopes {
    static final CName UNSET = new CName('', null)
    static final CName GLOBAL = new CName('', null)
}

class Keywords {
    static final CName PRIMITIVE = new CName('primitive', Scopes.GLOBAL)
}

class Relationships {
    static final CName MEMBER = new CName('hasMember', Scopes.GLOBAL)
    static final CName TYPE = new CName('isType', Scopes.GLOBAL)
}

class FileScope {
    private CName $namespace = Scopes.UNSET

    private final StructGraph $graph

    FileScope(StructGraph graph) {
        this.$graph = graph
    }

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
            def memberCName = args[0]
            if (memberCName instanceof String) {
                typeName = new CName(methodName, $namespace)
            }
            if (memberCName instanceof CName) {
                $graph.put(memberCName as CName, Relationships.TYPE, typeName)
                return
            }
        }

        throw new MissingMethodException(name, this.getClass(), args)
    }
}
