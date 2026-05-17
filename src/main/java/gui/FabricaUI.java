package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/** Fábrica de componentes Swing reutilizáveis (botões, tabelas, separadores, labels). */
public final class FabricaUI {

    private FabricaUI() {}

    public static JMenuItem item(String texto, String atalho, ActionListener a) {
        JMenuItem mi = new JMenuItem(texto);
        if (atalho != null) mi.setAccelerator(KeyStroke.getKeyStroke(atalho));
        mi.addActionListener(a);
        return mi;
    }

    public static JButton botaoToolbar(String texto, ActionListener a) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setFont(Tema.FONTE_UI);
        b.setBackground(Tema.FUNDO_PAINEL);
        b.setForeground(Tema.OPCIONAL);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDA, 1, true),
                new EmptyBorder(5, 12, 5, 12)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(a);
        return b;
    }

    public static Component separadorVertical() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setPreferredSize(new Dimension(1, 22));
        s.setForeground(Tema.BORDA);
        return s;
    }

    public static JLabel labelSecao(String texto) {
        JLabel l = new JLabel(texto.toUpperCase());
        l.setFont(Tema.FONTE_TITULO);
        l.setForeground(new Color(0x888888));
        l.setBorder(new EmptyBorder(0, 2, 0, 0));
        return l;
    }

    public static JTable criarTabela(String[] colunas, boolean ehErros) {
        DefaultTableModel modelo = new DefaultTableModel(new Object[][]{}, colunas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(modelo);
        t.setRowHeight(28);
        t.setFont(Tema.FONTE_UI);
        t.setForeground(Tema.TABELA_TEXTO);
        t.setBackground(Tema.TABELA_PAR);
        t.setSelectionBackground(Tema.SELECAO);
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(0x4A4D50));
        t.setShowVerticalLines(true);
        t.setShowHorizontalLines(true);
        t.setIntercellSpacing(new Dimension(1, 1));
        t.getTableHeader().setFont(Tema.FONTE_TITULO);
        t.getTableHeader().setBackground(Tema.CABECALHO);
        t.getTableHeader().setForeground(new Color(0xAAAAAA));
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDA));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                super.getTableCellRendererComponent(tb, v, sel, foc, r, c);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (sel) {
                    setBackground(Tema.SELECAO);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(r % 2 == 0 ? Tema.TABELA_PAR : Tema.TABELA_IMPAR);
                    setForeground(ehErros ? Tema.ERRO : Tema.TABELA_TEXTO);
                }
                return this;
            }
        });
        return t;
    }

    public static JPanel empacotarTabela(String titulo, JTable tabela) {
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Tema.BORDA));
        scroll.getViewport().setBackground(Tema.TABELA_PAR);
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setOpaque(false);
        painel.add(labelSecao(titulo), BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }
}
