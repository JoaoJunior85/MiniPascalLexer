import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import gui.TelaPrincipal;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        try {

            UIManager.setLookAndFeel(
                    new FlatArcDarkOrangeIJTheme()
            );

        } catch (Exception e) {

            System.out.println(
                    "Erro ao carregar tema"
            );
        }

        SwingUtilities.invokeLater(() -> {

            new TelaPrincipal().setVisible(true);

        });

    }

}