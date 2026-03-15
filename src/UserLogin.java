import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserLogin {

    private Stage primaryStage;
    private TextField usernameField;
    private PasswordField passwordField;

    public UserLogin(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void initializeComponents() {


        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(  "HMS Login");
        usernameField = new TextField();
        usernameField.setPromptText( "Enter Username");
        usernameField.setMaxWidth(200);
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password" );
        passwordField.setMaxWidth(200);
        Button loginButton = new Button("Login");
        Label messageLabel = new Label();





        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                authenticate(messageLabel);
            }
        });

        root.getChildren().add(titleLabel);
        root.getChildren().add(usernameField);
        root.getChildren().add(passwordField);
        root.getChildren().add(loginButton);
        root.getChildren().add(messageLabel);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("HMS Application");
        primaryStage.show();
    }

    private void authenticate(Label messageLabel) {
        String username = usernameField.getText();
        String password = passwordField.getText();    
        
        User loggedInUser = AuthenticationService.authenticate(username, password);

        if (loggedInUser != null) {
            if (loggedInUser.isLocked()) {
                messageLabel.setText("Account is locked!");
                return;
            }

            if (AuthorizationService.isAdmin(loggedInUser)) {
                System.out.println("You are authorized to manage users");
                messageLabel.setText("Admin Login Success!");
            } else if (AuthorizationService.isDoctor(loggedInUser)) {
                System.out.println("You are authorized to manage medical records");
                messageLabel.setText("Doctor Login Success!");
            } else {
                System.out.println("Welcome " + loggedInUser.getUsername());
                messageLabel.setText("Login Success!");
            }
        } else {
            messageLabel.setText("Invalid username or password");
            showAlert("Authentication Failed", "Invalid username or password.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
