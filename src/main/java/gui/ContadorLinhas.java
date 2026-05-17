package gui;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;

/** Gutter de numeração de linhas para um JTextPane. */
public class ContadorLinhas extends JPanel {

    private final JTextPane editor;

    public ContadorLinhas(JTextPane editor) {
        this.editor = editor;
        setFont(Tema.FONTE_GUTTER);
        setBackground(Tema.GUTTER);
        setForeground(Tema.GUTTER_TEXTO);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Tema.BORDA));
    }

    @Override
    public Dimension getPreferredSize() {
        int linhas = editor.getText().split("\n", -1).length;
        int dig = String.valueOf(Math.max(linhas, 99)).length();
        int w = getFontMetrics(getFont()).stringWidth("0".repeat(dig)) + 24;
        return new Dimension(w, editor.getPreferredSize().height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fmG = g2.getFontMetrics(getFont());
        FontMetrics fmE = editor.getFontMetrics(editor.getFont());
        int alt = fmE.getHeight();
        int total = editor.getText().split("\n", -1).length;
        int pad = editor.getMargin().top;
        int cursor = 0;
        try {
            Element root = editor.getDocument().getDefaultRootElement();
            cursor = root.getElementIndex(editor.getCaretPosition());
        } catch (Exception ignored) {}
        for (int i = 0; i < total; i++) {
            int y = pad + i * alt + alt - fmE.getDescent() - fmG.getDescent();
            g2.setColor(i == cursor ? Tema.GUTTER_ATIVO : Tema.GUTTER_TEXTO);
            String num = String.valueOf(i + 1);
            int x = getWidth() - fmG.stringWidth(num) - 8;
            g2.drawString(num, x, y);
        }
    }
}
