/*+++
[expect]
script.groovy = """\
    throw new com.columnzero.gstruct.lang.compile.BindingCompiler.BindingException()
"""
 */

bind Real: extern('double')
bind Real: extern('float')
