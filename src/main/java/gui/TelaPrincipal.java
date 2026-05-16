package gui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import lexer.AnalisadorLexico;
import model.TipoToken;
import model.Token;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════╗
 *  TelaPrincipal — Mini Pascal IDE
 *  Design: cinza escuro elegante, FlatMacLight como base.
 *
 *  Estrutura de componentes internos (inner classes):
 *   ├── PainelGradiente  → fundo diagonal escuro → cinza médio
 *   ├── ContadorLinhas   → gutter com numeração de linhas
 *   └── BotaoAnimado     → pill button com hover/clique animados
 * ╚══════════════════════════════════════════════════════╝
 */
public class TelaPrincipal extends JFrame {

    // =========================================================
    // PALETA DE CORES
    // Altere apenas aqui para mudar o tema inteiro.
    // =========================================================

    private static final Color COR_FUNDO_ESCURO  = new Color(0x2B2D30); // fundo geral
    private static final Color COR_FUNDO_PAINEL  = new Color(0x3C3F41); // painéis internos
    private static final Color COR_EDITOR        = new Color(0x1E1F22); // área de código
    private static final Color COR_GUTTER        = new Color(0x313335); // fundo do gutter
    private static final Color COR_GUTTER_TEXTO  = new Color(0x606366); // número inativo
    private static final Color COR_GUTTER_ATIVO  = new Color(0xC8C8C8); // número da linha atual
    private static final Color COR_TEXTO_EDITOR  = new Color(0xD4D4D4); // texto do código
    private static final Color COR_BOTAO_IDLE    = new Color(0x4A90D9); // botão em repouso
    private static final Color COR_BOTAO_HOVER   = new Color(0x5BA3EC); // botão com mouse por cima
    private static final Color COR_BOTAO_PRESS   = new Color(0x3A7ABF); // botão ao clicar
    private static final Color COR_TABELA_PAR    = new Color(0x3C3F41); // linhas pares
    private static final Color COR_TABELA_IMPAR  = new Color(0x45484B); // linhas ímpares
    private static final Color COR_CABECALHO     = new Color(0x2B2D30); // cabeçalho das tabelas
    private static final Color COR_TABELA_TEXTO  = new Color(0xC8C8C8); // texto das células
    private static final Color COR_SELECAO       = new Color(0x4A90D9); // seleção nas tabelas
    private static final Color COR_ERRO          = new Color(0xFF6B6B); // erros léxicos
    private static final Color COR_BORDA         = new Color(0x555555); // separadores

    // =========================================================
    // FONTES
    // JetBrains Mono é preferida; Consolas é o fallback.
    // resolverFonte() verifica automaticamente o que está instalado.
    // =========================================================

    private static final Font FONTE_EDITOR = resolverFonte("JetBrains Mono", "Consolas", Font.PLAIN, 15);
    private static final Font FONTE_GUTTER = resolverFonte("JetBrains Mono", "Consolas", Font.PLAIN, 13);
    private static final Font FONTE_UI     = new Font("Segoe UI", Font.PLAIN,  13);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD,   10);
    private static final Font FONTE_BOTAO  = new Font("Segoe UI", Font.BOLD,   14);

    // =========================================================
    // REFERÊNCIAS AOS COMPONENTES
    // =========================================================

    private JTextPane    areaCodigo;
    private JTable       tabelaTokens;
    private JTable       tabelaErros;
    private BotaoAnimado botaoAnalisar;
    private JLabel       labelEstatisticas;
    private boolean atualizando = false;
    private Timer timerColoracao;

    // =========================================================
    // PONTO DE ENTRADA (para teste isolado da UI)
    // =========================================================

    public static void main(String[] args) {
        try {
            FlatMacLightLaf.setup();
        } catch (Exception e) {
            System.err.println("FlatMacLightLaf não disponível. Usando L&F padrão.");
        }
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }

    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public TelaPrincipal() {
        setTitle("Mini Pascal IDE");
        setSize(1280, 800);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        inicializarInterface();
    }

    // =========================================================
    // MONTAGEM DA INTERFACE
    // =========================================================

    private void inicializarInterface() {

        PainelGradiente raiz =
                new PainelGradiente(
                        COR_FUNDO_ESCURO,
                        COR_FUNDO_PAINEL
                );

        raiz.setLayout(new BorderLayout(12, 12));

        raiz.setBorder(
                new EmptyBorder(14, 14, 14, 14)
        );

        raiz.add(criarBarraTitulo(), BorderLayout.NORTH);

        raiz.add(criarSplitPrincipal(), BorderLayout.CENTER);

        raiz.add(criarRodape(), BorderLayout.SOUTH);

        setContentPane(raiz);

        botaoAnalisar.addActionListener(e -> analisarCodigo());

        aplicarColoracao();
    }

    // =========================================================
    // BARRA DE TÍTULO
    // =========================================================

    private JPanel criarBarraTitulo() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA),
                new EmptyBorder(0, 0, 10, 0)
        ));

        // Lado esquerdo: título e subtítulo
        JLabel lblNome = new JLabel("Mini Pascal IDE");
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNome.setForeground(new Color(0xE0E0E0));

        JLabel lblSub = new JLabel("Analisador Léxico");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(COR_GUTTER_TEXTO);

        JPanel painelTexto = new JPanel(new GridLayout(2, 1, 0, 1));
        painelTexto.setOpaque(false);
        painelTexto.add(lblNome);
        painelTexto.add(lblSub);

        // Lado direito: contador de linhas e caracteres
        labelEstatisticas = new JLabel("0 linhas  |  0 chars");
        labelEstatisticas.setFont(FONTE_UI);
        labelEstatisticas.setForeground(COR_GUTTER_TEXTO);
        labelEstatisticas.setHorizontalAlignment(SwingConstants.RIGHT);

        barra.add(painelTexto,       BorderLayout.WEST);
        barra.add(labelEstatisticas, BorderLayout.EAST);

        return barra;
    }

    // =========================================================
    // SPLIT PRINCIPAL (editor | tabelas)
    // =========================================================

    private JSplitPane criarSplitPrincipal() {
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                criarSecaoEditor(),     // painel esquerdo
                criarSecaoResultados()  // painel direito
        );
        split.setDividerLocation(570);
        split.setResizeWeight(0.44);
        split.setOpaque(false);
        split.setBorder(null);

        // Divider minimalista com fundo escuro
        split.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI() {
            @Override
            public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() {
                return new javax.swing.plaf.basic.BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(COR_FUNDO_ESCURO);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        });

        return split;
    }

    // =========================================================
    // SEÇÃO DO EDITOR (esquerda)
    // =========================================================

    private JPanel criarSecaoEditor() {

        // ── Área de código ───────────────────────────────────
        areaCodigo = new JTextPane();

        areaCodigo.setFont(FONTE_EDITOR);

        areaCodigo.setBackground(COR_EDITOR);

        areaCodigo.setForeground(COR_TEXTO_EDITOR);

        areaCodigo.setCaretColor(new Color(0xAEAFAD));

        areaCodigo.setSelectionColor(new Color(0x214283));

        areaCodigo.setSelectedTextColor(COR_TEXTO_EDITOR);

        areaCodigo.setMargin(new Insets(8, 10, 8, 10));

        areaCodigo.setText(codigoExemplo());

        // ── Gutter (contador de linhas) ──────────────────────
        ContadorLinhas gutter = new ContadorLinhas(areaCodigo);

        // ── ScrollPane com o editor e o gutter ──────────────
        JScrollPane scroll = new JScrollPane(areaCodigo);
        scroll.setRowHeaderView(gutter);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, COR_BORDA));
        scroll.getViewport().setBackground(COR_EDITOR);

        // ── Container: label de seção + scroll ───────────────
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setOpaque(false);
        painel.add(criarLabelSecao("Código Fonte"), BorderLayout.NORTH);
        painel.add(scroll,                          BorderLayout.CENTER);

        // ── Listeners para atualizar gutter e estatísticas ───
        DocumentListener onDocChange = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                agendarColoracao();
            }
            public void removeUpdate(DocumentEvent e) {
                agendarColoracao();
            }
            public void changedUpdate(DocumentEvent e) {
                agendarColoracao();
            }
        };
        areaCodigo.getDocument().addDocumentListener(onDocChange);

        // Repinta o gutter ao mover o cursor (linha ativa muda)
        areaCodigo.addCaretListener(e -> gutter.repaint());

        atualizarEstatisticas(); // Inicializa com os valores do codigoExemplo()

        return painel;
    }
    private void agendarColoracao() {

        if (timerColoracao != null && timerColoracao.isRunning()) {
            timerColoracao.stop();
        }

        timerColoracao = new Timer(200, e -> aplicarColoracao());
        timerColoracao.setRepeats(false);
        timerColoracao.start();
    }

    // =========================================================
    // SEÇÃO DE RESULTADOS (direita)
    // =========================================================

    private JPanel criarSecaoResultados() {
        JPanel painel = new JPanel(new GridLayout(2, 1, 0, 10));
        painel.setOpaque(false);

        tabelaTokens = criarTabela(new String[]{"Lexema", "Tipo", "Linha"}, false);
        tabelaErros  = criarTabela(new String[]{"Mensagem de Erro", "Linha"}, true);

        painel.add(empacotar("Tokens Reconhecidos", tabelaTokens));
        painel.add(empacotar("Erros Léxicos",       tabelaErros));

        return painel;
    }

    /**
     * Envolve uma JTable num painel com label de seção e borda.
     */
    private JPanel empacotar(String titulo, JTable tabela) {
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, COR_BORDA));
        scroll.getViewport().setBackground(COR_TABELA_PAR);

        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setOpaque(false);
        painel.add(criarLabelSecao(titulo), BorderLayout.NORTH);
        painel.add(scroll,                  BorderLayout.CENTER);

        return painel;
    }

    // =========================================================
    // RODAPÉ COM BOTÃO
    // =========================================================

    private JPanel criarRodape() {
        botaoAnalisar = new BotaoAnimado(
                "⚙  Analisar Código",
                COR_BOTAO_IDLE, COR_BOTAO_HOVER, COR_BOTAO_PRESS
        );
        botaoAnalisar.setPreferredSize(new Dimension(220, 46));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        rodape.setOpaque(false);
        rodape.add(botaoAnalisar);

        return rodape;
    }

    // =========================================================
    // FÁBRICA DE TABELA ESTILIZADA
    // =========================================================

    /**
     * Cria uma JTable com estilo dark: linhas alternadas, cabeçalho escuro,
     * sem edição pelo utilizador.
     *
     * @param colunas  nomes das colunas
     * @param ehErros  se true, aplica COR_ERRO ao texto das células
     */
    private JTable criarTabela(String[] colunas, boolean ehErros) {

        DefaultTableModel modelo = new DefaultTableModel(new Object[][]{}, colunas) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // somente leitura
            }
        };

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(28);
        tabela.setFont(FONTE_UI);
        tabela.setForeground(COR_TABELA_TEXTO);
        tabela.setBackground(COR_TABELA_PAR);
        tabela.setSelectionBackground(COR_SELECAO);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setGridColor(new Color(0x4A4D50));
        tabela.setShowVerticalLines(true);
        tabela.setShowHorizontalLines(true);
        tabela.setIntercellSpacing(new Dimension(1, 1));

        // Estilo do cabeçalho
        tabela.getTableHeader().setFont(FONTE_TITULO);
        tabela.getTableHeader().setBackground(COR_CABECALHO);
        tabela.getTableHeader().setForeground(new Color(0xAAAAAA));
        tabela.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA)
        );

        // Renderer com alternância de cor e padding interno
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object valor, boolean selecionado,
                    boolean foco, int linha, int coluna) {

                super.getTableCellRendererComponent(t, valor, selecionado, foco, linha, coluna);
                setBorder(new EmptyBorder(0, 10, 0, 10));

                if (selecionado) {
                    setBackground(COR_SELECAO);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(linha % 2 == 0 ? COR_TABELA_PAR : COR_TABELA_IMPAR);
                    setForeground(ehErros ? COR_ERRO : COR_TABELA_TEXTO);
                }

                return this;
            }
        });

        return tabela;
    }

    // =========================================================
    // ANÁLISE LÉXICA
    // =========================================================

    /**
     * Lê o texto do editor, executa o AnalisadorLexico e preenche as tabelas
     * com uma animação de inserção linha-a-linha (Timer de 18 ms por token).
     */
    private void analisarCodigo() {
        String      codigo = areaCodigo.getText();
        List<Token> tokens = new AnalisadorLexico().analisar(codigo);

        DefaultTableModel modeloTokens = (DefaultTableModel) tabelaTokens.getModel();
        DefaultTableModel modeloErros  = (DefaultTableModel) tabelaErros.getModel();

        // Limpa as tabelas antes de preencher
        modeloTokens.setRowCount(0);
        modeloErros.setRowCount(0);

        // Timer que insere um token por vez — cria efeito visual de "feed"
        final int[] i = {0};
        Timer animacao = new Timer(18, null);

        animacao.addActionListener(e -> {
            if (i[0] >= tokens.size()) {
                animacao.stop();
                return;
            }

            Token token = tokens.get(i[0]++);

            if (token.getTipo() != TipoToken.ERRO) {
                modeloTokens.addRow(new Object[]{
                        token.getLexema(),
                        token.getTipo(),
                        token.getLinha()
                });
            } else {
                modeloErros.addRow(new Object[]{
                        token.getLexema(),
                        token.getLinha()
                });
            }
        });

        animacao.start();
    }
    // =========================================================
// COLORAÇÃO SINTÁTICA
// =========================================================

    private void aplicarColoracao() {

        if (atualizando) return;

        atualizando = true;

        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument documento = areaCodigo.getStyledDocument();
                String texto = areaCodigo.getText();

                Style estiloNormal = areaCodigo.addStyle("normal", null);
                StyleConstants.setForeground(estiloNormal, COR_TEXTO_EDITOR);

                documento.setCharacterAttributes(0, texto.length(), estiloNormal, true);

                List<Token> tokens = new AnalisadorLexico().analisar(texto);

                int posicao = 0;

                for (Token token : tokens) {

                    int inicio = texto.indexOf(token.getLexema(), posicao);
                    if (inicio == -1) continue;

                    int tamanho = token.getLexema().length();

                    Style estilo = areaCodigo.addStyle(token.getTipo().toString(), null);

                    switch (token.getTipo()) {
                        case PALAVRA_RESERVADA -> {
                            StyleConstants.setForeground(estilo, new Color(0xC586C0));
                            StyleConstants.setBold(estilo, true);
                        }
                        case IDENTIFICADOR -> StyleConstants.setForeground(estilo, new Color(0x9CDCFE));
                        case NUMERO -> StyleConstants.setForeground(estilo, new Color(0xB5CEA8));
                        case STRING -> StyleConstants.setForeground(estilo, new Color(0xCE9178));
                        case OPERADOR -> StyleConstants.setForeground(estilo, new Color(0xD16969));
                        case DELIMITADOR -> StyleConstants.setForeground(estilo, new Color(0xD4D4D4));
                        case ERRO -> {
                            StyleConstants.setForeground(estilo, Color.RED);
                            StyleConstants.setUnderline(estilo, true);
                        }
                    }

                    documento.setCharacterAttributes(inicio, tamanho, estilo, true);

                    posicao = inicio + tamanho;
                }

            } finally {
                atualizando = false;
            }
        });
    }
    // =========================================================
    // ESTATÍSTICAS (barra de título)
    // =========================================================

    /** Atualiza o rótulo de linhas/chars no canto superior direito. */
    private void atualizarEstatisticas() {

        SwingUtilities.invokeLater(() -> {

            String texto = areaCodigo.getText();

            // =========================================
            // CONTAR LINHAS MANUALMENTE
            // =========================================

            int linhas =
                    texto.split("\n", -1).length;

            int caracteres = texto.length();

            labelEstatisticas.setText(

                    String.format(
                            "%d %s  |  %d chars",

                            linhas,

                            linhas == 1
                                    ? "linha"
                                    : "linhas",

                            caracteres
                    )
            );
        });
    }

    // =========================================================
    // UTILITÁRIOS ESTÁTICOS
    // =========================================================

    /** Label de seção: texto em maiúsculas, cinza discreto. */
    private JLabel criarLabelSecao(String texto) {
        JLabel label = new JLabel(texto.toUpperCase());
        label.setFont(FONTE_TITULO);
        label.setForeground(new Color(0x888888));
        label.setBorder(new EmptyBorder(0, 2, 0, 0));
        return label;
    }

    /**
     * Retorna a melhor fonte disponível no sistema.
     * Testa preferida → fallback → Font.MONOSPACED.
     */
    private static Font resolverFonte(String preferida, String fallback, int estilo, int tamanho) {
        String[] disponiveis = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        for (String f : disponiveis) {
            if (f.equalsIgnoreCase(preferida)) return new Font(preferida, estilo, tamanho);
        }
        for (String f : disponiveis) {
            if (f.equalsIgnoreCase(fallback)) return new Font(fallback, estilo, tamanho);
        }
        return new Font(Font.MONOSPACED, estilo, tamanho);
    }

    /** Código Pascal exibido ao abrir o IDE. */
    private String codigoExemplo() {
        return """
                program exemplo;
 
                var
                    x    : integer;
                    nome : string;
 
                begin
 
                    x    := 10;
                    nome := 'Olá, Pascal';
 
                    write(nome);
                    writeln(x);
 
                end.
                """;
    }

    // =========================================================
    //
    //  INNER CLASS: ContadorLinhas (Gutter)
    //
    // =========================================================

    /**
     * Painel que exibe os números de linha ao lado do editor.
     * Funciona como rowHeaderView de um JScrollPane.
     *
     * ┌── Como usar ─────────────────────────────────────────┐
     *   ContadorLinhas gutter = new ContadorLinhas(areaCodigo);
     *   scrollPane.setRowHeaderView(gutter);
     * └──────────────────────────────────────────────────────┘
     *
     * Comportamento:
     *  • Largura ajustada ao número de dígitos da última linha
     *  • Linha onde o cursor está é destacada (COR_GUTTER_ATIVO)
     *  • Atualiza automaticamente via DocumentListener e CaretListener
     */
    private static class ContadorLinhas extends JPanel {

        private final JTextPane editor;

        public ContadorLinhas(JTextPane editor) {
            this.editor = editor;
            setFont(FONTE_GUTTER);
            setBackground(COR_GUTTER);
            setForeground(COR_GUTTER_TEXTO);
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COR_BORDA));
        }

        @Override
        public Dimension getPreferredSize() {
            // Largura proporcional ao número de dígitos da última linha
            int linhas =
                    editor.getText()
                            .split("\n", -1).length;
            int digitos = String.valueOf(Math.max(linhas, 99)).length();
            int largura = getFontMetrics(getFont()).stringWidth("0".repeat(digitos)) + 24;
            return new Dimension(largura, editor.getPreferredSize().height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            FontMetrics fmGutter      = g2.getFontMetrics(getFont());
            FontMetrics fmEditor      = editor.getFontMetrics(editor.getFont());
            int         alturaLinha   = fmEditor.getHeight();
            int totalLinhas =
                    editor.getText()
                            .split("\n", -1).length;
            int         paddingEditor = editor.getMargin().top;

            // Linha atual do cursor
            int linhaCursor = 0;
            try {
                Element root = editor.getDocument().getDefaultRootElement();
                linhaCursor = root.getElementIndex(editor.getCaretPosition());
            } catch (Exception ignorado) {}

            for (int i = 0; i < totalLinhas; i++) {
                // Calcula a posição vertical alinhada com o editor
                int y = paddingEditor
                        + i * alturaLinha
                        + alturaLinha
                        - fmEditor.getDescent()
                        - fmGutter.getDescent();

                boolean ativa = (i == linhaCursor);
                g2.setColor(ativa ? COR_GUTTER_ATIVO : COR_GUTTER_TEXTO);

                String numero = String.valueOf(i + 1);
                int    x      = getWidth() - fmGutter.stringWidth(numero) - 8;
                g2.drawString(numero, x, y);
            }
        }
    }

    // =========================================================
    //
    //  INNER CLASS: BotaoAnimado
    //
    // =========================================================

    /**
     * Botão totalmente personalizado com:
     *
     *  • Forma "pill" (bordas completamente arredondadas)
     *  • Gradiente vertical (topo claro → base escura)
     *  • Animação de cor ao hover (interpolação linear 8 passos)
     *  • Efeito de escala ao pressionar (shrink to 94% + bounce back)
     *  • Sombra difusa abaixo do botão
     *  • Brilho interno sutil no topo
     *
     * ┌── Como usar ─────────────────────────────────────────┐
     *   BotaoAnimado btn = new BotaoAnimado(
     *       "Texto", COR_IDLE, COR_HOVER, COR_PRESS
     *   );
     *   btn.addActionListener(e -> { ... });
     * └──────────────────────────────────────────────────────┘
     */
    private static class BotaoAnimado extends JButton {

        private final Color corIdle;
        private final Color corHover;
        private final Color corPress;

        private Color   corAtual;        // cor interpolada no momento
        private float   escala = 1.0f;  // escala atual (animação de clique)
        private boolean mouseOver = false;
        private Timer   timerCor;        // Timer de animação de cor

        public BotaoAnimado(String texto, Color corIdle, Color corHover, Color corPress) {
            super(texto);
            this.corIdle  = corIdle;
            this.corHover = corHover;
            this.corPress = corPress;
            this.corAtual = corIdle;

            // Remove toda decoração padrão do Swing
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(FONTE_BOTAO);
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            registrarEventosMouse();
        }

        // ── Registro de eventos de mouse ──────────────────────

        private void registrarEventosMouse() {
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    mouseOver = true;
                    animarCor(corHover);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    mouseOver = false;
                    animarCor(corIdle);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    animarEscala(0.94f); // contrai levemente
                    animarCor(corPress);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    animarEscala(1.0f);  // volta ao tamanho original
                    animarCor(mouseOver ? corHover : corIdle);
                }
            });
        }

        // ── Animação de cor ────────────────────────────────────

        /**
         * Interpola a cor de fundo do valor atual até o alvo em 8 passos de 16ms.
         */
        private void animarCor(Color alvo) {
            if (timerCor != null && timerCor.isRunning()) timerCor.stop();

            Color  inicio = corAtual;
            int    passos = 8;
            int[]  passo  = {0};

            timerCor = new Timer(16, e -> {
                passo[0]++;
                float t = Math.min(1f, (float) passo[0] / passos);
                corAtual = lerp(inicio, alvo, t);
                repaint();
                if (passo[0] >= passos) timerCor.stop();
            });
            timerCor.start();
        }

        // ── Animação de escala ─────────────────────────────────

        /**
         * Anima a escala do botão em 6 passos de 12ms (efeito de pressionar).
         */
        private void animarEscala(float alvo) {
            float inicio = escala;
            int   passos = 6;
            int[] passo  = {0};

            Timer t = new Timer(12, null);
            t.addActionListener(e -> {
                passo[0]++;
                escala = inicio + (alvo - inicio) * ((float) passo[0] / passos);
                repaint();
                if (passo[0] >= passos) t.stop();
            });
            t.start();
        }

        // ── Utilidades de cor ──────────────────────────────────

        /** Interpolação linear entre duas cores (t de 0.0 a 1.0). */
        private Color lerp(Color a, Color b, float t) {
            int r  = clamp((int)(a.getRed()   + (b.getRed()   - a.getRed())   * t));
            int g  = clamp((int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t));
            int bl = clamp((int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
            return new Color(r, g, bl);
        }

        /** Clareia uma cor por um fator (0 = sem mudança, 1 = branco). */
        private Color clarear(Color c, float fator) {
            return lerp(c, Color.WHITE, fator);
        }

        /** Garante que o valor de canal RGB fique entre 0 e 255. */
        private int clamp(int v) { return Math.max(0, Math.min(255, v)); }

        // ── Renderização ───────────────────────────────────────

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Aplica escala ao redor do centro do botão
            g2.translate(w / 2.0, h / 2.0);
            g2.scale(escala, escala);
            g2.translate(-w / 2.0, -h / 2.0);

            int raio = h; // raio = altura → pill shape

            // Sombra difusa
            g2.setColor(new Color(0, 0, 0, 55));
            g2.fill(new RoundRectangle2D.Float(2, 5, w - 4, h - 4, raio, raio));

            // Gradiente de fundo: topo claro → base escura
            g2.setPaint(new GradientPaint(
                    0, 0, clarear(corAtual, 0.18f),
                    0, h, corAtual
            ));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h - 2, raio, raio));

            // Brilho interno (linha branca translúcida no topo)
            g2.setColor(new Color(255, 255, 255, 35));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 3, raio, raio));

            // Texto centrado
            g2.setFont(getFont());
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(),
                    (w - fm.stringWidth(getText())) / 2,
                    (h - fm.getHeight()) / 2 + fm.getAscent()
            );

            g2.dispose();
        }
    }

    // =========================================================
    //
    //  INNER CLASS: PainelGradiente
    //
    // =========================================================

    /**
     * JPanel com gradiente diagonal do canto superior esquerdo ao inferior direito.
     * Usado como painel raiz da janela para criar profundidade visual.
     *
     * ┌── Como usar ─────────────────────────────────────────┐
     *   PainelGradiente raiz = new PainelGradiente(cor1, cor2);
     *   raiz.setLayout(new BorderLayout());
     *   setContentPane(raiz);
     * └──────────────────────────────────────────────────────┘
     *
     * Nota: super.paintComponent() é chamado DEPOIS de pintar o gradiente,
     * para que os filhos sejam renderizados por cima do fundo.
     */
    private static class PainelGradiente extends JPanel {

        private final Color corInicio;
        private final Color corFim;

        public PainelGradiente(Color corInicio, Color corFim) {
            this.corInicio = corInicio;
            this.corFim    = corFim;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new GradientPaint(0, 0, corInicio, getWidth(), getHeight(), corFim));
            g2.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g); // filhos pintados por cima
        }
    }
}

