import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class UIApp extends Application {

    enum style {BASE, GREY, DARK}

    private Stage primaryStage;
    private Scene primaryScene;
    private PrimaryController controller;


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader UX = new FXMLLoader();
        URL url = getClass().getResource("resource/UX.fxml");
        this.primaryStage = primaryStage;
        UX.setLocation(url);
        UX.setBuilderFactory(new JavaFXBuilderFactory());
        Parent load = UX.load(url.openStream());
        controller = UX.getController();
        primaryScene = new Scene(load , 800,500);
        addStyleSheet(style.DARK);
        primaryStage.setScene(primaryScene);
        this.primaryStage.show();


    }

    public void addStyleSheet(style chosenStyle){
        if(!primaryScene.getStylesheets().isEmpty())
            primaryScene.getStylesheets().clear();
        switch (chosenStyle){
            case BASE:
                break;
            case GREY:
                primaryScene.getStylesheets().add(getClass().getResource("resource/RES-Grey.css").toExternalForm());
                break;
            case DARK:
                primaryScene.getStylesheets().add(getClass().getResource("resource/RES-Dark.css").toExternalForm());
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}


