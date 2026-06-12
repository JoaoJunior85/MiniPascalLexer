package ai;

import java.util.prefs.Preferences;

/**
 * Pequena ajuda para armazenar a chave da Gemini localmente.
 * Usa Preferences para simplificar persistência por usuário.
 */
public final class GeminiConfig {

    private static final Preferences PREF = Preferences.userNodeForPackage(GeminiConfig.class);
    private static final String KEY = "gemini_api_key";

    private GeminiConfig() {}

    public static String getStoredApiKey() {
        return PREF.get(KEY, null);
    }

    public static void setStoredApiKey(String key) {
        if (key == null || key.isBlank()) PREF.remove(KEY);
        else PREF.put(KEY, key);
    }

    public static String getEffectiveApiKey() {
        String env = System.getenv("GEMINI_API_KEY");
        if (env != null && !env.isBlank()) return env;
        return getStoredApiKey();
    }
}
