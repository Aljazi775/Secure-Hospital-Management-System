import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CommandListing {

    private Stage stage;
    private TableView<AuditLog> table;
    private ObservableList<AuditLog> logData;

    public CommandListing(Stage s) {
        this.stage = s;
    }

    public void initializeComponents() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Audit Log - Admin View");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        table = new TableView<>();

        TableColumn<AuditLog, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<AuditLog, Integer> userCol = new TableColumn<>("User ID");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<AuditLog, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));

        TableColumn<AuditLog, String> tableCol = new TableColumn<>("Table");
        tableCol.setCellValueFactory(new PropertyValueFactory<>("tableName"));

        TableColumn<AuditLog, String> oldCol = new TableColumn<>("Old Value");
        oldCol.setCellValueFactory(new PropertyValueFactory<>("oldValue"));

        TableColumn<AuditLog, String> newCol = new TableColumn<>("New Value");
        newCol.setCellValueFactory(new PropertyValueFactory<>("newValue"));

        TableColumn<AuditLog, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("logTime"));

        table.getColumns().addAll(idCol, userCol, actionCol, tableCol, oldCol, newCol, timeCol);

        loadData();

        Button backButton = new Button("Back to Admin");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ManageUsers manageScreen = new ManageUsers(stage);
                manageScreen.initializeComponents();
            }
        });

        root.getChildren().addAll(title, table, backButton);

        Scene scene = new Scene(root, 950, 520);
        stage.setScene(scene);
        stage.setTitle("HMS - Audit Logs");
        stage.show();
    }

    private void loadData() {
        ArrayList<AuditLog> list = AuditDAO.getAllLogs();
        logData = FXCollections.observableArrayList(list);
        table.setItems(logData);
    }
}
