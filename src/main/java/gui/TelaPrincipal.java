

package gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

/**
 * Janela principal da Mini Pascal IDE.
 * Atua como orquestrador: delega responsabilidades em classes especializadas
 * dentro do pacote {@code gui}.
 */
public class TelaPrincipal extends JFrame {

    // componentes principais
    private JTextPane    areaCodigo;
    private JTable       tabelaTokens;
    private JTable       tabelaErros;
    private BotaoAnimado botaoAnalisar;
    private BotaoAnimado botaoLimpar;
    private JLabel       labelEstatisticas;
    private JLabel       labelStatus;
    private JLabel       labelCursor;
    private JProgressBar barraProgresso;
       private JLabel      lblNome;

    // colaboradores
    private ColoradorSintatico colorador;
    private ExecutorAnalise    executor;
    private GerenciadorArquivos arquivos;

    // estado
    private boolean temaEscuro = true;
    private int fontSize = 15;

    // =========================================================
    // BOOTSTRAP
    // =========================================================
    public static void main(String[] args) {
        try { new FlatDarculaLaf().setup(); }
        catch (Exception e) { System.err.println("L&F indisponível."); }
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }

    public TelaPrincipal() {
        setTitle("Mini Pascal IDE ✨");
        setSize(1280, 820);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        inicializarInterface();
        registrarAtalhos();
    }

    // =========================================================
    // CONSTRUÇÃO DA UI
    // =========================================================
    private void inicializarInterface() {
        setJMenuBar(ConstrutorMenu.construir(criarAcoes()));

        PainelGradiente raiz = new PainelGradiente(Tema.FUNDO_ESCURO, Tema.FUNDO_PAINEL);
        raiz.setLayout(new BorderLayout(12, 12));
        raiz.setBorder(new EmptyBorder(10, 14, 10, 14));

        JPanel topo = new JPanel(new BorderLayout(0, 8));
        topo.setOpaque(false);
        topo.add(criarBarraTitulo(), BorderLayout.NORTH);
        topo.add(criarToolbar(), BorderLayout.SOUTH);

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(criarSplitPrincipal(), BorderLayout.CENTER);
        raiz.add(criarRodape(), BorderLayout.SOUTH);

        setContentPane(raiz);

        // colaboradores que dependem de componentes prontos
        colorador = new ColoradorSintatico(areaCodigo);
        executor  = new ExecutorAnalise(tabelaTokens, tabelaErros, barraProgresso, this::setStatus);
        arquivos  = new GerenciadorArquivos(this, new GerenciadorArquivos.Ouvinte() {
            @Override public void aoCarregarTexto(String texto, java.io.File a) {
                areaCodigo.setText(texto);
                setTitle("Mini Pascal IDE — " + a.getName());
                setStatus("Arquivo aberto: " + a.getName(), Tema.SUCESSO);
            }
            @Override public void aoSalvar(java.io.File a) {
                setTitle("Mini Pascal IDE — " + a.getName());
                setStatus("💾 Salvo: " + a.getName(), Tema.SUCESSO);
            }
            @Override public void aoNovoArquivo(String textoPadrao) {
                areaCodigo.setText(textoPadrao);
                setTitle("Mini Pascal IDE ✨");
                executor.limparResultados();
                setStatus("Novo arquivo criado", Tema.OPCIONAL);
            }
        });

        botaoAnalisar.addActionListener(e -> executor.analisar(areaCodigo.getText()));
        botaoLimpar.addActionListener(e -> limparTudo());

        colorador.aplicar();
    }

    private JPanel criarBarraTitulo() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDA),
                new EmptyBorder(0, 0, 8, 0)));
        lblNome = new JLabel("Mini Pascal IDE");
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNome.setForeground(Tema.OPCIONAL);

    ;

        JPanel painelTexto = new JPanel(new GridLayout(2, 1, 0, 1));
        painelTexto.setOpaque(false);
        painelTexto.add(lblNome);

        labelEstatisticas = new JLabel("0 linhas  |  0 chars");
        labelEstatisticas.setFont(Tema.FONTE_UI);
        labelEstatisticas.setForeground(Tema.OPCIONAL);
        labelEstatisticas.setHorizontalAlignment(SwingConstants.RIGHT);

        barra.add(painelTexto, BorderLayout.WEST);
        barra.add(labelEstatisticas, BorderLayout.EAST);
        return barra;
    }

    private JPanel criarToolbar() {
        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tb.setOpaque(false);
        tb.add(FabricaUI.botaoToolbar("📄 Novo",   e -> arquivos.novo()));
        tb.add(FabricaUI.botaoToolbar("📂 Abrir",  e -> arquivos.abrir()));
        tb.add(FabricaUI.botaoToolbar("💾 Salvar", e -> arquivos.salvar(areaCodigo.getText(), false)));
        tb.add(FabricaUI.separadorVertical());
        tb.add(FabricaUI.botaoToolbar("🧹 Limpar", e -> limparTudo()));
        tb.add(FabricaUI.botaoToolbar("📋 Copiar", e -> areaCodigo.copy()));
        tb.add(FabricaUI.separadorVertical());
        tb.add(FabricaUI.botaoToolbar("🔍+", e -> ajustarFonte(+1)));
        tb.add(FabricaUI.botaoToolbar("🔍-", e -> ajustarFonte(-1)));
        tb.add(FabricaUI.separadorVertical());
        tb.add(FabricaUI.botaoToolbar("🎨 Tema", e -> trocarTema(!temaEscuro)));
        return tb;
    }

    private JSplitPane criarSplitPrincipal() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                criarSecaoEditor(), criarSecaoResultados());
        split.setDividerLocation(570);
        split.setResizeWeight(0.44);
        split.setOpaque(false);
        split.setBorder(null);
        return split;
    }

    private JPanel criarSecaoEditor() {
        areaCodigo = new JTextPane();
        areaCodigo.setFont(Tema.FONTE_EDITOR);
        areaCodigo.setBackground(Tema.EDITOR);
        areaCodigo.setForeground(Tema.TEXTO_EDITOR);
        areaCodigo.setCaretColor(new Color(0xAEAFAD));
        areaCodigo.setSelectionColor(new Color(0x214283));
        areaCodigo.setSelectedTextColor(Tema.TEXTO_EDITOR);
        areaCodigo.setMargin(new Insets(8, 10, 8, 10));
        areaCodigo.setText(Exemplos.PADRAO);

        ContadorLinhas gutter = new ContadorLinhas(areaCodigo);

        JScrollPane scroll = new JScrollPane(areaCodigo);
        scroll.setRowHeaderView(gutter);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Tema.BORDA));
        scroll.getViewport().setBackground(Tema.EDITOR);

        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setOpaque(false);
        painel.add(FabricaUI.labelSecao("Código Fonte"), BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);

        areaCodigo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { colorador.agendar(); atualizarEstatisticas(); }
            public void removeUpdate(DocumentEvent e)  { colorador.agendar(); atualizarEstatisticas(); }
            public void changedUpdate(DocumentEvent e) { atualizarEstatisticas(); }
        });
        areaCodigo.addCaretListener(e -> { gutter.repaint(); atualizarPosCursor(); });

        atualizarEstatisticas();
        return painel;
    }

    private JPanel criarSecaoResultados() {
        JPanel painel = new JPanel(new GridLayout(2, 1, 0, 10));
        painel.setOpaque(false);
        tabelaTokens = FabricaUI.criarTabela(new String[]{"Lexema", "Tipo", "Linha"}, false);
        tabelaErros  = FabricaUI.criarTabela(new String[]{"Mensagem de Erro", "Linha"}, true);
        painel.add(FabricaUI.empacotarTabela("Tokens Reconhecidos", tabelaTokens));
        painel.add(FabricaUI.empacotarTabela("Erros Léxicos",       tabelaErros));
        return painel;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout(0, 6));
        rodape.setOpaque(false);

        botaoAnalisar = new BotaoAnimado("⚙  Analisar Código",
                Tema.BOTAO_IDLE, Tema.BOTAO_HOVER, Tema.BOTAO_PRESS);
        botaoAnalisar.setPreferredSize(new Dimension(220, 44));
        botaoLimpar = new BotaoAnimado("🧹  Limpar",
                Tema.BOTAO_CINZA_IDLE, Tema.BOTAO_CINZA_HOVER, Tema.BOTAO_CINZA_PRESS);
        botaoLimpar.setPreferredSize(new Dimension(140, 44));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        painelBotoes.setOpaque(false);
        painelBotoes.add(botaoAnalisar);
        painelBotoes.add(botaoLimpar);

        JPanel status = new JPanel(new BorderLayout());
        status.setOpaque(false);
        status.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDA),
                new EmptyBorder(6, 4, 0, 4)));

        labelStatus = new JLabel("Pronto");
        labelStatus.setFont(Tema.FONTE_UI);
        labelStatus.setForeground(Tema.GUTTER_TEXTO);

        labelCursor = new JLabel("Ln 1, Col 1");
        labelCursor.setFont(Tema.FONTE_UI);
        labelCursor.setForeground(Tema.GUTTER_TEXTO);

        barraProgresso = new JProgressBar();
        barraProgresso.setPreferredSize(new Dimension(160, 6));
        barraProgresso.setVisible(false);
        barraProgresso.setBorderPainted(false);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        direita.setOpaque(false);
        direita.add(barraProgresso);
        direita.add(labelCursor);

        status.add(labelStatus, BorderLayout.WEST);
        status.add(direita, BorderLayout.EAST);

        rodape.add(painelBotoes, BorderLayout.CENTER);
        rodape.add(status, BorderLayout.SOUTH);
        return rodape;
    }

    // =========================================================
    // AÇÕES DO MENU
    // =========================================================
    private ConstrutorMenu.Acoes criarAcoes() {
        ConstrutorMenu.Acoes a = new ConstrutorMenu.Acoes();
        a.novo           = e -> arquivos.novo();
        a.abrir          = e -> arquivos.abrir();
        a.salvar         = e -> arquivos.salvar(areaCodigo.getText(), false);
        a.salvarComo     = e -> arquivos.salvar(areaCodigo.getText(), true);
        a.sair           = e -> System.exit(0);
        a.recortar       = e -> areaCodigo.cut();
        a.copiar         = e -> areaCodigo.copy();
        a.colar          = e -> areaCodigo.paste();
        a.limparTudo     = e -> limparTudo();
        a.selecionarTudo = e -> areaCodigo.selectAll();
        a.copiarTokens   = e -> copiarTokensClipboard();
        a.analisar       = e -> executor.analisar(areaCodigo.getText());
        a.limparResultados = e -> executor.limparResultados();
        a.temaEscuro     = e -> trocarTema(true);
        a.temaClaro      = e -> trocarTema(false);
        a.aumentarFonte  = e -> ajustarFonte(+1);
        a.diminuirFonte  = e -> ajustarFonte(-1);
        a.resetarFonte   = e -> setFonteTamanho(15);
        a.exemplo1       = e -> carregarExemplo(1);
        a.exemplo2       = e -> carregarExemplo(2);
        a.exemplo3       = e -> carregarExemplo(3);
        a.atalhos        = e -> mostrarAtalhos();
        a.sobre          = e -> mostrarSobre();
        return a;
    }

    private void limparTudo() {
        int r = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja limpar o editor e os resultados?",
                "Confirmar Limpeza", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            areaCodigo.setText("");
            executor.limparResultados();
            setStatus("Tudo limpo ✨", Tema.SUCESSO);
        }
    }

    private void copiarTokensClipboard() {
        DefaultTableModel m = (DefaultTableModel) tabelaTokens.getModel();
        StringBuilder sb = new StringBuilder("Lexema\tTipo\tLinha\n");
        for (int i = 0; i < m.getRowCount(); i++)
            sb.append(m.getValueAt(i, 0)).append("\t")
                    .append(m.getValueAt(i, 1)).append("\t")
                    .append(m.getValueAt(i, 2)).append("\n");
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(sb.toString()), null);
        setStatus("📋 Tokens copiados para a área de transferência", Tema.SUCESSO);
    }

    private void trocarTema(boolean escuro) {

        try {

            // =====================================
            // APLICA CORES DO TEMA
            // =====================================

            if (escuro) {

                Tema.aplicarTemaEscuro();

                new FlatArcDarkOrangeIJTheme().setup();

            } else {

                Tema.aplicarTemaClaro();

                new FlatArcOrangeIJTheme().setup();
            }

            temaEscuro = escuro;

            // =====================================
            // APLICA CORES NO EDITOR
            // =====================================

            areaCodigo.setBackground(Tema.EDITOR);

            areaCodigo.setForeground(Tema.TEXTO_EDITOR);

            areaCodigo.setCaretColor(Tema.TEXTO_EDITOR);

            areaCodigo.setSelectionColor(Tema.SELECAO);

            areaCodigo.setSelectedTextColor(Tema.TEXTO_EDITOR);

            // =====================================
            // REAPLICA SINTAXE
            // =====================================

            colorador.aplicar();

            // =====================================
            // ATUALIZA INTERFACE
            // =====================================

            SwingUtilities.updateComponentTreeUI(this);

            repaint();

            setStatus(
                    "Tema alterado: " + (escuro ? "Escuro" : "Claro"),
                    Tema.OPCIONAL
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void aplicarTemaEditor(boolean escuro) {

        if (escuro) {

            areaCodigo.setBackground(new Color(43, 43, 43));
            areaCodigo.setForeground(Color.WHITE);

            areaCodigo.setCaretColor(Color.WHITE);

            areaCodigo.setSelectionColor(new Color(60, 110, 180));
            areaCodigo.setSelectedTextColor(Color.WHITE);

        } else {

            areaCodigo.setBackground(Color.WHITE);
            areaCodigo.setForeground(Color.BLACK);

            areaCodigo.setCaretColor(Color.BLACK);

            areaCodigo.setSelectionColor(new Color(180, 210, 255));
            areaCodigo.setSelectedTextColor(Color.BLACK);
            lblNome.setForeground(Color.BLACK);

        }

        colorador.aplicar();
    }
    private void ajustarFonte(int delta) { setFonteTamanho(fontSize + delta); }

    private void setFonteTamanho(int tamanho) {
        fontSize = Math.max(9, Math.min(36, tamanho));
        areaCodigo.setFont(Tema.FONTE_EDITOR.deriveFont((float) fontSize));
        setStatus("Fonte: " + fontSize + "pt", Tema.OPCIONAL);
    }

    private void carregarExemplo(int n) {
        areaCodigo.setText(Exemplos.porId(n));
        setStatus("Exemplo carregado", Tema.OPCIONAL);
    }

    private void mostrarAtalhos() {
        String s = """
                ⌨  Atalhos de Teclado:

                Ctrl+N        Novo arquivo
                Ctrl+O        Abrir arquivo
                Ctrl+S        Salvar
                Ctrl+Shift+S  Salvar como
                Ctrl+Q        Sair

                Ctrl+X/C/V    Recortar/Copiar/Colar
                Ctrl+A        Selecionar tudo
                Ctrl+Shift+L  Limpar tudo

                F5            Analisar código
                F6            Limpar resultados
                F1            Esta ajuda

                Ctrl++ / -    Zoom da fonte
                Ctrl+0        Resetar fonte
                """;
        JOptionPane.showMessageDialog(this, s, "Atalhos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarSobre() {
        JOptionPane.showMessageDialog(this,
                "Mini Pascal IDE ✨\n\nAnalisador léxico didático para Pascal.\nFeito com 💙 em Java + Swing + FlatLaf.",
                "Sobre", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================
    // ATALHOS GLOBAIS + STATUS
    // =========================================================
    private void registrarAtalhos() {
        JRootPane rp = getRootPane();
        InputMap im = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rp.getActionMap();
        im.put(KeyStroke.getKeyStroke("F5"), "analisar");
        am.put("analisar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { executor.analisar(areaCodigo.getText()); }
        });
        im.put(KeyStroke.getKeyStroke("F6"), "limparRes");
        am.put("limparRes", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { executor.limparResultados(); }
        });
    }

    private void setStatus(String texto, Color cor) {
        labelStatus.setText(texto);
        labelStatus.setForeground(cor);
    }

    private void atualizarPosCursor() {
        try {
            int pos = areaCodigo.getCaretPosition();
            Element root = areaCodigo.getDocument().getDefaultRootElement();
            int linha = root.getElementIndex(pos);
            int col = pos - root.getElement(linha).getStartOffset();
            labelCursor.setText("Ln " + (linha + 1) + ", Col " + (col + 1));
        } catch (Exception ignored) {}
    }

    private void atualizarEstatisticas() {
        SwingUtilities.invokeLater(() -> {
            String t = areaCodigo.getText();
            int linhas = t.split("\n", -1).length;
            int chars = t.length();
            int palavras = t.trim().isEmpty() ? 0 : t.trim().split("\\s+").length;
            labelEstatisticas.setText(String.format("%d %s  |  %d palavras  |  %d chars",
                    linhas, linhas == 1 ? "linha" : "linhas", palavras, chars));
        });
    }
}
