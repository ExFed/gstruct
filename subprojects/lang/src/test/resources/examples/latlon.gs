/*+++
[expect]
script.groovy.file = "latlon.expect.groovy"
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

/*
decl GeoVolume: spec {
    description = "A geographic volume defined as a rectangular cuboid."
    type = struct {
        field northEast: spec {
            description = "Northeast-most corner of the volume."
            type = LatLon
        }
        field southWest: spec {
            description = "Southwest-most corner of the volume."
            type = LatLon
        }
        field height: Real
    }
}
 */
