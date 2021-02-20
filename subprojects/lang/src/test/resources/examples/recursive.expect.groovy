package examples

import com.columnzero.gstruct.model.Identifier
import com.columnzero.gstruct.model.NameRef
import com.columnzero.gstruct.model.NominalModel
import groovy.transform.CompileStatic

import static com.columnzero.gstruct.model.Extern.extern
import static com.columnzero.gstruct.model.Ref.constRef
import static com.columnzero.gstruct.model.Ref.ref
import static com.columnzero.gstruct.model.Tuple.tuple

@CompileStatic
static NominalModel buildModel() {
    def model = new NominalModel()

    def externString = extern 'string'
    NameRef namedStringList // forward declare to call recursively
    def consStringList = ref { tuple(constRef(externString), namedStringList) }
    namedStringList = model.bind(Identifier.name('StringList'), consStringList)

    return model
}

return buildModel()
