package co.leantechniques.hygieia.rally;

public class RuntimeEnvironment {
    public static String getRallyUsername() {
        return System.getenv("RALLY_USERNAME");
    }

    public static String getRallyPassword() {
        return System.getenv("RALLY_PASSWORD");
    }
}
