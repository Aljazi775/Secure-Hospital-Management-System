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

import java.util.ArrayList;

public class AppointmentView {

    private Stage stage;
    private TableView<Appointment> table;
    private ObservableList<Appointment> appointmentData;
    private TextField patientIdInput;
    private TextField doctorIdInput;
    private TextField datetimeInput;

    public AppointmentView(Stage s) {
        this.stage = s;
    }

    public void initializeComponents() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Appointments");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        table = new TableView<>();

        TableColumn<Appointment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Appointment, Integer> patientCol = new TableColumn<>("Patient ID");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));

        TableColumn<Appointment, Integer> doctorCol = new TableColumn<>("Doctor ID");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorId"));

        TableColumn<Appointment, String> datetimeCol = new TableColumn<>("Date & Time");
        datetimeCol.setCellValueFactory(new PropertyValueFactory<>("apptDatetime"));

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, patientCol, doctorCol, datetimeCol, statusCol);

        loadData();

        HBox formBox = new HBox(10);
        formBox.setAlignment(Pos.CENTER);

        patientIdInput = new TextField();
        patientIdInput.setPromptText("Patient ID");

        doctorIdInput = new TextField();
        doctorIdInput.setPromptText("Doctor ID");

        datetimeInput = new TextField();
        datetimeInput.setPromptText("YYYY-MM-DD HH:MM:SS");

        Button bookButton = new Button("Book Appointment");
        bookButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bookAppointment();
            }
        });

        formBox.getChildren().addAll(patientIdInput, doctorIdInput, datetimeInput, bookButton);

        root.getChildren().addAll(title, table, formBox);

        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.setTitle("HMS - Appointments");
        stage.show();
    }

    private void loadData() {
        ArrayList<Appointment> list = AppointmentDAO.getAllAppointments();
        appointmentData = FXCollections.observableArrayList(list);
        table.setItems(appointmentData);
    }

    private void bookAppointment() {
        String pidText = patientIdInput.getText();
        String didText = doctorIdInput.getText();
        String datetime = datetimeInput.getText();

        if (pidText.isEmpty() || didText.isEmpty() || datetime.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields are required");
            alert.showAndWait();
            return;
        }

        int patientId = Integer.parseInt(pidText);
        int doctorId = Integer.parseInt(didText);

        Appointment appt = new Appointment(patientId, doctorId, datetime, "Scheduled");
        boolean success = AppointmentDAO.addAppointment(appt);

        if (success) {
            loadData();
            patientIdInput.clear();
            doctorIdInput.clear();
            datetimeInput.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to book appointment");
            alert.showAndWait();
        }
    }
}
