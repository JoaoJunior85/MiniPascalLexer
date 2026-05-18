package gui;

import java.awt.*;

/**
 * Paleta de cores e fontes centralizadas.
 * Suporta tema escuro e tema claro dinamicamente.
 */
public final class Tema {

    private Tema() {}

    // =========================================================
    // CORES DINÂMICAS
    // =========================================================

    public static Color FUNDO_ESCURO;
    public static Color FUNDO_PAINEL;

    public static Color EDITOR;

    public static Color GUTTER;
    public static Color GUTTER_TEXTO;
    public static Color GUTTER_ATIVO;

    public static Color TEXTO_EDITOR;

    public static Color BOTAO_IDLE;
    public static Color BOTAO_HOVER;
    public static Color BOTAO_PRESS;

    public static Color BOTAO_CINZA_IDLE;
    public static Color BOTAO_CINZA_HOVER;
    public static Color BOTAO_CINZA_PRESS;

    public static Color TABELA_PAR;
    public static Color TABELA_IMPAR;

    public static Color CABECALHO;
    public static Color TABELA_TEXTO;

    public static Color SELECAO;

    public static Color ERRO;
    public static Color BORDA;

    public static Color OPCIONAL;
    public static Color SUCESSO;

    // =========================================================
    // CORES DE SINTAXE
    // =========================================================

    public static Color SINTAX_RESERVADA;
    public static Color SINTAX_IDENTIFIER;
    public static Color SINTAX_NUMERO;
    public static Color SINTAX_STRING;
    public static Color SINTAX_OPERADOR;
    public static Color SINTAX_DELIMITADOR;

    // =========================================================
    // FONTES
    // =========================================================

    public static Font FONTE_EDITOR =
            resolverFonte("JetBrains Mono", "Consolas", Font.PLAIN, 15);

    public static Font FONTE_GUTTER =
            resolverFonte("JetBrains Mono", "Consolas", Font.PLAIN, 13);

    public static final Font FONTE_UI =
            new Font("Segoe UI", Font.PLAIN, 13);

    public static final Font FONTE_TITULO =
            new Font("Segoe UI", Font.BOLD, 10);

    public static final Font FONTE_BOTAO =
            new Font("Segoe UI", Font.BOLD, 14);

    // =========================================================
    // BLOCO ESTÁTICO
    // =========================================================

    static {
        aplicarTemaEscuro();
    }

    // =========================================================
    // TEMA ESCURO
    // =========================================================

    public static void aplicarTemaEscuro() {

        FUNDO_ESCURO = new Color(0x2B2D30);
        FUNDO_PAINEL = new Color(0x3C3F41);

        EDITOR = new Color(0x1E1F22);

        GUTTER = new Color(0x313335);
        GUTTER_TEXTO = new Color(0x606366);
        GUTTER_ATIVO = new Color(0xC8C8C8);

        TEXTO_EDITOR = new Color(0xD4D4D4);

        BOTAO_IDLE = new Color(0x4A90D9);
        BOTAO_HOVER = new Color(0x5BA3EC);
        BOTAO_PRESS = new Color(0x3A7ABF);

        BOTAO_CINZA_IDLE = new Color(0x6E7681);
        BOTAO_CINZA_HOVER = new Color(0x8B95A1);
        BOTAO_CINZA_PRESS = new Color(0x555B65);

        TABELA_PAR = new Color(0x3C3F41);
        TABELA_IMPAR = new Color(0x45484B);

        CABECALHO = new Color(0x2B2D30);

        TABELA_TEXTO = new Color(0xC8C8C8);

        SELECAO = new Color(0x4A90D9);

        ERRO = new Color(0xFF6B6B);

        BORDA = new Color(0x555555);

        OPCIONAL = new Color(0xE0E0E0);

        SUCESSO = new Color(0x6BCB77);

        // =====================================
        // SINTAXE
        // =====================================

        SINTAX_RESERVADA = new Color(0xC586C0);

        SINTAX_IDENTIFIER = new Color(0x9CDCFE);

        SINTAX_NUMERO = new Color(0xB5CEA8);

        SINTAX_STRING = new Color(0xCE9178);

        SINTAX_OPERADOR = new Color(0xD16969);

        SINTAX_DELIMITADOR = new Color(0xD4D4D4);
    }

    // =========================================================
    // TEMA CLARO
    // =========================================================

    public static void aplicarTemaClaro() {

        FUNDO_ESCURO = new Color(245, 245, 245);

        FUNDO_PAINEL = new Color(250, 250, 250);

        // branco sujo
        EDITOR = new Color(248, 248, 248);

        GUTTER = new Color(235, 235, 235);

        GUTTER_TEXTO = new Color(120, 120, 120);

        GUTTER_ATIVO = new Color(30, 30, 30);

        TEXTO_EDITOR = new Color(35, 35, 35);

        BOTAO_IDLE = new Color(52, 120, 246);

        BOTAO_HOVER = new Color(72, 140, 255);

        BOTAO_PRESS = new Color(40, 100, 220);

        BOTAO_CINZA_IDLE = new Color(180, 180, 180);

        BOTAO_CINZA_HOVER = new Color(200, 200, 200);

        BOTAO_CINZA_PRESS = new Color(150, 150, 150);

        TABELA_PAR = new Color(252, 252, 252);

        TABELA_IMPAR = new Color(242, 242, 242);

        CABECALHO = new Color(230, 230, 230);

        TABELA_TEXTO = new Color(30, 30, 30);

        SELECAO = new Color(180, 210, 255);

        ERRO = new Color(220, 50, 50);

        BORDA = new Color(210, 210, 210);

        OPCIONAL = new Color(40, 40, 40);

        SUCESSO = new Color(0, 140, 80);

        // =====================================
        // SINTAXE MAIS FORTE
        // =====================================

        // palavras reservadas
        SINTAX_RESERVADA = new Color(0, 70, 160);

        // identificadores
        SINTAX_IDENTIFIER = new Color(25, 25, 25);

        // números
        SINTAX_NUMERO = new Color(9, 134, 88);

        // strings
        SINTAX_STRING = new Color(163, 21, 21);

        // operadores
        SINTAX_OPERADOR = new Color(111, 66, 193);

        // delimitadores
        SINTAX_DELIMITADOR = new Color(0, 0, 0);
    }

    // =========================================================
    // RESOLVER FONTE
    // =========================================================

    public static Font resolverFonte(
            String preferida,
            String fallback,
            int estilo,
            int tamanho
    ) {

        String[] fontes =
                GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames();

        for (String fonte : fontes) {
            if (fonte.equalsIgnoreCase(preferida)) {
                return new Font(preferida, estilo, tamanho);
            }
        }

        for (String fonte : fontes) {
            if (fonte.equalsIgnoreCase(fallback)) {
                return new Font(fallback, estilo, tamanho);
            }
        }

        return new Font(Font.MONOSPACED, estilo, tamanho);
    }
}