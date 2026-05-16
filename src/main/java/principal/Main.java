import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import gui.TelaPrincipal;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Main {

    public static void main(String[] args) {

        try {

            UIManager.setLookAndFeel(
                    new FlatMacLightLaf()
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