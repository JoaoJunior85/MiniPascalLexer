package gui;

import javax.swing.*;
import java.awt.*;

/** Painel com fundo em gradiente diagonal. */
public class PainelGradiente extends JPanel {
    private final Color a, b;

    public PainelGradiente(Color a, Color b) {
        this.a = a;
        this.b = b;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setPaint(new GradientPaint(0, 0, a, getWidth(), getHeight(), b));
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}
