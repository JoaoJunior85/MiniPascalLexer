package gui;

import lexer.AnalisadorLexico;
import model.TipoToken;
import model.Token;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Executa a análise léxica e popula incrementalmente as tabelas de tokens/erros
 * com animação. Notifica o status via callback (texto, cor).
 */
public class ExecutorAnalise {

    private final JTable tabelaTokens;
    private final JTable tabelaErros;
    private final JProgressBar barra;
    private final BiConsumer<String, Color> status;

    public ExecutorAnalise(JTable tabelaTokens, JTable tabelaErros,
                           JProgressBar barra, BiConsumer<String, Color> status) {
        this.tabelaTokens = tabelaTokens;
        this.tabelaErros = tabelaErros;
        this.barra = barra;
        this.status = status;
    }

    public void analisar(String codigo) {
        List<Token> tokens = new AnalisadorLexico().analisar(codigo);

        DefaultTableModel modTok = (DefaultTableModel) tabelaTokens.getModel();
        DefaultTableModel modErr = (DefaultTableModel) tabelaErros.getModel();
        modTok.setRowCount(0);
        modErr.setRowCount(0);

        barra.setVisible(true);
        barra.setMaximum(Math.max(1, tokens.size()));
        barra.setValue(0);

        final int[] i = {0};
        final int[] erros = {0};
        Timer animacao = new Timer(18, null);
        animacao.addActionListener(e -> {
            if (i[0] >= tokens.size()) {
                animacao.stop();
                barra.setVisible(false);
                int totalTok = tokens.size() - erros[0];
                if (erros[0] == 0)
                    status.accept("✅ Análise concluída — " + totalTok + " tokens, sem erros", Tema.SUCESSO);
                else
                    status.accept("⚠ Análise concluída — " + totalTok + " tokens, " + erros[0] + " erro(s)", Tema.ERRO);
                return;
            }
            Token tk = tokens.get(i[0]++);
            barra.setValue(i[0]);
            if (tk.getTipo() != TipoToken.ERRO)
                modTok.addRow(new Object[]{tk.getLexema(), tk.getTipo(), tk.getLinha()});
            else {
                modErr.addRow(new Object[]{tk.getLexema(), tk.getLinha()});
                erros[0]++;
            }
        });
        status.accept("Analisando...", Tema.OPCIONAL);
        animacao.start();
    }

    public void limparResultados() {
        ((DefaultTableModel) tabelaTokens.getModel()).setRowCount(0);
        ((DefaultTableModel) tabelaErros.getModel()).setRowCount(0);
        status.accept("Resultados limpos", Tema.GUTTER_TEXTO);
    }
}
