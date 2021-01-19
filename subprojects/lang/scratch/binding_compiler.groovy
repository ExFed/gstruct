import groovy.transform.Canonical
import groovy.transform.CompileStatic

interface Type {}

@CompileStatic
@Canonical
class Ref implements Type {
    String name
    Type type

    String toString() { name }
}

@CompileStatic
@Canonical
class Extern implements Type {
    String name
}

@CompileStatic
@Canonical
class Tuple implements Type {
    List<Type> types = []

    String toString() { "Tuple(${types.join(', ')})" }
}

@CompileStatic
@Canonical
class Struct implements Type {
    Map<String, Type> fields = [:]

    String toString() { "Struct(${fields.collect { k, v -> "$k:$v" }.join(', ')})" }
}

class Compiler {
    @CompileStatic
    static Closure<?> binder(Map<String, Type> bindings) {
        return { Map<String, Type> mapping ->
            def dupes = mapping.keySet().intersect(bindings.keySet())
            if (dupes) {
                throw new RuntimeException("duplicate names: $dupes")
            }
            println "## binding $mapping"
            bindings << mapping
        }
    }

    static Map<String, Type> compile(Closure code) {
        def model = new TreeMap<String, Type>().withDefault { String name ->
            { -> throw new RuntimeException("name not found in model: $name") }.call()
        }
        def getRefs = { -> model.collectEntries { k, v -> [k, new Ref(k, v)] } }

        def tasks = [] as Queue<Closure> // breadth first syntax traversal
        def addTask = { Closure task -> tasks.offer(task) }
        def nextTask = { -> tasks.poll() }

        def scope = new Expando().tap {
            bind = binder(model)
            extern = { String name -> new Extern(name) }
            tuple = tupleCons(addTask, getRefs)
            struct = structCons(addTask, getRefs)
        }

        addTask { scope.with code } // get the traversal started
        while (tasks) {
            nextTask().call() // traverse each syntax node
        }

        return model
    }


    private static Closure<Tuple> tupleCons(addTask, getRefs) {
        return { Closure cl ->
            println '# initializing tuple'
            def tuple = new Tuple()
            addTask {
                println '# constructing tuple'
                (getRefs() as Expando).tap {
                    types = { Type... types ->
                        println "## adding types: $types"
                        tuple.types.addAll(types)
                    }
                }.with cl // apply cl in context of tuple scope
            }
            return tuple
        }
    }

    private static Closure<Struct> structCons(addTask, getRefs) {
        return { Closure cl ->
            println '# initializing struct'
            def struct = new Struct()
            addTask {
                println '# constructing struct'
                (getRefs() as Expando).tap {
                    field = binder(struct.fields)
                }.with cl // apply cl in context of struct scope
            }
            return struct
        }
    }
}

def result = Compiler.compile {
    bind LatLon: struct {
        field latitude: DecimalDeg
        field longitude: DegMinSec
    }
    bind GeoVolume: struct {
        field northEast: LatLon
        field southWest: LatLon
        field height: Real
    }
    def tupleIntIntReal = tuple { types Int, Int, Real }
    bind DegMinSec: tupleIntIntReal
    bind DecimalDeg: tuple { types extern('double') }
    bind Int: extern('int')
    bind Real: extern('float')
    bind String: extern('string')
    bind Thing: tuple { types delegate.String, Thing }
}

println "\n$result"
assert result as String == '[DecimalDeg:Tuple(Extern(double)), DegMinSec:Tuple(Int, Int, Real), GeoVolume:Struct(northEast:LatLon, southWest:LatLon, height:Real), Int:Extern(int), LatLon:Struct(latitude:DecimalDeg, longitude:DegMinSec), Real:Extern(float), String:Extern(string), Thing:Tuple(String, Thing)]'
