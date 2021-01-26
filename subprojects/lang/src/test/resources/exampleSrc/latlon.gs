/*+++
[expect]
script.groovy.file = "latlon.expect.groovy"
 */

println "### LatLon script: ${getClass()}"

tuple {
    println "### LatLon = ${LatLon}"
    println "### this.LatLon = ${this.LatLon}"
    println "### this = $this"
    assert LatLon == this.LatLon
}

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
