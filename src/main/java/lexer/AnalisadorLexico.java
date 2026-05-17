package lexer;

import model.TipoToken;
import model.Token;

import java.util.ArrayList;
import java.util.List;

public class AnalisadorLexico {

    // =========================================================
    // LISTA DE PALAVRAS RESERVADAS DO MINI-PASCAL
    // =========================================================

    private final List<String> palavrasReservadas = List.of(

            "program",
            "begin",
            "end",
            "if",
            "then",
            "else",
            "while",
            "do",
            "string",
            "write",
            "writeln",
            "read",
            "readln",
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

    // =========================================================
    // MÉTODO PRINCIPAL DO ANALISADOR LÉXICO
    // =========================================================
    /*
        Este método recebe o código fonte escrito
        em mini-Pascal e devolve uma lista de tokens.
     */

    public List<Token> analisar(String codigo) {

        // LISTA QUE ARMAZENARÁ TODOS OS TOKENS
        List<Token> tokens = new ArrayList<>();

        // VARIÁVEL RESPONSÁVEL PELA CONTAGEM DE LINHAS
        int linha = 1;

        // =====================================================
        // PERCORRER TODO O CÓDIGO FONTE CARACTERE POR CARACTERE
        // =====================================================

        for (int i = 0; i < codigo.length(); i++) {

            // CARACTERE ACTUAL
            char c = codigo.charAt(i);

            // =================================================
            // CONTAGEM DE LINHAS
            // =================================================

            /*
                Sempre que encontrar '\n'
                significa mudança de linha.
             */

            if (c == '\n') {

                linha++;

                continue;
            }

            // =================================================
            // IGNORAR ESPAÇOS EM BRANCO
            // =================================================

            if (Character.isWhitespace(c)) {

                continue;
            }

            // =================================================
            // COMENTÁRIOS
            // =================================================

            /*
                Comentários em Pascal:

                { comentario }

                O lexer deve ignorar tudo
                até encontrar o }
             */

            if (c == '{') {

                i++;

                boolean comentarioFechado = false;

                while (i < codigo.length()) {

                    // VERIFICA SE O COMENTÁRIO FOI FECHADO
                    if (codigo.charAt(i) == '}') {

                        comentarioFechado = true;

                        break;
                    }

                    // CONTAGEM DE LINHAS DENTRO DO COMENTÁRIO
                    if (codigo.charAt(i) == '\n') {

                        linha++;
                    }

                    i++;
                }

                // CASO O COMENTÁRIO NÃO TENHA FECHAMENTO
                if (!comentarioFechado) {

                    tokens.add(
                            new Token(
                                    "Comentário não fechado",
                                    TipoToken.ERRO,
                                    linha
                            )
                    );
                }

                continue;
            }

            // =================================================
            // IDENTIFICADORES E PALAVRAS RESERVADAS
            // =================================================

            /*
                IDENTIFICADORES:

                x
                idade
                valor1

                PALAVRAS RESERVADAS:

                begin
                if
                while
                write
             */

            if (Character.isLetter(c)) {

                String lexema = "";

                // CAPTURAR LETRAS E DÍGITOS
                while (i < codigo.length() &&
                        Character.isLetterOrDigit(codigo.charAt(i))) {

                    lexema += codigo.charAt(i);

                    i++;
                }

                // RETROCEDE UMA POSIÇÃO
                i--;

                // =============================================
                // VERIFICAR SE É PALAVRA RESERVADA
                // =============================================

                if (palavrasReservadas.contains(lexema)) {

                    tokens.add(
                            new Token(
                                    lexema,
                                    TipoToken.PALAVRA_RESERVADA,
                                    linha
                            )
                    );

                }

                // =============================================
                // CASO CONTRÁRIO É IDENTIFICADOR
                // =============================================

                else {

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

            // =================================================
// NÚMEROS
// =================================================

            if (Character.isDigit(c)) {

                String numero = "";

                while (i < codigo.length() &&
                        Character.isDigit(codigo.charAt(i))) {

                    numero += codigo.charAt(i);

                    i++;
                }

                // =================================================
                // VERIFICA SE EXISTEM LETRAS APÓS O NÚMERO
                // EXEMPLO INVÁLIDO:
                //
                // 2da
                // 10abc
                // =================================================

                if (i < codigo.length() &&
                        Character.isLetter(codigo.charAt(i))) {

                    String erro = numero;

                    while (i < codigo.length() &&
                            Character.isLetterOrDigit(codigo.charAt(i))) {

                        erro += codigo.charAt(i);

                        i++;
                    }

                    i--;

                    tokens.add(
                            new Token(
                                    erro,
                                    TipoToken.ERRO,
                                    linha
                            )
                    );

                    continue;
                }

                // RETROCEDE UMA POSIÇÃO
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

            // =================================================
            // STRINGS / CHARACTER CONSTANT
            // =================================================

            /*
                Exemplos:

                'a'
                'ola'
                'texto'
             */

            if (c == '\'') {

                String texto = "";

                i++;

                boolean stringFechada = false;

                while (i < codigo.length()) {

                    // VERIFICA FECHAMENTO DA STRING
                    if (codigo.charAt(i) == '\'') {

                        stringFechada = true;

                        break;
                    }

                    texto += codigo.charAt(i);

                    i++;
                }

                // =============================================
                // STRING VÁLIDA
                // =============================================

                if (stringFechada) {

                    tokens.add(
                            new Token(
                                    texto,
                                    TipoToken.STRING,
                                    linha
                            )
                    );

                }

                // =============================================
                // STRING NÃO FECHADA
                // =============================================

                else {

                    tokens.add(
                            new Token(
                                    "String não fechada",
                                    TipoToken.ERRO,
                                    linha
                            )
                    );
                }

                continue;
            }

            // =================================================
            // OPERADORES COMPOSTOS
            // =================================================

            /*
                :=
                <=
                >=
                <>
                ..
             */

            // =============================================
            // OPERADOR :=
            // =============================================

            if (c == ':' &&
                    i + 1 < codigo.length() &&
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

            // =============================================
            // OPERADOR <=
            // =============================================

            if (c == '<' &&
                    i + 1 < codigo.length() &&
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

            // =============================================
            // OPERADOR >=
            // =============================================

            if (c == '>' &&
                    i + 1 < codigo.length() &&
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

            // =============================================
            // OPERADOR <>
            // =============================================

            if (c == '<' &&
                    i + 1 < codigo.length() &&
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

            // =============================================
            // OPERADOR ..
            // =============================================

            if (c == '.' &&
                    i + 1 < codigo.length() &&
                    codigo.charAt(i + 1) == '.') {

                tokens.add(
                        new Token(
                                "..",
                                TipoToken.OPERADOR,
                                linha
                        )
                );

                i++;

                continue;
            }

            // =================================================
            // OPERADORES SIMPLES
            // =================================================

            /*
                +
                -
                *
                =
                <
                >
             */

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

            // =================================================
            // DELIMITADORES
            // =================================================

            /*
                ;
                ,
                .
                :
                (
                )
                [
                ]
             */

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

            // =================================================
            // ERROS LÉXICOS
            // =================================================

            /*
                Qualquer símbolo que não pertença
                à linguagem mini-Pascal.
             */

            tokens.add(
                    new Token(
                            String.valueOf(c),
                            TipoToken.ERRO,
                            linha
                    )
            );

        }

        // DEVOLVE A LISTA FINAL DE TOKENS
        return tokens;
    }

}