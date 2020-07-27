package org.twelve.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class WindowHandler extends Application {

    private Stage primaryStage;
    private Map<Scenes, Scene> scenes;

    // don't add parameters to this, Application.launch() will explode if you do
    public WindowHandler() {

        ViewBuilder viewBuilder = new ViewBuilder(this);

        if (!viewBuilder.buildGateways("json")) return; // we can add more flexibility if we want here
        viewBuilder.buildControllers();

        scenes = new HashMap<>();
        for (Scenes scene : Scenes.values()) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(scene.toString() + "View.fxml"),
                    ResourceBundle.getBundle("org.twelve.presenters." + scene.toString()));

            loader.setControllerFactory(v -> viewBuilder.getView(scene));

            try {
                scenes.put(scene, new Scene(loader.load()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        this.primaryStage.setTitle("Trading Application");
        changeScene(Scenes.LANDING);
        this.primaryStage.show();

    }

    public void changeScene(Scenes scene) {
        primaryStage.setScene(scenes.get(scene));
    }

}
