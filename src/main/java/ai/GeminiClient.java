package ai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GeminiClient {

    private static final String MODEL = "gemini-2.5-flash";

    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    private static final HttpClient CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

    private GeminiClient() {
    }

    public static String corrigirCodigo(String codigo, String apiKey)
            throws IOException, InterruptedException {

        String prompt =
                "Você é um especialista em MiniPascal.\n"
                        + "INSTRUÇÕES CRÍTICAS:\n"
                        + "1. NÃO altere a lógica do código.\n"
                        + "2. NÃO reescreva ou optimize nada.\n"
                        + "3. NÃO adicione escape sequences (como \\u003e, \\n escapados, etc).\n"
                        + "4. NÃO remova operadores ou tokens - se estão errados, comente-os com {* comentário *} ou deixe como está.\n"
                        + "5. APENAS remova caracteres inválidos (acentos errados, símbolos estranhos não-MiniPascal).\n"
                        + "6. APENAS corrija digitação e espaçamento em tokens.\n"
                        + "7. Mantenha o estilo, indentação e estrutura intactos.\n"
                        + "8. Retorne SOMENTE o código corrigido, sem explicações nem markdown.\n\n"
                        + "Código:\n"
                        + codigo;

        String payload =
                "{"
                        + "\"contents\":["
                        + "{"
                        + "\"parts\":["
                        + "{"
                        + "\"text\":\"" + escapeJson(prompt) + "\""
                        + "}"
                        + "]"
                        + "}"
                        + "]"
                        + "}";

        String endpoint =
                BASE_URL
                        + MODEL
                        + ":generateContent?key="
                        + apiKey;

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .timeout(Duration.ofSeconds(60))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                payload,
                                StandardCharsets.UTF_8))
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString(
                                StandardCharsets.UTF_8
                        )
                );

        if (response.statusCode() != 200) {

            throw new IOException(
                    "Erro Gemini "
                            + response.statusCode()
                            + "\n\n"
                            + response.body()
            );
        }

        String texto = extrairTexto(response.body());
        return limparEscapeSequences(texto);
    }

    private static String limparEscapeSequences(String texto) {
        // Remove escape sequences Unicode residuais (\u003e -> >, etc)
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher(texto);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            try {
                int code = Integer.parseInt(m.group(1), 16);
                m.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf((char) code)));
            } catch (NumberFormatException e) {
                // Ignora se não conseguir decodificar
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String extrairTexto(String json)
            throws IOException {

        Pattern pattern = Pattern.compile(
                "\"text\"\\s*:\\s*\"(.*?)\"",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return unescapeJson(matcher.group(1));
        }

        throw new IOException(
                "Não foi possível encontrar texto na resposta:\n"
                        + json
        );
    }

    private static String escapeJson(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescapeJson(String text) {

        return text
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}