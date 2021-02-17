import com.columnzero.gstruct.model.NominalModel
import groovy.transform.CompileStatic

import static com.columnzero.gstruct.model.Extern.extern
import static com.columnzero.gstruct.model.NameRef.named
import static com.columnzero.gstruct.model.Struct.struct
import static com.columnzero.gstruct.model.Tuple.tuple

@CompileStatic
static NominalModel setup() {
    def typeInt = extern "int"
    def namedInt = named "Int", typeInt

    def typeReal = extern "float"
    def namedReal = named "Real", typeReal

    def typeDouble = extern "double"
    def typeDecimalDeg = tuple typeDouble
    def namedDecimalDeg = named "DecimalDeg", typeDecimalDeg

    def typeDegMinSec = tuple namedInt, namedInt, namedReal
    def namedDegMinSec = named "DegMinSec", typeDegMinSec

    def typeLatLon = struct([
            latitude : namedDecimalDeg,
            longitude: namedDegMinSec
    ])
    def namedLatLon = named "LatLon", typeLatLon

    def typeGeoVolume = struct([
            northEast: namedLatLon,
            southWest: namedLatLon,
            height   : namedReal
    ])
    def namedGeoVolume = named "GeoVolume", typeGeoVolume

    return NominalModel.of([
            namedLatLon,
            namedGeoVolume,
            namedDegMinSec,
            namedDecimalDeg,
            namedInt,
            namedReal
    ] as Iterable)
}

return setup()
