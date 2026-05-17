package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/** Botão "pílula" com animação de cor (hover/press) e leve escala ao clicar. */
public class BotaoAnimado extends JButton {

    private final Color corIdle, corHover, corPress;
    private Color corAtual;
    private float escala = 1.0f;
    private boolean mouseOver = false;
    private Timer timerCor;

    public BotaoAnimado(String texto, Color idle, Color hover, Color press) {
        super(texto);
        this.corIdle  = idle;
        this.corHover = hover;
        this.corPress = press;
        this.corAtual = idle;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(Tema.FONTE_BOTAO);
        setForeground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e)  { mouseOver = true;  animarCor(corHover); }
            @Override public void mouseExited(MouseEvent e)   { mouseOver = false; animarCor(corIdle); }
            @Override public void mousePressed(MouseEvent e)  { animarEscala(0.94f); animarCor(corPress); }
            @Override public void mouseReleased(MouseEvent e) { animarEscala(1.0f);  animarCor(mouseOver ? corHover : corIdle); }
        });
    }

    private void animarCor(Color alvo) {
        if (timerCor != null && timerCor.isRunning()) timerCor.stop();
        final Color ini = corAtual;
        final int[] p = {0};
        final int passos = 8;
        timerCor = new Timer(16, e -> {
            p[0]++;
            float t = Math.min(1f, (float) p[0] / passos);
            corAtual = lerp(ini, alvo, t);
            repaint();
            if (p[0] >= passos) timerCor.stop();
        });
        timerCor.start();
    }

    private void animarEscala(float alvo) {
        final float ini = escala;
        final int[] p = {0};
        final int passos = 6;
        Timer t = new Timer(12, null);
        t.addActionListener(e -> {
            p[0]++;
            escala = ini + (alvo - ini) * ((float) p[0] / passos);
            repaint();
            if (p[0] >= passos) t.stop();
        });
        t.start();
    }

    private static Color lerp(Color a, Color b, float t) {
        return new Color(
                clamp((int) (a.getRed()   + (b.getRed()   - a.getRed())   * t)),
                clamp((int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t)),
                clamp((int) (a.getBlue()  + (b.getBlue()  - a.getBlue())  * t)));
    }

    private static Color clarear(Color c, float f) { return lerp(c, Color.WHITE, f); }
    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.translate(w / 2.0, h / 2.0);
        g2.scale(escala, escala);
        g2.translate(-w / 2.0, -h / 2.0);
        int raio = h;
        g2.setColor(new Color(0, 0, 0, 55));
        g2.fill(new RoundRectangle2D.Float(2, 5, w - 4, h - 4, raio, raio));
        g2.setPaint(new GradientPaint(0, 0, clarear(corAtual, 0.18f), 0, h, corAtual));
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h - 2, raio, raio));
        g2.setColor(new Color(255, 255, 255, 35));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 3, raio, raio));
        g2.setFont(getFont());
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(getText(),
                (w - fm.stringWidth(getText())) / 2,
                (h - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
