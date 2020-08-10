package org.twelve.views;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.twelve.controllers.RegistrationController;
import org.twelve.presenters.RegistrationPresenter;
import org.twelve.presenters.ui.ObservablePresenter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RegistrationView<T extends ObservablePresenter & RegistrationPresenter> implements SceneView, Initializable {

    @FXML
    private GridPane graphic;

    @FXML
    private TextField usernameBox;

    @FXML
    private TextField passwordBox;

    @FXML
    private ComboBox<String> locationBox;

    @FXML
    private ComboBox<String> typeBox;

    @FXML
    private Label errorLabel;

    private final WindowHandler windowHandler;
    private final RegistrationController registrationController;
    private final T registrationPresenter;

    public RegistrationView(WindowHandler windowHandler, RegistrationController registrationController, T registrationPresenter) {
        this.windowHandler = windowHandler;
        this.registrationPresenter = registrationPresenter;

        this.registrationController = registrationController;
        this.registrationController.setRegistrationPresenter(this.registrationPresenter);

    }

    @FXML
    private void registerClicked() {

        if (registrationController.createAccount(usernameBox.getText(), passwordBox.getText(), locationBox.getValue(),
                typeBox.getSelectionModel().getSelectedIndex())) {

            windowHandler.changeScene(Scenes.MENU);

        }

    }

    @FXML
    private void backClicked() {

        if (typeBox.getItems().size() > 1) {
            windowHandler.changeScene(Scenes.LANDING);
        } else {
            windowHandler.changeScene(Scenes.MENU);
        }

    }

    @Override
    public void reload() {
        registrationController.updateOptions();
    }

    @Override
    public Parent getGraphic() {
        return graphic;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {

            ReadOnlyJavaBeanObjectProperty<List<String>> availableTypesBinding =
                    ReadOnlyJavaBeanObjectPropertyBuilder.<List<String>>create().bean(registrationPresenter).name("availableTypes").build();

            ObjectBinding<ObservableList<String>> typesBinding = Bindings.createObjectBinding(() ->
                    FXCollections.observableArrayList(availableTypesBinding.get()), availableTypesBinding);

            typeBox.itemsProperty().bind(typesBinding);

            ReadOnlyJavaBeanObjectProperty<List<String>> existingCitiesBinding =
                    ReadOnlyJavaBeanObjectPropertyBuilder.<List<String>>create().bean(registrationPresenter).name("existingCities").build();

            ObjectBinding<ObservableList<String>> citiesBinding = Bindings.createObjectBinding(() ->
                    FXCollections.observableArrayList(existingCitiesBinding.get()), existingCitiesBinding);

            locationBox.itemsProperty().bind(citiesBinding);

            errorLabel.textProperty().bind(ReadOnlyJavaBeanStringPropertyBuilder.create()
                    .bean(registrationPresenter).name("error").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
