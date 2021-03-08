typedef struct {
    double _0;
} DecimalDeg;

typedef struct {
    Int _0;
    Int _1;
    Real _2;
} DegMinSec;

typedef struct {
    LatLon northEast;
    LatLon southWest;
    Real height;
} GeoVolume;

typedef int Int;

typedef struct {
    DecimalDeg latitude;
    DegMinSec longitude;
} LatLon;

typedef float Real;
