package gui;

import lexer.AnalisadorLexico;
import model.Token;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.util.List;

/** Aplica coloração sintática sobre um JTextPane a partir dos tokens do AnalisadorLexico. */
public class ColoradorSintatico {

    private final JTextPane area;
    private boolean atualizando = false;
    private Timer timer;

    public ColoradorSintatico(JTextPane area) {
        this.area = area;
    }

    /** Agenda uma re-coloração com debounce de 200ms. */
    public void agendar() {
        if (timer != null && timer.isRunning()) timer.stop();
        timer = new Timer(200, e -> aplicar());
        timer.setRepeats(false);
        timer.start();
    }

    public void aplicar() {
        if (atualizando) return;
        atualizando = true;
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = area.getStyledDocument();
                String texto = area.getText();
                Style normal = area.addStyle("normal", null);
                StyleConstants.setForeground(normal, Tema.TEXTO_EDITOR);
                doc.setCharacterAttributes(0, texto.length(), normal, true);

                List<Token> tokens = new AnalisadorLexico().analisar(texto);
                int posicao = 0;
                for (Token tk : tokens) {
                    int inicio = texto.indexOf(tk.getLexema(), posicao);
                    if (inicio == -1) continue;
                    int tam = tk.getLexema().length();
                    Style est = area.addStyle(tk.getTipo().toString(), null);
                    switch (tk.getTipo()) {
                        case PALAVRA_RESERVADA -> {
                            StyleConstants.setForeground(est, Tema.SINTAX_RESERVADA);
                            StyleConstants.setBold(est, true);
                        }
                        case IDENTIFICADOR -> StyleConstants.setForeground(est, Tema.SINTAX_IDENTIFIER);
                        case NUMERO        -> StyleConstants.setForeground(est, Tema.SINTAX_NUMERO);
                        case STRING        -> StyleConstants.setForeground(est, Tema.SINTAX_STRING);
                        case OPERADOR      -> StyleConstants.setForeground(est, Tema.SINTAX_OPERADOR);
                        case DELIMITADOR   -> StyleConstants.setForeground(est, Tema.SINTAX_DELIMITADOR);
                        case ERRO          -> {
                            StyleConstants.setForeground(est, java.awt.Color.RED);
                            StyleConstants.setUnderline(est, true);
                        }
                    }
                    doc.setCharacterAttributes(inicio, tam, est, true);
                    posicao = inicio + tam;
                }
            } finally {
                atualizando = false;
            }
        });
    }
}
