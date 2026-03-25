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
import java.util.HashMap;

public class AppointmentView {

    private Stage stage;
    private String callerRole;
    private TableView<Appointment> table;
    private ObservableList<Appointment> appointmentData;
    private ComboBox<String> patientDropdown;
    private ComboBox<String> doctorDropdown;
    private TextField dateInput;
    private TextField timeInput;
    private HashMap<String, Integer> patientMap;
    private HashMap<String, Integer> doctorMap;

    public AppointmentView(Stage s, String callerRole) {
        this.stage = s;
        this.callerRole = callerRole;
        this.patientMap = new HashMap<>();
        this.doctorMap = new HashMap<>();
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

        // Booking form - only shown to Receptionist
        HBox formBox = new HBox(10);
        formBox.setAlignment(Pos.CENTER);

        patientDropdown = new ComboBox<>();
        patientDropdown.setPromptText("Select Patient");
        loadPatients();

        doctorDropdown = new ComboBox<>();
        doctorDropdown.setPromptText("Select Doctor");
        loadDoctors();

        dateInput = new TextField();
        dateInput.setPromptText("Date: YYYY-MM-DD");
        dateInput.setPrefWidth(130);

        timeInput = new TextField();
        timeInput.setPromptText("Time: HH:MM");
        timeInput.setPrefWidth(100);

        Button bookButton = new Button("Book Appointment");
        bookButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bookAppointment();
            }
        });

        formBox.getChildren().addAll(patientDropdown, doctorDropdown, dateInput, timeInput, bookButton);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (callerRole.equals("Admin")) {
                    ManageUsers manageScreen = new ManageUsers(stage);
                    manageScreen.initializeComponents();
                } else {
                    PatientView patientScreen = new PatientView(stage, callerRole);
                    patientScreen.initializeComponents();
                }
            }
        });

        root.getChildren().addAll(title, table);

        // Only receptionist can book
        if (callerRole.equals("Receptionist")) {
            root.getChildren().add(formBox);
        }

        root.getChildren().add(backButton);

        Scene scene = new Scene(root, 800, 550);
        stage.setScene(scene);
        stage.setTitle("HMS - Appointments");
        stage.show();
    }

    private void loadPatients() {
        Connection con = DBUtils.establishConnection();
        try {
            String q = "SELECT id, first_name, last_name FROM patients";
            PreparedStatement pstmt = con.prepareStatement(q);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String display = rs.getString("first_name") + " " + rs.getString("last_name");
                patientMap.put(display, rs.getInt("id"));
                patientDropdown.getItems().add(display);
            }
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void loadDoctors() {
        Connection con = DBUtils.establishConnection();
        try {
            String q = "SELECT id, username FROM users WHERE role = 'Doctor'";
            PreparedStatement pstmt = con.prepareStatement(q);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String display = rs.getString("username");
                doctorMap.put(display, rs.getInt("id"));
                doctorDropdown.getItems().add(display);
            }
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void loadData() {
        ArrayList<Appointment> list = AppointmentDAO.getAllAppointments();
        appointmentData = FXCollections.observableArrayList(list);
        table.setItems(appointmentData);
    }

    private void bookAppointment() {
        String selectedPatient = patientDropdown.getValue();
        String selectedDoctor = doctorDropdown.getValue();
        String date = dateInput.getText();
        String time = timeInput.getText();

        if (selectedPatient == null || selectedDoctor == null || date.isEmpty() || time.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields are required");
            alert.showAndWait();
            return;
        }

        // combine date and time into the DB format
        String datetime = date + " " + time + ":00";

        int patientId = patientMap.get(selectedPatient);
        int doctorId = doctorMap.get(selectedDoctor);

        Appointment appt = new Appointment(patientId, doctorId, datetime, "Scheduled");
        boolean success = AppointmentDAO.addAppointment(appt);

        if (success) {
            loadData();
            patientDropdown.getSelectionModel().clearSelection();
            doctorDropdown.getSelectionModel().clearSelection();
            dateInput.clear();
            timeInput.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to book appointment");
            alert.showAndWait();
        }
    }
}
