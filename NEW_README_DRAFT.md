# Hiss

Hiss a field-level encryption and hashing Java library
which is most used when you want to encrypt some fields of an object before
persisting it or passing it (e.g. over network).

It lets you encrypt only selected (annotated) fields in your Java and Kotlin classes.
It can also calculate hashes of those fields by which you can query on them.

## Contents

* Quick Start
  * Adding Hiss Dependency
  * Creating Hiss Instance
  * Annotating Fields
  * Encrypting Object
* Hiss Annotations
  * `@Encrypted`
  * `@EncryptedInside`
* Hiss Instantiation
  * Key Validation
* Hiss Properties
  * Properties Provided in Code
  * Properties Provided by Environment Variables
* Hiss Encryption 
  * Partial Encryption
  * Supported Encryption Algorithms
  * Extending Encryption Algorithms
  * Key and Algorithm Rotation
* Hiss Hashing
  * Supported Hashing Algorithms
  * Extending Hashing Algorithms
* Hiss and Spring Data
