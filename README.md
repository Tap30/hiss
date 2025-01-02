# Hiss

Hiss is a Java/Kotlin field-level encryption and hashing library
which lets you encrypt and calculate hash of selected (annotated) fields of an object.

It is most useful when you want to persist or send an object which has sensitive fields.

The motivation behind this project was we wanted to
encrypt [personally identifiable information (PII)](https://en.wikipedia.org/wiki/Personal_data)
of our users prior to persisting them in the database that in case of
a data breach or unauthorized access, user identities would be protected.

## Contents

* [Quick Start](#quick-start)
    * [Adding Hiss Dependency](#adding-hiss-dependency)
    * [Creating Hiss Instance](#creating-hiss-instance)
    * [Annotating Fields](#annotating-fields)
    * [Encrypting Object](#encrypting-object)
* [How does Hiss work?](#how-does-hiss-work)
    * [Overview](#overview)
    * [Use of Getters and Setters](#use-of-getters-and-setters)
    * [Nested Classes and Usage of `@EncryptedInside`](#nested-classes-and-usage-of-encryptedinside)
    * [Hash Calculation](#hash-calculation)
    * [Partial Encryption](#partial-encryption)
    * [Supported Algorithms](#supported-algorithms)
* [Hiss Instantiation](#hiss-instantiation)
    * [Hiss Properties](#hiss-properties)
        * [Creating Properties From Environment Variables](#creating-properties-from-environment-variables)
    * [Key Integrity Validation](#key-integrity-validation)

## Quick Start

Using Hiss is straight forward; by adding Hiss dependency and annotating your classes you're good to go.

**Hiss is also integrated with Spring Data
Mongo. [Check this out to find out more](https://github.com/Tap30/hiss-spring-boot-mongo-starter).**

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

### Creating Hiss Instance

To create an Hiss instance, keys and default encryption and hashing algorithms must be configured;
below is simple configuration by which an Hiss instance can be created. For more details [see here](#hiss-instantiation).

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

And in Kotlin (`@Encrypted` can be only applied on `var` fields):

```kotlin
import io.github.tap30.hiss.Encrypted
import io.github.tap30.hiss.EncryptedInside

data class User(
    val name: String,
    @Encrypted
    var phoneNumber: String,
    var hashedPhoneNumber: String,
    @EncryptedInside
    val addresses: List<Address>
)

data class Address(
    val name: String,
    val street: String,
    val city: String,
    val state: String,
    @Encrypted(hashingEnabled = false)
    val postalCode: String
)
```

### Encrypting Object

By simply calling `hiss.encryptObject(user)`, the annotated fields will be encrypted
and their hash string (if enabled) will be calculated.

Also, by calling `hiss.decryptObject(user)`, the annotated fields will be decrypted.

All methods in Hiss class are idempotent; meaning calling `encryptObject` twice,
won't result in encrypting fields twice and as for decryption, plain texts will be left untouched.

There is a [sample application in tests](src/test/java/io/github/tap30/hissapp/Application.java)
which demonstrates more use case of Hiss.

## How does Hiss work?

### Overview

When you call `encryptObject` of Hiss, it'll scan all fields of the object using Java reflection.
String fields annotated with `@Encrypted`, will be read using getters of the fields,
and their value will be encrypted and their hash will be calculated, and the encrypted content and hash value
will be set using the fields setters.

The story is same for `decryptObject` while it only decrypts those fields.

The object field scanning will be done once per class and the scan result will be cached.

### Use of Getters and Setters

As stated earlier, Hiss won't change value of fields directly. It relies on getters and setters
of the fields. Thus, it is necessary to implement those in Java classes.

#### Kotlin Data Classes

For Kotlin data classes, no getters or setters are needed for `var` fields
as they are automatically generated in their Java representation.

As `val` fields are immutable, `@Encrypted` can't be used on them.

### Nested Classes and Usage of `@EncryptedInside`

Recall the `addresses` field of the `User` class in [Quick Start: Annotating Fields](#annotating-fields),
we've annotated the `addresses` field with `@EncryptedInside` to tell Hiss to scan fields inside them.

`@EncryptedInside` can be used on non-primitive fields,
subtypes of `Iterable<T>` (e.g. `Set<T>`, `List<T>`, ...) and, subtypes of `Map<?, T>`.

Here is an example of valid `@EncryptedInside` usages:

```java
public class ValidEncryptedInsideUsage {
    @EncryptedInside
    private Address address;
    
    @EncryptedInside
    private List<Address> addressList;
    
    @EncryptedInside
    private Map<String, Address> addressMap;
    
    // getters ...
}
```

### Hash Calculation

Hash values of fields are most useful when you want to search on these fields.

Unless you disable hash calculation of a field (i.e `@Encrypted(hashingEnabled = false)`),
during encryption, the hash value of the field will also be calculated.

The hash value will be stored in a different field having the pattern `hashed<FIELD NAME>`;
for example the field for storing hash value of field `phoneNumber`, will be `hashedPhoneNumber`.
You can provide your custom hash field name in `@Encrypted` annotation field `hashFieldName`.
Here's an example in which we want to store hash value of `phoneNumber` in `searchablePhoneNumber`:

```java
public class User {
    @Encrypted(hashFieldName="searchablePhoneNumber")  
    private String phoneNumber;
    private String searchablePhoneNumber; // The hash value will be stored here.
  
    // getters and setters ...
}
```

### Partial Encryption

By setting `pattern` in `@Encrypted`, only parts matched with the pattern will be encrypted and hashed.

For example, having:

```java
public class Message {

  @Encrypted(pattern = "\\d+")
  private String content;
  private String hashedContent;

  // getters and setters ...
}

var message = new Message();
message.setContent("User 123 called you.");

hiss.encryptObject(message);

System.out.println(message.getContent());
System.out.println(message.getHashedContent());
```

will result in:

```
User #$$#{aes/gcm/nopadding:default_key}{anibgQ6BsnMbFz5+mtNENjE1ioAaOm5J7T4pyEIhEKTiqeY=}#$$# called you.
User #$$#{hmacsha256:default_key}{wMwN/frvI3Dk1WcRF1/jSd727Uy6JdPHoB/G72VoIg0=}#$$# called you.
```

### Supported Algorithms

For encryption, these algorithms are supported:

- [AES/CBC/PKCS5Padding](src/main/java/io/github/tap30/hiss/encryptor/impl/AesCbcPkcs5PaddingEncryptor.java)
- [AES/GCM/NoPadding](src/main/java/io/github/tap30/hiss/encryptor/impl/AesGcmNoPaddingEncryptor.java)

For hashing, only [HmacSHA256](src/main/java/io/github/tap30/hiss/hasher/impl/HmacSha256Hasher.java) is supported.

By implementing [`Encryptor`](src/main/java/io/github/tap30/hiss/encryptor/Encryptor.java)
and [`Hasher`](src/main/java/io/github/tap30/hiss/hasher/Hasher.java) interfaces, you can provide
your own algorithms. We'll talk more about it in [Hiss Instantiation](#hiss-instantiation).

## Hiss Instantiation

Hiss can be instantiated using [`HissFactory`](src/main/java/io/github/tap30/hiss/HissFactory.java)'s `createHiss` methods.

`createHiss` method requires [`HissProperties`](src/main/java/io/github/tap30/hiss/properties/HissProperties.java) instances.
In the overloaded method, it accepts sets of `Encryptor`s and `Hasher`s by which you can provide your own custom
algorithm implementations. The default algorithms will be available.

### Hiss Properties

[`HissProperties`](src/main/java/io/github/tap30/hiss/properties/HissProperties.java)
can be created using its builder, using environment variables, or by implementing
[`HissPropertiesProvider`](src/main/java/io/github/tap30/hiss/properties/HissPropertiesProvider.java)
and passing it to `HissProperties.withProvider`.

Here are the fields in `HissProperties`:

```java
/**
 * Pairs of key ID (name) to key.
 */
Map<String, Key> keys;
/**
 * The key ID of the key by which encryption will be done. It must exist in `keys` map.
 */
String defaultEncryptionKeyId;
/**
 * The algorithm name by which encryption will be done.
 * It must exist among default or custom encryption algorithms.
 */
String defaultEncryptionAlgorithm;
/**
 * The key ID of the key by which hashing will be done. It must exist in `keys` map.
 */
String defaultHashingKeyId;
/**
 * The algorithm name by which hashing will be done.
 * It must exist among default or custom hashing algorithms.
 */
String defaultHashingAlgorithm;
/**
 * Whether to generate keys' hashes on Hiss instantiation. 
 */
boolean keyHashGenerationEnabled;
```

Creating `HissProperties` using its builder is straight-forward and explained in [Quick Start](#create-hiss-instance).
In the following, we'll describe creating key from environment variables.

#### Creating Properties From Environment Variables

By calling `fromEnv` function of `HissProperties`, `HissProperties` will be created.

Here are the mapping of the properties fields to environment variables:

- `keys`:
    - `HISS_KEYS_{Key ID}`: the base64 encoded representation of the key.
    - `HISS_KEYS_{Key ID}__HASH`: the hash of the key.
- `defaultEncryptionKeyId`: `HISS_DEFAULT_ENCRYPTION_KEY_ID`
- `defaultEncryptionAlgorithm`: `HISS_DEFAULT_ENCRYPTION_ALGORITHM`
- `defaultHashingKeyId`: `HISS_DEFAULT_HASHING_KEY_ID`
- `defaultHashingAlgorithm`: `HISS_DEFAULT_HASHING_ALGORITHM`
- `keyHashGenerationEnabled`: `HISS_KEY_HASH_GENERATION_ENABLED`

Below is a full working set of envs having two keys IDed `default_key` and `old_key`:

```bash
HISS_KEYS_DEFAULT_KEY='AAAAAAAAAAAAAAAAAAAAAA=='
HISS_KEYS_DEFAULT_KEY___HASH='$2a$12$3T0VMnGMgvesehYomommnO02dbFOJuM/3elsmgmsB2/qlGSF3BIbe'

HISS_KEYS_OLD_KEY='AQIDBAUGBwgJCgsMDQ4PEA=='
HISS_KEYS_OLD_KEY___HASH='$2a$12$THkoYZHlqD/HvrSkKUDs9eyHwY7W2FmyJm6SMp4xeGfP2g7F6Ro/i'

HISS_DEFAULT_ENCRYPTION_KEY_ID='default_key'
HISS_DEFAULT_ENCRYPTION_ALGORITHM='aes-128-gcm'

HISS_DEFAULT_HASHING_KEY_ID='default_key'
HISS_DEFAULT_HASHING_ALGORITHM='hmac-sha256'

HISS_KEY_HASH_GENERATION_ENABLED='true'
```

### Key Integrity Validation

Above we've seen "key hash". By setting `keyHashGenerationEnabled` to `true`, Hiss, upon instantiation, will
print hashes of the keys on the console.

Later by providing these hashes, Hiss will make sure integrity of keys will be left untouched;
this should hopefully prevent accidental key change or manipulation 🤞.
