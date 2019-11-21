package com.columnzero.gstruct

class Relationships {
    protected static final CName MEMBER = new CName('isMember', Scope.GLOBAL)
    protected static final CName TYPE = new CName('isType', Scope.GLOBAL)
}

class Scope {
    protected static final CName GLOBAL = new CName('', null)

    private final CName $name

    Scope() {
        this.$name = GLOBAL
    }

    def primitive(CName name) {
        // noop
    }

    def propertyMissing(String name) {
        // "${this.getClass()} property: $name"
        return new CName(name, $name)
    }

    def methodMissing(String methodName, args) {
        // "${this.getClass()} method: $methodName($args)"
        def typeName = new CName(methodName, $name)
        if (args.size() == 1) {
            def memberCName = args[0]
            if (memberCName instanceof String) {
                memberCName = new CName(memberCName, $name)
            }
            if (memberCName instanceof CName) {
                StructGraph.edge(memberCName, Relationships.TYPE, typeName)
                return
            }
        }

        throw new MissingMethodException(name, this.getClass(), args)
    }
}
