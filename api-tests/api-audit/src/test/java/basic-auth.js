
function basicAuth() {
    //apiToken needs to be updated accordingly
    var apiToken = "TDcmKa37tYXa1YrcbaJPXbOUwYWrEy+b";
    var passwordIsAuthToken = "PasswordIsAuthToken:{\"apiKey\":\"" + apiToken + "\"}";
    var Base64 = Java.type("java.util.Base64");
    var encodedAuth = Base64.getEncoder().encodeToString(passwordIsAuthToken.bytes);
    return 'apiToken ' + encodedAuth;
}