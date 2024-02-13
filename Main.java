package com.example.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.security.cert.PolicyNode;

public class Main extends Application {
    public static void main(String[] args) {
        //System.out.println("main");
        launch(args);
    }

    @Override
    public void init() throws Exception{
        //System.out.println("Init method");
        super.init();
    }

    private GameController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //System.out.println("start");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));

        GridPane rootGridPane = loader.load();

        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty()); //Making the MenuBar to Occupy the entire row

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu(){
        //File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newGame = new MenuItem("New game");
        newGame.setOnAction(actionEvent -> {controller.resetGame();});

        MenuItem resetGame = new MenuItem("Reset game");
        resetGame.setOnAction(actionEvent -> {controller.resetGame();});

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem exitGame = new MenuItem("Exit game");
        exitGame.setOnAction(actionEvent -> exitGame());//Adding functionality to the exit

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

        //Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutGame = new MenuItem("About Connect4");
        aboutGame.setOnAction(actionEvent -> aboutConnect4());//Adding functionality to the About Connect4

        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(actionEvent -> aboutMe());//Adding functionality to the About Me

        helpMenu.getItems().addAll(aboutGame,separator,aboutMe);

        //Menu Bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);

        return menuBar;
    }


    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Yaswanth");
        alert.setContentText("I love to play along with code and create games.\n" +
                "connect four is one of them. In free time\n" +
                "I like to spend time with nears and dears.");
        alert.show();
    }

    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About connect Four");
        alert.setHeaderText("How to play?");
        alert.setContentText("Connect Four is a two-player connection game in which the\n" +
                "players first choose a color and then take turns dropping colored discs\n" +
                "from the top into a seven-column, six-row vertically suspended grid. \n" +
                "The pieces fall straight down, occupying the next available space within the column.\n" +
                "The objective of the game is to be the first to form a horizontal, vertical,\n" +
                "or diagonal line of four of one's own discs. Connect Four is a solved game.\n" +
                "The first player can always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }


    @Override
    public void stop() throws Exception {
        //System.out.println("stop");
        super.stop();
    }
}