package gui;

import java.awt.*;

/**
 * Paleta de cores e fontes centralizadas (tema escuro inspirado no Darcula).
 * Constantes públicas para serem usadas em toda a UI.
 */
public final class Tema {

    private Tema() {}

    // -------- cores --------
    public static final Color FUNDO_ESCURO  = new Color(0x2B2D30);
    public static final Color FUNDO_PAINEL  = new Color(0x3C3F41);
    public static final Color EDITOR        = new Color(0x1E1F22);
    public static final Color GUTTER        = new Color(0x313335);
    public static final Color GUTTER_TEXTO  = new Color(0x606366);
    public static final Color GUTTER_ATIVO  = new Color(0xC8C8C8);
    public static final Color TEXTO_EDITOR  = new Color(0xD4D4D4);
    public static final Color BOTAO_IDLE    = new Color(0x4A90D9);
    public static final Color BOTAO_HOVER   = new Color(0x5BA3EC);
    public static final Color BOTAO_PRESS   = new Color(0x3A7ABF);
    public static final Color BOTAO_CINZA_IDLE  = new Color(0x6E7681);
    public static final Color BOTAO_CINZA_HOVER = new Color(0x8B95A1);
    public static final Color BOTAO_CINZA_PRESS = new Color(0x555B65);
    public static final Color TABELA_PAR    = new Color(0x3C3F41);
    public static final Color TABELA_IMPAR  = new Color(0x45484B);
    public static final Color CABECALHO     = new Color(0x2B2D30);
    public static final Color TABELA_TEXTO  = new Color(0xC8C8C8);
    public static final Color SELECAO       = new Color(0x4A90D9);
    public static final Color ERRO          = new Color(0xFF6B6B);
    public static final Color BORDA         = new Color(0x555555);
    public static final Color OPCIONAL      = new Color(0xE0E0E0);
    public static final Color SUCESSO       = new Color(0x6BCB77);

    // cores de coloração sintática
    public static final Color SINTAX_RESERVADA   = new Color(0xC586C0);
    public static final Color SINTAX_IDENTIFIER  = new Color(0x9CDCFE);
    public static final Color SINTAX_NUMERO      = new Color(0xB5CEA8);
    public static final Color SINTAX_STRING      = new Color(0xCE9178);
    public static final Color SINTAX_OPERADOR    = new Color(0xD16969);
    public static final Color SINTAX_DELIMITADOR = new Color(0xD4D4D4);

    // -------- fontes --------
    public static Font FONTE_EDITOR = resolverFonte("JetBrains Mono", "Consolas", Font.PLAIN, 15);
    public static Font FONTE_GUTTER = resolverFonte("JetBrains Mono", "Consolas", Font.PLAIN, 13);
    public static final Font FONTE_UI     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 10);
    public static final Font FONTE_BOTAO  = new Font("Segoe UI", Font.BOLD, 14);

    public static Font resolverFonte(String pref, String fb, int estilo, int tam) {
        String[] av = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String f : av) if (f.equalsIgnoreCase(pref)) return new Font(pref, estilo, tam);
        for (String f : av) if (f.equalsIgnoreCase(fb))   return new Font(fb, estilo, tam);
        return new Font(Font.MONOSPACED, estilo, tam);
    }
}
