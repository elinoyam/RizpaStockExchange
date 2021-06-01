import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class UIApp extends Application {

    enum style {

        BASE(0,null),
        GREY(1,"resource/RSE-Grey.css"),
        DARK(2, "resource/RSE-Dark.css");

        private final Integer id;
        private final String url;

        style(Integer _id, String _url){
            this.id = _id.intValue();
            if(_url==null)
                this.url = null;
            else
                this.url = _url;
        }

        public static style getStyleByID(int id)
        {
            for(style s: style.values())
            {
                if(s.id.intValue() == id)
                    return s;
            }
            return null;
        }

        public String getURL(){
            return this.url;
        }
    }

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
       // primaryScene.getStylesheets().add(getClass().getResource("resource/RSE-Dark.css").toExternalForm());
        //primaryScene.getStylesheets().add(getClass().getResource("resource/RES-Grey.css").toExternalForm());
        controller.styleSliderChangedProperty().addListener(/*new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue){
                addStyleSheet(0,1);//Integer.getInteger(oldValue.toString()),Integer.getInteger(newValue.toString()));
            }
        }*/(observable, oldValue, newValue) -> {
            addStyleSheet(oldValue.intValue(), newValue.intValue());
        });
        primaryStage.setScene(primaryScene);
        this.primaryStage.show();


    }

    public void addStyleSheet(int oldValue , int newValue){
        style oldStyle = style.getStyleByID(oldValue);
        style newStyle = style.getStyleByID(newValue);
        if(!oldStyle.equals(style.BASE))
            primaryScene.getStylesheets().remove(getClass().getResource(oldStyle.getURL()).toExternalForm());
        //primaryScene.getStylesheets().add(getClass().getResource("resource/RES-Grey.css").toExternalForm());
        if(!newStyle.equals(style.BASE))
            primaryScene.getStylesheets().add(getClass().getResource(newStyle.getURL()).toExternalForm());
    }

    public static void main(String[] args) {
        launch(args);
    }

}


