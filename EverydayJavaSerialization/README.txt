EverydayJavaSerialization

This class contains a simple Java serialization system using only Oracle Java libraries,
but without reliance on ObjectInputStream and ObjectOutputStream. Instead, it primarily
uses DataInputStream and DataOutputStream over ByteArrayInputStream and 
ByteArrayOutStream respectively and with versions of these classes modified by removing
synchronization for faster run time. It serializes to strings for development convenience 
and provides utilities for writing and reading a string to and from a file with and 
without compression and encryption.  

A serialization string generally consists of a readable part with basic information about 
what is serialized in a Base64 encoded byte array data part. Formats of serialization 
strings vary by serializer which include those for primitive and boxed types, strings, 
enums, arrays, collections, maps, Attributes, BitSets and a master serializer named 
serialize which can call all the rest and handles all other object types provided they
have a no argument constructor. All serialization formats are shown below. The ser 
method is the front end for serialize and is necessary for doing required initialization 
of global variables. For each serializer there is a corresponding deserializer. For 
example the deserialize method is the master deserializer corresponding to serialize and
its front end is the des method, that like ser is necessary for global variable
initialization, which is more complex for deserialization than for serialization since the
former depends on the contents of its serialization string argument that is produced by
the latter.

Here is a example serialization string for testclasses.B where line feeds have been 
inserted for better readability:

testclasses.B
^AAAAAUABmZmZmZmaAQAAAAR3aGF0AAAAEHByb3RlY3RlZCBzdHJpbmcAAAAiYXJyYXleaW50XkFBQUFBd0FBQUFF
QUFBQUNBQUFBQXclWQAAAJ1hcnJheV5qYXZhLmxhbmcuU3RyaW5nXkFBQUFBd0FBQUJ0cVlYWmhMbXhoYm1jdVUzU
nlhVzVuWGtGQlFVRkJNamwxV2xFQUFBQWJhbUYyWVM1c1lXNW5MbE4wY21sdVoxNUJRVUZCUVROU00ySjNBQUFBSF
dwaGRtRXViR0Z1Wnk1VGRISnBibWRlUVVGQlFVSllVbTlqYlZacyVZAAAAdGFycmF5XmphdmEubGFuZy5DaGFyYWN
0ZXJeQUFBQUF3QUFBQmRxWVhaaExteGhibWN1UTJoaGNtRmpkR1Z5WGtGSFJRQUFBQVJPZFd4c0FBQUFGMnBoZG1F
dWJHRnVaeTVEYUdGeVlXTjBaWEplUVVkTiVZAAAAvGNvbGxlY3Rpb25eamF2YS51dGlsLkhhc2hTZXQmYXJyYXlea
mF2YS5sYW5nLk9iamVjdF5BQUFBQXdBQUFCeHFZWFpoTG14aGJtY3VSRzkxWW14bFhsQXZSMXB0V20xYWJWcHZBQU
FBSEdwaGRtRXViR0Z1Wnk1RWIzVmliR1ZlVVVGSFdtMWFiVnB0V204QUFBQWNhbUYyWVM1c1lXNW5Ma1J2ZFdKc1p
WNVJRWEJ0V20xYWJWcHRXUSVZAAAAg2NvbGxlY3Rpb25eamF2YS51dGlsLkFycmF5TGlzdCZhcnJheV5qYXZhLmxh
bmcuT2JqZWN0XkFBQUFBd0FBQUJScVlYWmhMbXhoYm1jdVFtOXZiR1ZoYmw1QlVRQUFBQlJxWVhaaExteGhibWN1U
W05dmJHVmhibDVCUVElWTIwLDE4AAAA7W1hcF5qYXZhLnV0aWwuSGFzaE1hcCZhcnJheV5qYXZhLmxhbmcuT2JqZW
N0XkFBQUFBdyVZMjAsMTgsMjMsNSwyNCw2LDI1LDcmYXJyYXleamF2YS5sYW5nLk9iamVjdF5BQUFBQXdBQUFCaHF
ZWFpoTG14aGJtY3VTVzUwWldkbGNsNUJRVUZCUVZFQUFBQVlhbUYyWVM1c1lXNW5Ma2x1ZEdWblpYSmVRVUZCUVVG
bkFBQUFHR3BoZG1FdWJHRnVaeTVKYm5SbFoyVnlYa0ZCUVVGQmR3JVkyMCwxOCwyMyw1LDI0LDYsMjUsNwAAAEZ0Z
XN0Y2xhc3Nlcy5BXkFBQUFCMmhsYkd4dklHRV5hOmphdmEubGFuZy5TdHJpbmclWTIwLDE4LDIzLDUsMjQsNiwyNS
w3AAAAanRlc3RjbGFzc2VzLkNeQUFBQUNHTWdjM1J5YVc1bkFBQUFDSGdnYzNSeWFXNW5eYzpqYXZhLmxhbmcuU3R
yaW5nXng6amF2YS5sYW5nLlN0cmluZyVZMjAsMTgsMjMsNSwyNCw2LDI1LDc
^z:int
^d:double
^bool:boolean
^s:java.lang.String
^pstr:java.lang.String
^ia:array:int
^sa:array:java.lang.String
^ca:array:java.lang.Character
^set1:collection:java.util.HashSet
^list1:collection:java.util.ArrayList
^map1:map:java.util.HashMap
^a:testclasses.A
^c:testclasses.C
%Y20,18,23,5,24,6,25,7

Sections of this string are separated by "^" and "%Y.  It begins with the serialized 
class name, followed by a long Base64 string which contains data, namely the values of
fields or other serialization strings for fields of complex classes that contain objects
including arrays, collections and maps. Then comes a list of field metadata in a format 
that depends on the field type but always begins with the field name. Finally after "%Y 
is a list of numbers with an even number of elements which is used to build the object
relations map, called rmap, on deserialization. rmap and several other maps are used to 
handle cyclic references and to ensure the serialization data is read appropriately for
each primitive or object serialized. The ordering of field elements in a serialization
string communicates the necessary ordering for the deserialization process using rmap
to resolve cyclic depencencies between objects already deserialized with those still
requiring deserialization.

Advantages of serializing to strings are that it facilitates testing and enables building 
a method heierarchy which helps to reduce code, since a string can include another string.  
An example of this is that if a class contains a field with an array value, that value is 
captured as a serialized string which is then included in the data part of the overall
serialization string for the class. Similarly, collections are basically serialized as a
collection type plus a serialized array and maps are serialized as a map type and two
serialized arrays, one for its keys and the other for the corresponding values.

Deserialization works by reading the serialized class name from a serialization string, 
instantiating it, possibly adding fields or components and returning the instance. As part 
of this process, the data component is converted back to a byte array with which a 
DataInputStream over a ByteArrayInputStream is instantiated. Tracking of fields is done by
saving their names and types to a list during serialization in exactly the same order as 
their values are written to a DataOutputStream over a ByteArrayOutputStream to a byte
array that is then Base64 encoded and plugged into a serialization string, and includin
that list in the serialization string to sequence deserialization. Type information is used
for object instantiation and verification of the current types of fields. If a field is 
missing or its type has changed, deserialization of it is skipped and a message regarding 
that is printed. 

Accomodating classes with constructors that take arguments is mostly straightforward.
For  example, most collections can be constructed from another collection such as list,
which can be generated from an array, which is precisely the data that is serialized for
them. There are cases when constructors with arguments takes more work and introduce
irregularities, but they are still usually relatively easy to handle.

Special cases demonstrate the flexibility of the system. One example is BitSet. The 
serializeBitSet(BitSet) method converts its argument to a byte array using
BitSet.toByteArray(), then turns that into a Base64 string with toB64String() and  
returns the string "java.util.BitSet&" + base64String. deserializeBitSet(String) 
parses the serialization string, converts the Base64 data part to a byte array with
toByteArray() and converts that to a BitSet using the computation provided on
https://docs.oracle.com/javase/8/docs/api/java/util/BitSet.html#toByteArray--.

A second special case is for Enums. A given enum instance is a constant specified by its    
type and name which is a string, so all that is required for serialization is to include    
those which is done with "enum^" + typeName + "^" + enumName where the leading "enum" is
a category type used to enable easy classification of object output types for input
serialization strings. Enum deserialization is done by parsing the serialization string
and reconstructing the enum with Enum.valueOf(typeName, enumName).

A third special case is twofold for Collections regarding ArrayBlockingQueue, which has
no zero argument constructor nor one taking only a collection arg, and for EnumSet which    
instantiators such allOf() and noneOf() require the element type as an argument. A
primary issue is that in order to handle a collection that contains itself as an
element, it's necessary to put the collection object into imap before its array and this
sequence must be preserved during deserialization, but doing that is more difficult when
all its constructors or instantiators require odd arguments especially if data for them 
hasn't been gathered during serialization. For ArrayBlockingQueue this was handled by
working harder get values for its capacity and lock policy during serialization and
inserting them as is into a modified serialization string after typeName with a %Q
prefix just before &array. For EnumSet, however, it seemed easier to proceed with normal
serialization, gathering no type information for its elements, then during deserialization
the system state is saved by making copies of all the global maps and the the oc
variable, then the array is deserialized and the type of its elements determined, then
the state is restored to the save point, the Enum set is instantiated using the element
type, the array is serialized again to put it and its elements into imap in the correct
sequence, and the EnumSum is populated with elements and returned. This may not be the
best way for performance and doesn't typically doesn't matter for EnumSet since it is not
usually circular (however an enum element could contain a field referencing the 
containing EnumSet), but it manages to keep EnumSet within the serializeCollection and 
deserializeCollection umbrella, which is good.

Serialization string and list formats:

What is added by serialize to its list for non null field values:
==================================================================================
field type           string added to list                  # components
===================  ====================================  ============
Primitive or Boxed:  name+":"+typeName                          2

String:              name+":java.lang.String"                   2

Array:               name+":array:"+componentTypeName"          3

Enum:                name+":enum:"+typeName+":"+enumName        4

Collection:          name+":collection:"+typeName               3

Map:                 name+":map:"+typeName"                     3

BitSet:              name+":java.util.BitSet"                   2

AttributesName:      name+":java.util.jar.Attributes.Name"      2

All other:           name+":"+typeName                          2

What is added by serialize to its list for null field values:
(":Null" is appended to the above for each field type)
=================================================================================
field type          string added to list
==================  ========================================
Array:              name+":array:+componentTypeName+":Null"

Enum:               name+":enum:"+typeName+":Null"

Collection:         name+":collection:"+typeName+":Null"

Map:                name+":map:"+typeName+":Null"

All other:          name+":"+typeName+":Null" 

What is returned as a string by the serializers:
(intSeq is a string of integers such as "1,2,3,4...")
=================================================================================
serializer                  format of returned string 
==========================  =====================================================
serialize:                  typeName+"^"+b64String+"^"+list.get(0)+"^"
                               +list.get(1)...+"%Y"+intSeq
                               
serializeNull:              "Null"

serializePrimitiveOrBoxed:  typeName+"^"+b64String

serializeString:            "java.lang.String^"+b64String

serializeArray:             "array^"+componentTypeName+"^"+b64String+"%Y+intSeq

serializeEnum:              "enum^" + typeName+"^"+enumName

serializeCollection:        "collection^"+typeName
                                +"&array^"+componentTypeName+"^"+b64String+"%Y+intSeq
                                
  or for typeName = "java.util.concurrent.ArrayBlockingQueue":  
                            "collection^"+typeName+"%Q[0-9]+,true|false"
                                +"&array^"+componentTypeName+"^"+b64String+"%Y+intSeq
                                                         
serializeMap:               "map^"+typeName
                                +"&array^"+componentTypeName+"^"+b64String+"%Y+intSeq
                                +"&array^"+componentTypeName+"^"+b64String+"%Y+intSeq
                                                
serializeBitSet:            "java.util.BitSet&array^"+byte+"^"+b64String

serializeAttributesName:    "java.util.jar.Attributes.Name^" + b64String

(all the serializers also return "Null" if their argument is null)

