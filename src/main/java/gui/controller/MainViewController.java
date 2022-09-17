package gui.controller;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML
    private MenuItem menuItemSeller;
    @FXML
    private MenuItem menuItemDepartment;
    @FXML
    private MenuItem menuItemAbout;

    @FXML
    public void onMenuItemSellerAction() {
        System.out.println("onMenuItemSellerAction");
    }

    @FXML
    public void onMenuItemDepartmentAction() {
        loadView("/gui/DepartmentList.fxml");
    }

    @FXML
    public void onMenuItemAboutAction() {
        loadView("/gui/About.fxml");
    }

    @Override
    public void initialize(URL uri, ResourceBundle rb) {

    }

    // Método utilizado para abrir uma tela em cima da outra.
    // synchronized é utilizado para que o processamento não seja interrompido durante a multi-thread.
    private synchronized void loadView(String absoluteName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVBox = loader.load();

            Scene mainScene = Main.getMainScene();
            // Irá pegar o primeiro elemento da View, no caso o ScrollPane.
            // Assim a referência será pega da tela principal.
            VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

            // Irá atribuir ao Node, o mainMenu da tela principal.
            Node mainMenu = mainVBox.getChildren().get(0);
            // Irá limpar todos os filhos do mainVBox
            mainVBox.getChildren().clear();
            // Irá adicionar os filhos de mainMenu
            mainVBox.getChildren().add(mainMenu);
            // Irá adicionar os filhos de newVbox
            mainVBox.getChildren().addAll(newVBox.getChildren());

        } catch (IOException e) {
            Alerts.showAlert("IO Excepiton", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
