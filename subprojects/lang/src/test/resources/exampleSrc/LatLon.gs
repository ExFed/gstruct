/*+++
[expect]
script.groovy = """\
    import com.columnzero.gstruct.model.NameBindings

    import static com.columnzero.gstruct.model.Extern.extern
    import static com.columnzero.gstruct.model.Ref.eager
    import static com.columnzero.gstruct.model.Struct.struct
    import static com.columnzero.gstruct.model.Tuple.tuple
    import static java.util.Map.entry
    import static java.util.Map.ofEntries

    def typeInt = extern("int")
    def refInt = eager("Int", typeInt)

    def typeReal = extern("float")
    def refReal = eager("Real", typeReal)

    def typeDecimalDeg = tuple(extern("double"))
    def refDecimalDeg = eager("DecimalDeg", typeDecimalDeg)

    def typeDegMinSec = tuple(refInt, refInt, refReal)
    def refDegMinSec = eager("DegMinSec", typeDegMinSec)

    def typeLatLon = struct(ofEntries(
            entry("latitude", refDecimalDeg),
            entry("longitude", refDegMinSec))
    )
    def refLatLon = eager("LatLon", typeLatLon)

    def bindings = [
            DecimalDeg: typeDecimalDeg,
            DegMinSec : typeDegMinSec,
            GeoVolume : struct(
                    northEast: refLatLon,
                    southWest: refLatLon,
                    height   : refReal),
            Int       : typeInt,
            LatLon    : typeLatLon,
            Real      : typeReal
    ]

    def expect = new NameBindings()
    expect.bindings << bindings
    return expect
"""
 */

def tupleIntIntReal = tuple { types Int, Int, Real }

bind LatLon: struct {
    field latitude: DecimalDeg
    field longitude: DegMinSec
}

bind GeoVolume: struct {
    field northEast: LatLon
    field southWest: LatLon
    field height: Real
}

bind DegMinSec: tupleIntIntReal

bind DecimalDeg: tuple { types extern('double') }

bind Int: extern('int')

bind Real: extern('float')
