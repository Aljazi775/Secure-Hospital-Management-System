import org.mindrot.jbcrypt.BCrypt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ManageUsers {

    private Stage stage;
    private TableView<User> table;
    private ObservableList<User> userData;
    private TextField usernameInput;
    private PasswordField passwordInput;
    private ComboBox<String> roleInput;

    public ManageUsers(Stage s) {
        this.stage = s;
    }

    public void initializeComponents() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        
        Label title = new Label("Manage System Users");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Set up the table
        table = new TableView<>();
        
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, Boolean> lockedCol = new TableColumn<>("Locked");
        lockedCol.setCellValueFactory(new PropertyValueFactory<>("locked"));

        table.getColumns().addAll(idCol, userCol, roleCol, lockedCol);

        // Load data from db
        loadData();

        // Form to add new user
        HBox formBox = new HBox(10);
        formBox.setAlignment(Pos.CENTER);

        usernameInput = new TextField();
        usernameInput.setPromptText("Username");
        
        passwordInput = new PasswordField();
        passwordInput.setPromptText("Password");

        roleInput = new ComboBox<>();
        roleInput.getItems().addAll("Admin", "Doctor", "Receptionist");
        roleInput.setPromptText("Role");
        
        Button addButton = new Button("Add User");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addUser();
            }
        });

        formBox.getChildren().addAll(usernameInput, passwordInput, roleInput, addButton);

        root.getChildren().addAll(title, table, formBox);

        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.setTitle("HMS - Manage Users");
        stage.show();
    }

    private void loadData() {
        Connection con = DBUtils.establishConnection();
        ArrayList<User> list = new ArrayList<>();
        try {
            String q = "SELECT * FROM users";
            PreparedStatement pstmt = con.prepareStatement(q);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User temp = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("role")
                );
                temp.setLocked(rs.getBoolean("is_locked"));
                list.add(temp);
            }
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        userData = FXCollections.observableArrayList(list);
        table.setItems(userData);
    }

    private void addUser() {
        String u = usernameInput.getText();
        String p = passwordInput.getText();
        String r = roleInput.getValue();

        if (u.isEmpty() || p.isEmpty() || r == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Missing fields");
            alert.showAndWait();
            return;
        }

        String salt = BCrypt.gensalt(12);
        String hashedPassword = BCrypt.hashpw(p, salt);

        Connection con = DBUtils.establishConnection();
        try {
            String query = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, u);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, r);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                loadData(); // refresh table
                usernameInput.clear();
                passwordInput.clear();
                roleInput.getSelectionModel().clearSelection();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save user");
                alert.showAndWait();
            }
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
