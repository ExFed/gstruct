/*+++
[expect]
script.groovy.source = "error com.columnzero.gstruct.model.DuplicateBindingException"
 */

bind Real: extern('double')
bind Real: extern('float')
