package examples.com.columnzero.gstruct

import com.columnzero.gstruct.model.NominalModel
import groovy.transform.CompileStatic

import static com.columnzero.gstruct.model.Extern.extern
import static com.columnzero.gstruct.model.Tuple.tuple

@CompileStatic
static NominalModel buildModel() {
    def model = new NominalModel()

    def externThing = extern 'thing'
    def tupleThings = tuple externThing, externThing, externThing
    model.bind tupleThings to 'com', 'columnzero', 'gstruct', 'Things'

    def typeStuff = extern 'stuff'
    model.bind typeStuff to 'com', 'columnzero', 'gstruct', 'Stuff'

    return model
}

return buildModel()
