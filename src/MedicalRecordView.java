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

public class MedicalRecordView {

    private Stage stage;
    private String callerRole;
    private TableView<MedicalRecord> table;
    private ObservableList<MedicalRecord> recordData;
    private ComboBox<String> patientDropdown;
    private ComboBox<String> doctorDropdown;
    private TextField diagnosisInput;
    private TextField prescriptionInput;
    private HashMap<String, Integer> patientMap;
    private HashMap<String, Integer> doctorMap;

    public MedicalRecordView(Stage s, String callerRole) {
        this.stage = s;
        this.callerRole = callerRole;
        this.patientMap = new HashMap<>();
        this.doctorMap = new HashMap<>();
    }

    public void initializeComponents() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Medical Records");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        table = new TableView<>();

        TableColumn<MedicalRecord, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<MedicalRecord, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        TableColumn<MedicalRecord, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        TableColumn<MedicalRecord, String> diagnosisCol = new TableColumn<>("Diagnosis");
        diagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diagnosisEnc"));

        TableColumn<MedicalRecord, String> prescriptionCol = new TableColumn<>("Prescription");
        prescriptionCol.setCellValueFactory(new PropertyValueFactory<>("prescriptionEnc"));

        TableColumn<MedicalRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        table.getColumns().addAll(idCol, patientCol, doctorCol, diagnosisCol, prescriptionCol, dateCol);

        loadData();

        HBox formBox = new HBox(10);
        formBox.setAlignment(Pos.CENTER);

        patientDropdown = new ComboBox<>();
        patientDropdown.setPromptText("Select Patient");
        loadPatients();

        doctorDropdown = new ComboBox<>();
        doctorDropdown.setPromptText("Select Doctor");
        loadDoctors();

        diagnosisInput = new TextField();
        diagnosisInput.setPromptText("Diagnosis");

        prescriptionInput = new TextField();
        prescriptionInput.setPromptText("Prescription");

        Button addButton = new Button("Add Record");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addRecord();
            }
        });

        formBox.getChildren().addAll(patientDropdown, doctorDropdown, diagnosisInput, prescriptionInput, addButton);

        Button backButton = new Button("Back to Patients");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PatientView patientScreen = new PatientView(stage, callerRole);
                patientScreen.initializeComponents();
            }
        });

        root.getChildren().addAll(title, table, formBox, backButton);

        Scene scene = new Scene(root, 950, 580);
        stage.setScene(scene);
        stage.setTitle("HMS - Medical Records");
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
        ArrayList<MedicalRecord> list = MedicalRecordDAO.getAllRecords();
        recordData = FXCollections.observableArrayList(list);
        table.setItems(recordData);
    }

    private void addRecord() {
        String selectedPatient = patientDropdown.getValue();
        String selectedDoctor = doctorDropdown.getValue();
        String diagnosis = diagnosisInput.getText();
        String prescription = prescriptionInput.getText();

        if (selectedPatient == null || selectedDoctor == null || diagnosis.isEmpty() || prescription.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields are required");
            alert.showAndWait();
            return;
        }

        int patientId = patientMap.get(selectedPatient);
        int doctorId = doctorMap.get(selectedDoctor);

        // DAO will encrypt before storing
        boolean success = MedicalRecordDAO.addRecord(patientId, doctorId, diagnosis, prescription);

        if (success) {
            loadData();
            patientDropdown.getSelectionModel().clearSelection();
            doctorDropdown.getSelectionModel().clearSelection();
            diagnosisInput.clear();
            prescriptionInput.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save medical record");
            alert.showAndWait();
        }
    }
}
