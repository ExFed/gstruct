<%
def visitType

def visitExtern = { t -> t.name }

def visitTuple = { t ->
    'tuple { ' + t.types.collect { visitType(it) }.join(', ') + ' }'
}

def visitStruct = { t ->
    def fields = t.fields.toJavaMap().collect { f, ft -> "    ${visitType(ft)} $f;\n" }.join('')
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
