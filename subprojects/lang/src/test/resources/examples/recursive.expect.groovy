package examples

import com.columnzero.gstruct.model.NameRef
import com.columnzero.gstruct.model.NominalModel
import com.columnzero.gstruct.model.Ref
import com.columnzero.gstruct.model.Tuple
import groovy.transform.CompileStatic

import static com.columnzero.gstruct.model.Extern.extern
import static com.columnzero.gstruct.model.Ref.constRef
import static com.columnzero.gstruct.model.Tuple.tuple

@CompileStatic
static Iterable<NameRef<?>> buildModel() {

    def externString = extern 'string'
    NameRef<Tuple> namedStringList
    Ref<Tuple> consStringList = { tuple constRef(externString), namedStringList }
    namedStringList = NameRef.of consStringList named 'StringList'

    return [namedStringList] as Iterable<NameRef<?>>
}

return NominalModel.of(buildModel())
