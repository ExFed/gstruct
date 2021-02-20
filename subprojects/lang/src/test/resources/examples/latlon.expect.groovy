package examples

import com.columnzero.gstruct.model.NominalModel
import groovy.transform.CompileStatic

import static com.columnzero.gstruct.model.Extern.extern
import static com.columnzero.gstruct.model.Struct.struct
import static com.columnzero.gstruct.model.Tuple.tuple

@CompileStatic
static NominalModel buildModel() {
    def model = new NominalModel();

    def typeInt = extern 'int'
    def namedInt = model.bind typeInt to 'Int'

    def typeReal = extern 'float'
    def namedReal = model.bind typeReal to 'Real'

    def typeDouble = extern 'double'
    def typeDecimalDeg = tuple typeDouble
    def namedDecimalDeg = model.bind typeDecimalDeg to 'DecimalDeg'

    def typeDegMinSec = tuple namedInt, namedInt, namedReal
    def namedDegMinSec = model.bind typeDegMinSec to 'DegMinSec'

    def typeLatLon = struct([
            latitude : namedDecimalDeg,
            longitude: namedDegMinSec
    ])
    def namedLatLon = model.bind typeLatLon to 'LatLon'

    def typeGeoVolume = struct([
            northEast: namedLatLon,
            southWest: namedLatLon,
            height   : namedReal
    ])
    model.bind typeGeoVolume to 'GeoVolume'

    return model
}

return buildModel()
