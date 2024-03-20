# Hiss [![build status](https://github.com/Tap30/hiss/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/Tap30/hiss/actions/workflows/build.yml)

Hiss is an encryption and hashing library which aims to make encrypting
domain objects easier while support key rotation and even algorithm change.

Key Features:
- Key rotation and algorithm change by storing key ID and algorithm name alongside encrypted content.
- Supporting multiple algorithms.
- Supports encrypted and not encrypted data during decryption.
- Spring Boot and Spring Data Mongo integration support.

## Quick Start

### 1. Add Hiss dependency

Apache Maven:
```xml
<dependency>
    <groupId>tech.tapsi.libs.hiss</groupId>
    <artifactId>hiss</artifactId>
    <version>0.11.1</version>
</dependency>
```

Gradle (Groovy):
```groovy
implementation 'tech.tapsi.libs.hiss:hiss:0.11.1'
```

Gradle (Kotlin):
```kotlin
implementation("tech.tapsi.libs.hiss:hiss:0.11.1")
```

### 2. Create Hiss instance via `HissFactory`

```java
var hiss = HissFactory.createHiss(() -> HissProperties.fromBase64EncodedKeys(
    Map.of("default_key", "AAAAAAAAAAAAAAAAAAAAAA=="),
    "default_key",
    "aes-128-gcm",
    "default_key",
    "hmac-sha256"
));
```

### 3. Annotate your class with `@Encrypted`

```java
import tech.tapsi.libs.hiss.Encrypted;
import tech.tapsi.libs.hiss.EncryptedInside;

public class User {
    @Encrypted
    private String phoneNumber;
    private String hashedPhoneNumber;
    @EncryptedInside
    private List<Address> addresses;

    // getters and setters
}

public class Address {
    private String name;
    private String street;
    private String city;
    private String state;
    @Encrypted(hashingEnabled = false)
    private String postalCode;

    // getters and setters
}
```

Note: Getters and setters must exist as Hiss use them to get/set values.

### 4. Call `hiss.encryptObject(user)` to encrypt the object and `hiss.decryptObject(user)` to decrypt it.

By calling those method, all annotated fields will be encrypted/decrypted;
also, during encryption, the hash of the content will be calculated and stored
in `hashedX` (in this case `hashedPhoneNumber`).

## Hiss and Spring Boot

Hiss integration with Spring Boot is available;
see [this](https://github.com/Tap30/hiss-spring-boot-starter).

## Hiss Instantiation

For creating a Hiss instance, `HissFactory$createHiss(HissPropertiesProvider)` can be used;
`HissPropertiesProvider` is an interface which its implementations will create a `HissProperties` instance.

### HissProperties

`HissProperties` has all the configuration Hiss needs.

`HissProperties` contains:
- `keys`: the map of key ID to key bytes; Hiss uses this map to encrypt and decrypt contents.
- `defaultEncryptionKeyId`: the key ID which Hiss uses to encrypt contents.
- `defaultEncryptionAlgorithm`: the algorithm which Hiss uses to encrypt contents.
- `defaultHashingKeyId`: the key ID which Hiss uses to create hash of contents.
- `defaultHashingAlgorithm`: the algorithm which Hiss uses to create hash of contents.

### HissPropertiesFromEnvProvider

`HissPropertiesProvider` should be implemented by the client of Hiss, but there is a `HissPropertiesFromEnvProvider`
class which, as the name suggest, creates `HissProperties` from environment variables:

`HISS_KEYS_(Key ID)`: mapped to `keys`; all environment variables which start with `HISS_KEYS_` expected to be base64 encoded keys;
for example `HISS_KEYS_KEY_1=AAAAAAAAAAAAAAAAAAAAAA==` will be a key with ID `key_1` and value of 16 zero bytes.

`HISS_DEFAULT_ENCRYPTION_KEY_ID`: mapped to `defaultEncryptionKeyId` 

`HISS_DEFAULT_ENCRYPTION_ALGORITHM`: mapped to `defaultEncryptionAlgorithm` 

`HISS_DEFAULT_HASHING_KEY_ID`: mapped to `defaultHashingKeyId` 

`HISS_DEFAULT_HASHING_ALGORITHM`: mapped to `defaultHashingAlgorithm` 

## Encryption

By using `@Encrypted` annotation and calling `Hiss$encryptObject(Object)`,
Hiss will know which fields are needed to be encrypted;
also, there are `Hiss$encrypt(String)` and `Hiss$encrypt(String, String)` methods,
which just encrypt provided string and return encrypted content.

Hiss is smart enough to know if a field requires encryption/decryption; in another word
Hiss won't re-encrypt encrypted content and decrypt plain text.  

Supported algorithms are:
- `aes-128-gcm`: which translates to `AES/GCM/NoPadding` having 16 bytes for tag and 16 bytes for IV. 
- `aes-128-cbc`: which translates to `AES/CBC/PKCS5Padding` having 16 bytes for IV.

Encrypted data has the following format: `#$$#{algorithm:key_id}{encypted_content}#$$#`;
which `key_id` is the ID of the key that the content was encrypted with,
`algorithm` is the algorithm name mentioned above and
`encypted_content` is base64 encoding of iv bytes + encrypted content bytes.

Hiss will use getters and setters to read/change value of a field.

### Partial Encryption

With Hiss it is possible to encrypt only some parts of an string.

As an example having:

```java
import tech.tapsi.libs.hiss.Encrypted;

public class Message {
    
    @Encrypted(pattern = "\\d+")
    private String content = "Your code is 123456";
    private String hashedContent;
    
    // getters and setter
}
```

after encryption `content` will be `Your code is #$$#{aes-128-gcm:default_key}{RCnPlJc5H/yeygBEm0wBpgZBrtlOlvWetHpUaBO3oqQSrQXARSw=}#$$#`
and `hashedContent` will be `#$$#{hmac-sha256:default_key}{29l4zNu+i23nf2s3td+bW2Kn6JKlAcO1PqoWsIOL1e0=}#$$#`.

### `@Encrypted` Annotation

`@Encrypted` annotation can be applied on class fields
by which Hiss will know which fields should be encrypted/decrypted.

Only `String` fields can be annotated with `@Encrypted`;
for other (non-primitive) types use [`@EncryptedInside`](#encryptedinside-annotation).

`@Encrypted` parameters are:
- `pattern`: the pattern which only matched content will be encrypted;
empty or null values mean all content should be encrypted.
- `hashingEnabled`: should Hiss calculate and store hash of content.
- `hashFieldName`: name of the field in which hashed content will be put;
empty or null values mean the name will be guessed.
The guessing algorithm is by concatenating "hashed" and first-letter-capitalized field name.
e.g. if the name of the encrypted field is `phoneNumber`,
guessed hashed field name is `hashedPhoneNumber`.

### `@EncryptedInside` Annotation

`@EncryptedInside` tells Hiss to scan fields inside object.

Having `T` as a non-primitive type, `T`, subtypes of `Iterable<T>` (e.g. `Set<T>`, `List<T>`, ...) and,
subtypes of `Map<?, T>` can be annotated with `@EncryptedInside`.

Examples below are valid:

```java
public class ValidEncryptedInsideUsage {
    @EncryptedInside
    private Address address;
    
    @EncryptedInside
    private List<Address> addressList;
    
    @EncryptedInside
    private Map<String, Address> addressMap;
    
    // getters and setters ...
}
```

## Hashing

As we may want to search on encrypted content and as Hiss adds a random IV to each encrypted content,
no-salt hashing is required.

Hiss supports keyed hash functionality; currently `hmac-sha256` is only supported.

Hashed content format is like encrypted content.
