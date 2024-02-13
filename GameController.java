package com.example.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameController implements Initializable {
    private static final int columns =7;
    private static final int rows =6;
    private static final int circle_diameter = 80;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";
    private static String player_ONE = "Player One";
    private static String player_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;
    private Disc[][] insertedDiscsArray = new Disc[rows][columns]; //Structural changes for the developers

    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertedDiscsPane;

    @FXML
    public Label playerNameLabel;

    @FXML
    public TextField playerOneTextField, playerTwoTextField;

    @FXML
    public Button setNamesButton;

    private boolean isAllowedToInsert = true; //Flag to avoid same color disc being added.

    public void createPlayground(){
        Shape rectangleWithHoles = createGameStructuralGrid();
        rootGridPane.add(rectangleWithHoles,0,1);

        List<Rectangle> rectangleList = createClickableColumns();
        for(Rectangle rectangle: rectangleList) {
            rootGridPane.add(rectangle, 0, 1);
        }
    }

    private Shape createGameStructuralGrid(){
        Shape rectangleWithHoles = new Rectangle((columns+1)*circle_diameter,(rows+1)*circle_diameter);
        for(int row=0; row<rows; row++){
            for(int col=0; col<columns; col++){
                Circle circle = new Circle();
                circle.setRadius(circle_diameter/2);
                circle.setCenterX(circle_diameter/2);
                circle.setCenterY(circle_diameter/2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (circle_diameter+5)+ circle_diameter/4);
                circle.setTranslateY(row * (circle_diameter+5)+ circle_diameter/4);

                rectangleWithHoles = Shape.subtract(rectangleWithHoles,circle);
            }
        }

        rectangleWithHoles.setFill(Color.WHITE);
        return rectangleWithHoles;

    }

    private List<Rectangle> createClickableColumns(){

        List<Rectangle> rectangleList = new ArrayList<>();
        for(int col=0; col<columns; col++) {
            Rectangle rectangle = new Rectangle(circle_diameter, (rows + 1) * circle_diameter);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (circle_diameter+5) + circle_diameter / 4);
            //Hover Effect
            rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee36")));
            rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));

            //Adding some Click events on each rectangle
            final int column = col;
            rectangle.setOnMouseClicked(mouseEvent -> {
                if (isAllowedToInsert) {
                    isAllowedToInsert = false; //when disc is being dropped then no more disc will be inserted
                    insertDisc(new Disc(isPlayerOneTurn), column);
                }
            });


            rectangleList.add(rectangle);
        }
        return rectangleList;
    }

    private void insertDisc(Disc disc, int column){
        int row = rows-1;

        while(row>=0){
            if(getDiscIfPresent(row,column)==null)
                break;
            row--;
        }

        if(row<0) //If the row is full, we cannot insert anymore disc
            return;

        insertedDiscsArray[row][column] = disc; //Structural changes for the developers
        insertedDiscsPane.getChildren().add(disc);

        disc.setTranslateX(column * (circle_diameter+5) + circle_diameter / 4);

        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
        translateTransition.setToY(row * (circle_diameter+5)+ circle_diameter/4);
        translateTransition.setOnFinished(actionEvent -> {
            isAllowedToInsert=true; //Finally, when disc is dropped allow next player to insert disc
            if(gameEnded(currentRow,column)){
                //Do Something
                gameOver();
                return;
                
            }
            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? player_ONE: player_TWO);
        });
        translateTransition.play();
    }

    private boolean gameEnded(int row, int column) {
        //Vertical Points from where we can get the possible combinations
        //range of row values = 0,1,2,3,4,5
        //index of the element present in column [row][column] : 0,3  1,3  2,3  3,3  4,3  5,3 --> Point2D x,y
        List<Point2D> verticalPoints = IntStream.rangeClosed(row-3,row+3).mapToObj(r->new Point2D(r,column)).collect(Collectors.toList());

        //Horizontal Points
        List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3,column+3).mapToObj(col->new Point2D(row,col)).collect(Collectors.toList());

        //Diagonals
        Point2D startPoint1 = new Point2D(row-3,column+3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6).mapToObj(i->startPoint1.add(i,-i)).collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row-3,column-3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6).mapToObj(i->startPoint2.add(i,i)).collect(Collectors.toList());

        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints) || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> points) {
        int chain =0;
        for(Point2D point: points){

            int rowIndexForArray = (int)point.getX();
            int columnIndexForArray = (int)point.getY();

            //Finding the disc present this row and columnIndex
            Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);
            if(disc!=null && disc.isPlayerOneMove==isPlayerOneTurn){ //If the last inserted Disc belongs to the current player
                chain++;
                if(chain == 4)
                    return true;
            } else {
                chain=0;
            }
        }
        return false;
    }

    private Disc getDiscIfPresent(int row,int column){ //To prevent ArrayIndexOutOfBoundException
        if(row>=rows||row<0||column>=columns||column<0)
            return null;
        return insertedDiscsArray[row][column];
    }
    private void gameOver() {
        String winner = isPlayerOneTurn ? player_ONE : player_TWO;
        System.out.println("Winner is: "+winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The Winner is: "+winner);
        alert.setContentText("Want to play again? ");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesBtn,noBtn);

        Platform.runLater(()->{ //Helps us to resolve IllegalStateException
            Optional<ButtonType> bthClicked = alert.showAndWait();
            if(bthClicked.isPresent() && bthClicked.get()==yesBtn){
                //user has chosen Yes or RESET the game
                resetGame();
            } else{ //user has chosen No so EXIT the game
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {
        insertedDiscsPane.getChildren().clear(); //Remove all Inserted disc from Pane
        for(int row=0; row<insertedDiscsArray.length; row++){ //Structurally, making all elements of InsertedDiscs to null
            for (int col=0; col<insertedDiscsArray[row].length; col++){
                insertedDiscsArray[row][col]=null;
            }
        }
        isPlayerOneTurn=true; //Let player start the game
        playerNameLabel.setText(player_ONE);

        createPlayground(); //Prepare a fresh playground
    }

    private static class Disc extends Circle{
        private final boolean isPlayerOneMove;

        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(circle_diameter/2);
            setFill(isPlayerOneMove? Color.valueOf(discColor1) : Color.valueOf(discColor2));
            setCenterX(circle_diameter/2);
            setCenterY(circle_diameter/2);
        }
    }

    @FXML
    public void handleSetNamesButton(ActionEvent event) {
        // Get the names entered by the user from the text fields
        player_ONE = playerOneTextField.getText();
        player_TWO = playerTwoTextField.getText();

        // Update the player name label
        playerNameLabel.setText(isPlayerOneTurn ? player_ONE : player_TWO);

        /* Optionally, you can disable or hide the text fields and setNamesButton
        playerOneTextField.setDisable(true);
        playerTwoTextField.setDisable(true);
        setNamesButton.setDisable(true); */
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
