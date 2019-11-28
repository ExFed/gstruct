# gStruct Design Notes

## Grammar

In an eBNF-like syntax:

```
RELATIONSHIP := RELTYPE QNAME: SPEC {, QNAME: SPEC }

RELTYPE := STRUCT | NAMESPACE | TYPE
QNAME := { NAME QDELIM } NAME

NAME := /[-_A-Za-z0-9]+/
QDELIM := '.'

STRUCT := 'struct'
NAMESPACE := 'namespace'
TYPE := 'type'

SPEC := [QNAME] SPECOBJ

SPECOBJ := '{' RELATIONSHIP { LF RELATIONSHIP } '}'

LF := /\r?\n/
```
