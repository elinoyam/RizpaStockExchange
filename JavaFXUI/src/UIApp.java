import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class UIApp extends Application {

    private Stage primaryStage;


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader UX = new FXMLLoader();
        URL url = getClass().getResource("resource/UX.fxml");
        this.primaryStage = primaryStage;
        UX.setLocation(url);
        UX.setBuilderFactory(new JavaFXBuilderFactory());
        Parent load = UX.load(url.openStream());
        Scene primaryScene = new Scene(load , 800,500);
        primaryScene.getStylesheets().add(getClass().getResource("resource/RES-Dark.css").toExternalForm());
        primaryStage.setScene(primaryScene);
        this.primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }

}


