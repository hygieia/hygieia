# Hygieia℠ UI Tests

This contains a Serenity based project for testing the Hygieia UI. It is important to ensure that changes made in the development process will not break existing functionality, and having a UI test suite can accomplish that. The foundation Serenity provides is a BDD mentality allowing the easy addition of use cases and meaningful reporting. It can also be easily integrated into the developement pipeline.

## Building

Run `mvn verify` from the UI-test project to run the UI tests.

**Note:** If running tests that perform valid log in operations, you will need to have your encrypted password in application.properties file, and the following maven parameter passed in `-Duitest.secret=[yourEncryptionKey]`


## uitest.properties file

The UI-test layer needs a property file in following format:

```properties
# uitest.properties
UITEST_PROXY_URL=[Proxy URL - if you are using a proxy, put the url here]
UITEST_PROXY_USERNAME=[your proxy username]
UITEST_PROXY_PASSWORD=[your encrypted proxy password]
UITEST_EXISTING_USER=[Test Username used to login - REQUIRED]
UITEST_EXISTING_USERS_PASSWORD=[Test Password used to login - REQUIRED (Must be encrypted, see section below for encrypting)]
```

## A note on proxies...
If you are not using a proxy, you can omit the UITEST_PROXY_URL and UITEST_PROXY_PASSWORD from your uitest.properties file, but you must also
remove lines 20 & 21 from HygieiaPhantomJSDriver.
```
.withProxy()
.withProxyAuth()
```

## Encrypting Passwords

There is a class in the UI-Tests project that assists you in creating encrypted credentials. The EncryptionHelper guides you through the process of creating a new encryption key, or encrypting new fields to be used in the UI-tests. Run the EncryptionHelper class to get an encryption key or to encrypt new fields.

**WARNING:** The EncryptionHelper generates a new key each time the class is run. If you already have a key, **IGNORE THE NEWLY GENERATED ONE**. Ask your team lead for the main key used for encryption/decryption. Using multiple keys may cause confusion.
