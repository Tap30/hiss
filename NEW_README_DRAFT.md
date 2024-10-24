# Hiss

Hiss is a Java/Kotlin field-level encryption and hashing library
which lets you encrypt and calculate hash of selected (annotated) fields of an object.

It is most useful when you want to persist or send an object which has sensitive fields.

The motivation behind this project was we wanted to encrypt [personally identifiable information (PII)](https://en.wikipedia.org/wiki/Personal_data)
of our users prior to persisting them in the database that in case of
a data breach or unauthorized access, user identities would be protected. 

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
  * Hiss Properties
    * Properties Provided in Code
    * Properties Provided by Environment Variables
  * Key Validation
* Hiss Encryption 
  * Partial Encryption
  * Supported Encryption Algorithms
  * Extending Encryption Algorithms
  * Key and Algorithm Rotation
* Hiss Hashing
  * Supported Hashing Algorithms
  * Extending Hashing Algorithms
* Hiss and Spring Data

## Quick Start

Using Hiss is straight forward; by adding Hiss dependency and annotating your classes you're good to go.

Hiss is also integrated with Spring Data Mongo. [Check this out to find out more](https://github.com/Tap30/hiss-spring-boot-mongo-starter).

### Adding Hiss Dependency

Apache Maven:
```xml
<dependency>
    <groupId>io.github.tap30</groupId>
    <artifactId>hiss</artifactId>
    <version>0.12.0</version>
</dependency>
```

Gradle (Groovy):
```groovy
implementation 'io.github.tap30:hiss:0.12.0'
```

Gradle (Kotlin):
```kotlin
implementation("io.github.tap30:hiss:0.12.0")
```

### Create Hiss Instance

**(TODO: Links)**
To create an Hiss instance, keys and default encryption and hashing algorithms must be configured;
below is simple configuration by which an Hiss instance can be created. For more details [see here].

```java
var properties = HissProperties.builder()
    .keys(Set.of(Key.builder()
           .id("default_key")
           .key(Base64.getDecoder().decode("AAAAAAAAAAAAAAAAAAAAAA==")) // 
           .keyHash("$2a$12$3T0VMnGMgvesehYomommnO02dbFOJuM/3elsmgmsB2/qlGSF3BIbe")
           .build()))
    .defaultEncryptionKeyId("default_key")
    .defaultEncryptionAlgorithm("AES/GCM/NoPadding")
    .defaultHashingKeyId("default_key")
    .defaultHashingAlgorithm("HmacSHA256")
    .keyHashGenerationEnabled(false)
    .build();

var hiss = HissFactory.createHiss(properties);
```

### Annotating Fields

Assume we have a `User` class containing a phone number and a list of `Address`es;
the `Address` class contains postal code alongside other fields.

We want to make sure phone number is encrypted and its hash is calculated by which
we can search for a user by his/her phone number.
We also want to encrypt his/her postal code but the postal code is not searchable
(at least in our imaginary app 😌).

Here will be the code in Java:

```java
import io.github.tap30.hiss.Encrypted;
import io.github.tap30.hiss.EncryptedInside;

public class User {
    private String name;
    @Encrypted
    private String phoneNumber;
    private String hashedPhoneNumber; // Hiss will automatically fill this field.
    @EncryptedInside
    private List<Address> addresses;

    // Getters and setters; Hiss will use these!
}

public class Address {
    private String name;
    private String street;
    private String city;
    private String state;
    @Encrypted(hashingEnabled = false)
    private String postalCode;

    // Getters and setters; Hiss will use these!
}
```

**(TODO)** And in Kotlin:

```kotlin

```

### Encrypting Object

By simply calling `hiss.encryptObject(user)`, the annotated fields will be encrypted
and their hash string (if enabled) will be calculated.

Also, by calling `hiss.decryptObject(user)`, the annotated fields will be decrypted.

All methods in Hiss class are idempotent; meaning calling `encryptObject` twice,
won't result in encrypting fields twice and as for decryption, plain texts will be left untouched.

There is a [sample application in tests](src/test/java/io/github/tap30/hissapp/Application.java)
which demonstrates more use case of Hiss. 