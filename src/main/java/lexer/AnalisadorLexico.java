package lexer;

import model.TipoToken;
import model.Token;

import java.util.ArrayList;
import java.util.List;

public class AnalisadorLexico {

    // LISTA DE PALAVRAS RESERVADAS
    private final List<String> palavrasReservadas =
            List.of(
                    "program",
                    "begin",
                    "end",
                    "if",
                    "then",
                    "else",
                    "while",
                    "do",
                    "write",
                    "read",
                    "var",
                    "array",
                    "integer",
                    "boolean",
                    "char",
                    "true",
                    "false",
                    "and",
                    "or",
                    "not",
                    "div",
                    "procedure",
                    "function"
            );

    /*
     Este método recebe o código fonte
     e devolve uma lista de tokens
     */

    public List<Token> analisar(String codigo) {

        List<Token> tokens = new ArrayList<>();

        int linha = 1;

        // PERCORRER TODO O CÓDIGO
        for (int i = 0; i < codigo.length(); i++) {

            char c = codigo.charAt(i);

            // CONTAGEM DE LINHAS
            if (c == '\n') {
                linha++;
                continue;
            }

            // IGNORAR ESPAÇOS EM BRANCO
            if (Character.isWhitespace(c)) {
                continue;
            }

            // =========================
            // IDENTIFICADORES E KEYWORDS
            // =========================

            if (Character.isLetter(c)) {

                String lexema = "";

                while (i < codigo.length() &&
                        Character.isLetterOrDigit(codigo.charAt(i))) {

                    lexema += codigo.charAt(i);

                    i++;
                }

                i--;

                // VERIFICAR SE É PALAVRA RESERVADA
                if (palavrasReservadas.contains(lexema)) {

                    tokens.add(
                            new Token(
                                    lexema,
                                    TipoToken.PALAVRA_RESERVADA,
                                    linha
                            )
                    );

                } else {

                    tokens.add(
                            new Token(
                                    lexema,
                                    TipoToken.IDENTIFICADOR,
                                    linha
                            )
                    );
                }

                continue;
            }

            // =========================
            // NÚMEROS
            // =========================

            if (Character.isDigit(c)) {

                String numero = "";

                while (i < codigo.length() &&
                        Character.isDigit(codigo.charAt(i))) {

                    numero += codigo.charAt(i);

                    i++;
                }

                i--;

                tokens.add(
                        new Token(
                                numero,
                                TipoToken.NUMERO,
                                linha
                        )
                );

                continue;
            }

            // =========================
            // OPERADOR :=
            // =========================

            if (c == ':') {

                if (i + 1 < codigo.length() &&
                        codigo.charAt(i + 1) == '=') {

                    tokens.add(
                            new Token(
                                    ":=",
                                    TipoToken.OPERADOR,
                                    linha
                            )
                    );

                    i++;

                    continue;
                }
            }

            // =========================
            // OPERADOR <=
            // =========================

            if (c == '<') {

                if (i + 1 < codigo.length() &&
                        codigo.charAt(i + 1) == '=') {

                    tokens.add(
                            new Token(
                                    "<=",
                                    TipoToken.OPERADOR,
                                    linha
                            )
                    );

                    i++;

                    continue;
                }
            }

            // =========================
            // OPERADOR >=
            // =========================

            if (c == '>') {

                if (i + 1 < codigo.length() &&
                        codigo.charAt(i + 1) == '=') {

                    tokens.add(
                            new Token(
                                    ">=",
                                    TipoToken.OPERADOR,
                                    linha
                            )
                    );

                    i++;

                    continue;
                }
            }

            // =========================
            // OPERADOR <>
            // =========================

            if (c == '<') {

                if (i + 1 < codigo.length() &&
                        codigo.charAt(i + 1) == '>') {

                    tokens.add(
                            new Token(
                                    "<>",
                                    TipoToken.OPERADOR,
                                    linha
                            )
                    );

                    i++;

                    continue;
                }
            }

            // =========================
            // OPERADORES SIMPLES
            // =========================

            if (c == '+' ||
                    c == '-' ||
                    c == '*' ||
                    c == '=' ||
                    c == '<' ||
                    c == '>') {

                tokens.add(
                        new Token(
                                String.valueOf(c),
                                TipoToken.OPERADOR,
                                linha
                        )
                );

                continue;
            }

            // =========================
            // DELIMITADORES
            // =========================

            if (c == ';' ||
                    c == ',' ||
                    c == '.' ||
                    c == ':' ||
                    c == '(' ||
                    c == ')' ||
                    c == '[' ||
                    c == ']') {

                tokens.add(
                        new Token(
                                String.valueOf(c),
                                TipoToken.DELIMITADOR,
                                linha
                        )
                );

                continue;
            }

        }

        return tokens;
    }

}