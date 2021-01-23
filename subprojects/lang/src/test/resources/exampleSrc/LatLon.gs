/*+++
[expect]
script.groovy.file = "LatLon.groovy"
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
