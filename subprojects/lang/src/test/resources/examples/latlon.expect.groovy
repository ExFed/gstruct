package examples

import com.columnzero.gstruct.model.NameRef
import com.columnzero.gstruct.model.NominalModel
import groovy.transform.CompileStatic

import static com.columnzero.gstruct.model.Extern.extern
import static com.columnzero.gstruct.model.Struct.struct
import static com.columnzero.gstruct.model.Tuple.tuple

@CompileStatic
static NominalModel setup() {
    def typeInt = extern 'int'
    def namedInt = NameRef.of typeInt named 'Int'

    def typeReal = extern 'float'
    def namedReal = NameRef.of typeReal named 'Real'

    def typeDouble = extern 'double'
    def typeDecimalDeg = tuple typeDouble
    def namedDecimalDeg = NameRef.of typeDecimalDeg named 'DecimalDeg'

    def typeDegMinSec = tuple namedInt, namedInt, namedReal
    def namedDegMinSec = NameRef.of typeDegMinSec named 'DegMinSec'

    def typeLatLon = struct([
            latitude : namedDecimalDeg,
            longitude: namedDegMinSec
    ])
    def namedLatLon = NameRef.of typeLatLon named 'LatLon'

    def typeGeoVolume = struct([
            northEast: namedLatLon,
            southWest: namedLatLon,
            height   : namedReal
    ])
    def namedGeoVolume = NameRef.of typeGeoVolume named 'GeoVolume'

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
