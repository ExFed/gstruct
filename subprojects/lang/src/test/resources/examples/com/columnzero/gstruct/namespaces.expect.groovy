package examples.com.columnzero.gstruct

import com.columnzero.gstruct.model.NameRef
import com.columnzero.gstruct.model.NominalModel
import groovy.transform.CompileStatic

import static com.columnzero.gstruct.model.Extern.extern
import static com.columnzero.gstruct.model.Tuple.tuple

@CompileStatic
static NominalModel expect() {
    def externThing = extern 'thing'
    def tupleThings = tuple externThing, externThing, externThing
    def namedThings = NameRef.of tupleThings named 'com', 'columnzero', 'gstruct', 'Things'

    def typeStuff = extern'stuff'
    def namedStuff = NameRef.of typeStuff named 'com', 'columnzero', 'gstruct', 'Stuff'

    return NominalModel.of([namedThings, namedStuff] as Iterable)
}

return expect()
