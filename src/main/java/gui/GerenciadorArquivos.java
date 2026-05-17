package gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Gerencia operações de arquivo (novo / abrir / salvar) e mantém o estado
 * do arquivo atual. Expõe callbacks simples para a janela hospedeira.
 */
public class GerenciadorArquivos {

    public interface Ouvinte {
        void aoCarregarTexto(String texto, File arquivo);
        void aoSalvar(File arquivo);
        void aoNovoArquivo(String textoPadrao);
    }

    private final Component pai;
    private final Ouvinte ouvinte;
    private File arquivoAtual;

    public GerenciadorArquivos(Component pai, Ouvinte ouvinte) {
        this.pai = pai;
        this.ouvinte = ouvinte;
    }

    public File arquivoAtual() { return arquivoAtual; }

    public void novo() {
        arquivoAtual = null;
        ouvinte.aoNovoArquivo(Exemplos.PADRAO);
    }

    public void abrir() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Arquivos Pascal (*.pas, *.txt)", "pas", "txt"));
        if (fc.showOpenDialog(pai) != JFileChooser.APPROVE_OPTION) return;
        try {
            arquivoAtual = fc.getSelectedFile();
            String texto = new String(Files.readAllBytes(arquivoAtual.toPath()));
            ouvinte.aoCarregarTexto(texto, arquivoAtual);
        } catch (IOException ex) {
            erro("Erro ao abrir: " + ex.getMessage());
        }
    }

    public void salvar(String conteudo, boolean salvarComo) {
        if (salvarComo || arquivoAtual == null) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Arquivo Pascal (*.pas)", "pas"));
            if (fc.showSaveDialog(pai) != JFileChooser.APPROVE_OPTION) return;
            arquivoAtual = fc.getSelectedFile();
            if (!arquivoAtual.getName().contains("."))
                arquivoAtual = new File(arquivoAtual.getAbsolutePath() + ".pas");
        }
        try (FileWriter w = new FileWriter(arquivoAtual)) {
            w.write(conteudo);
            ouvinte.aoSalvar(arquivoAtual);
        } catch (IOException ex) {
            erro("Erro ao salvar: " + ex.getMessage());
        }
    }

    private void erro(String msg) {
        JOptionPane.showMessageDialog(pai, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
