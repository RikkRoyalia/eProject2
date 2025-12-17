package SHAIF;

import javafx.application.Application;
import javafx.stage.Stage;
import SHAIF.view.GameView;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new GameView(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
