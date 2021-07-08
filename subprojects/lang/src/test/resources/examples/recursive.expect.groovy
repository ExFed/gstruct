package examples

import com.columnzero.gstruct.model.Identifier
import com.columnzero.gstruct.model.NameRef
import com.columnzero.gstruct.model.NominalModel
import groovy.transform.CompileStatic

import static com.columnzero.gstruct.model.Extern.extern
import static com.columnzero.gstruct.model.Type.ref
import static com.columnzero.gstruct.model.Tuple.tuple

@CompileStatic
static NominalModel buildModel() {
    def model = new NominalModel()

    def externString = extern 'string'
    NameRef stringListRef // forward declare for recursive declaration
    def stringListSpec = ref { tuple(externString, stringListRef) }
    stringListRef = model.bind(Identifier.name('StringList'), stringListSpec)

    return model
}

return buildModel()
