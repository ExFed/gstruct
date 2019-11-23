package com.columnzero.gstruct

class Scopes {
    static final CName UNSET = new CName('', null)
    static final CName GLOBAL = new CName('', null)
}

class Keywords {
    static final CName PRIMITIVE = new CName('primitive', Scopes.GLOBAL)
}

class Relationships {
    static final CName MEMBER = new CName('isMember', Scopes.GLOBAL)
    static final CName TYPE = new CName('isType', Scopes.GLOBAL)
}

class FileScope {
    private CName $namespace = Scopes.UNSET

    FileScope() {}

    def primitive(CName member) {
        StructGraph.edge(member, Relationships.TYPE, Keywords.PRIMITIVE)
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
                StructGraph.edge(memberCName as CName, Relationships.TYPE, typeName)
                return
            }
        }

        throw new MissingMethodException(name, this.getClass(), args)
    }
}
