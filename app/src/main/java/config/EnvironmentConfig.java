package config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentConfig {

    private static final Dotenv dotenv;

    static {
        dotenv = Dotenv.configure().load();

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    }

    public static void load() {
    }

    public static String getEnv(String key) {
        return dotenv.get(key);
    }
}