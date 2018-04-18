
function basicAuth() {
    var apiToken = "4JcxcOWF3K1wL2s+2cTmN+r4Z+CY1mcp";
    var passwordIsAuthToken = "PasswordIsAuthToken:{\"apiKey\":\"" + apiToken + "\"}";
    var Base64 = Java.type("java.util.Base64");
    var encodedAuth = Base64.getEncoder().encodeToString(passwordIsAuthToken.bytes);
    return 'apiToken ' + encodedAuth;
}