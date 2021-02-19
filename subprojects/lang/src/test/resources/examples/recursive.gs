/*+++
[expect]
script.groovy.file = "recursive.expect.groovy"
 */

bind StringList: tuple {
    types extern('string'), StringList
}
