package gui;

import javax.swing.*;
import java.awt.event.ActionListener;

import static gui.FabricaUI.item;

/** Constrói a JMenuBar a partir de ações fornecidas pela tela. */
public final class ConstrutorMenu {

    private ConstrutorMenu() {}

    public static class Acoes {
        public ActionListener novo, abrir, salvar, salvarComo, sair;
        public ActionListener recortar, copiar, colar, limparTudo, selecionarTudo, copiarTokens;
        public ActionListener analisar, limparResultados;
        public ActionListener temaEscuro, temaClaro;
        public ActionListener aumentarFonte, diminuirFonte, resetarFonte;
        public ActionListener exemplo1, exemplo2, exemplo3;
        public ActionListener atalhos, sobre;
    }

    public static JMenuBar construir(Acoes a) {
        JMenuBar mb = new JMenuBar();

        JMenu mArquivo = new JMenu("Arquivo");
        mArquivo.setMnemonic('A');
        mArquivo.add(item("📄  Novo",          "ctrl N",       a.novo));
        mArquivo.add(item("📂  Abrir...",       "ctrl O",       a.abrir));
        mArquivo.add(item("💾  Salvar",         "ctrl S",       a.salvar));
        mArquivo.add(item("💾  Salvar Como...", "ctrl shift S", a.salvarComo));
        mArquivo.addSeparator();
        mArquivo.add(item("🚪  Sair", "ctrl Q", a.sair));

        JMenu mEditar = new JMenu("Editar");
        mEditar.setMnemonic('E');
        mEditar.add(item("✂  Recortar",       "ctrl X",       a.recortar));
        mEditar.add(item("📋  Copiar",         "ctrl C",       a.copiar));
        mEditar.add(item("📌  Colar",          "ctrl V",       a.colar));
        mEditar.add(item("🧹  Limpar Tudo",    "ctrl shift L", a.limparTudo));
        mEditar.addSeparator();
        mEditar.add(item("🔎  Selecionar Tudo","ctrl A",       a.selecionarTudo));
        mEditar.add(item("📑  Copiar Tokens",  null,           a.copiarTokens));

        JMenu mAnalisar = new JMenu("Analisar");
        mAnalisar.setMnemonic('N');
        mAnalisar.add(item("⚙  Executar Análise", "F5", a.analisar));
        mAnalisar.add(item("🗑  Limpar Resultados", "F6", a.limparResultados));

        JMenu mExibir = new JMenu("Exibir");
        mExibir.setMnemonic('X');
        JMenu temas = new JMenu("🎨  Tema");
        temas.add(item("Escuro (Darcula)",  null, a.temaEscuro));
        temas.add(item("Claro (Mac Light)", null, a.temaClaro));
        mExibir.add(temas);
        mExibir.addSeparator();
        mExibir.add(item("🔍+  Aumentar Fonte", "ctrl PLUS",  a.aumentarFonte));
        mExibir.add(item("🔍-  Diminuir Fonte", "ctrl MINUS", a.diminuirFonte));
        mExibir.add(item("↺   Resetar Fonte",   "ctrl 0",     a.resetarFonte));

        JMenu mExemplos = new JMenu("Exemplos");
        mExemplos.setMnemonic('P');
        mExemplos.add(item("Hello World",      null, a.exemplo1));
        mExemplos.add(item("Fatorial (loop)",  null, a.exemplo2));
        mExemplos.add(item("Código com Erros", null, a.exemplo3));

        JMenu mAjuda = new JMenu("Ajuda");
        mAjuda.setMnemonic('J');
        mAjuda.add(item("⌨  Atalhos", "F1", a.atalhos));
        mAjuda.add(item("ℹ  Sobre",   null, a.sobre));

        mb.add(mArquivo);
        mb.add(mEditar);
        mb.add(mAnalisar);
        mb.add(mExibir);
        mb.add(mExemplos);
        mb.add(mAjuda);
        return mb;
    }
}
