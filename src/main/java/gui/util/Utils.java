package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

    public static Stage currentStage(ActionEvent event) {
        // Acessar o stage que o controle que recebeu evento está
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    public static Integer tryParseToInt(String str) {
        // Não haverá risco de acontecer exceção, pois assim, irá capturar e retornar o valor nulo.
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }

    }

}
