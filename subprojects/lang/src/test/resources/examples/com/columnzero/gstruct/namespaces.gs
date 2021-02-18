/*+++
[expect]
script.groovy.file = "namespaces.expect.groovy"
 */

bind Stuff: extern('stuff')
bind Things: tuple {
    types extern('thing'), extern('thing'), extern('thing')
}
