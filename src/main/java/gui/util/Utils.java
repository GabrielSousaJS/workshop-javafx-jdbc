package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

    public static Stage currentStage(ActionEvent event) {
        // Acessar o stage que o controle que recebeu evento está
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

}
