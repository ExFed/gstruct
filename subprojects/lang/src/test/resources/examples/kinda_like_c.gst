<%
def visitType

def visitExtern = { t -> t.name }

def visitFields = { fields ->
    fields.collect { f, ft -> "    ${visitType(ft)} $f;\n" }.join('')
}

def visitTuple = { t ->
    def fields = visitFields(t.types.indexed().collectEntries{ f, ft -> ["_$f", ft] })
    'struct {' + (fields ? "\n$fields" : '')  + '}'
}

def visitStruct = { t ->
    def fields = visitFields(t.fields.toJavaMap())
    'struct {' + (fields ? "\n$fields" : '') + '}'
}

def visitNameRef = { ref -> ref.name }

def visitRef = { ref -> visitType(ref.get()) }

visitType = { val ->
    switch (val) {
        case Extern: visitExtern(val); break
        case Tuple: visitTuple(val); break
        case Struct: visitStruct(val); break
        case NameRef: visitNameRef(val); break
        case Ref: visitRef(val); break
        default: throw new UnsupportedOperationException("unknown value: $val")
    }
}
%>typedef ${visitType(type)} $name;
