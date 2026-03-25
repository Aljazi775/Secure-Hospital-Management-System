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

public class PatientView {

    private Stage stage;
    private String role;
    private TableView<Patient> table;
    private ObservableList<Patient> patientData;
    private TextField fNameInput;
    private TextField lNameInput;
    private TextField dobInput;
    private TextField genderInput;
    private TextField phoneInput;
    private TextField addressInput;

    public PatientView(Stage s, String role) {
        this.stage = s;
        this.role = role;
    }

    public void initializeComponents() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Patient Data Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Set up the table
        table = new TableView<>();

        TableColumn<Patient, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Patient, String> fnameCol = new TableColumn<>("First Name");
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Patient, String> lnameCol = new TableColumn<>("Last Name");
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Patient, String> dobCol = new TableColumn<>("DOB");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));

        TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        table.getColumns().addAll(idCol, fnameCol, lnameCol, dobCol, genderCol, phoneCol);

        loadData();

        // Navigation buttons
        HBox navBox = new HBox(10);
        navBox.setAlignment(Pos.CENTER);

        // Receptionist: can add patients and book appointments
        if (role.equals("Receptionist")) {
            HBox formBox = new HBox(10);
            formBox.setAlignment(Pos.CENTER);

            fNameInput = new TextField();
            fNameInput.setPromptText("First Name");

            lNameInput = new TextField();
            lNameInput.setPromptText("Last Name");

            dobInput = new TextField();
            dobInput.setPromptText("YYYY-MM-DD");

            genderInput = new TextField();
            genderInput.setPromptText("Gender");

            phoneInput = new TextField();
            phoneInput.setPromptText("+974XXXXXXXX");

            addressInput = new TextField();
            addressInput.setPromptText("Address");

            Button addButton = new Button("Add New Patient");
            addButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    addPatientButtonClicked();
                }
            });

            formBox.getChildren().addAll(fNameInput, lNameInput, dobInput, genderInput, phoneInput, addressInput, addButton);
            root.getChildren().addAll(title, table, formBox);

            Button appointmentButton = new Button("Book Appointment");
            appointmentButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    AppointmentView apptScreen = new AppointmentView(stage, role);
                    apptScreen.initializeComponents();
                }
            });
            navBox.getChildren().add(appointmentButton);
        }

        // Doctor: can view medical records and view appointments
        if (role.equals("Doctor")) {
            root.getChildren().addAll(title, table);

            Button medRecordButton = new Button("Medical Records");
            medRecordButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    MedicalRecordView medScreen = new MedicalRecordView(stage, role);
                    medScreen.initializeComponents();
                }
            });

            Button appointmentButton = new Button("View Appointments");
            appointmentButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    AppointmentView apptScreen = new AppointmentView(stage, role);
                    apptScreen.initializeComponents();
                }
            });

            navBox.getChildren().addAll(medRecordButton, appointmentButton);
        }

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserLogin loginScreen = new UserLogin(stage);
                loginScreen.initializeComponents();
            }
        });

        navBox.getChildren().add(logoutButton);
        root.getChildren().add(navBox);

        Scene scene = new Scene(root, 900, 620);
        stage.setScene(scene);
        stage.setTitle("HMS - Patients (" + role + ")");
        stage.show();
    }

    private void loadData() {
        ArrayList<Patient> list = PatientDAO.getAllPatients();
        patientData = FXCollections.observableArrayList(list);
        table.setItems(patientData);
    }

    private void addPatientButtonClicked() {
        String fname = fNameInput.getText();
        String lname = lNameInput.getText();
        String dob = dobInput.getText();
        String gender = genderInput.getText();
        String phone = phoneInput.getText();
        String address = addressInput.getText();

        if (fname.isEmpty() || lname.isEmpty() || dob.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Missing required fields");
            alert.showAndWait();
            return;
        }

        if (!InputValidator.isValidUsername(fname) || !InputValidator.isValidUsername(lname)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Names must be alphanumeric and 3-20 chars");
            alert.showAndWait();
            return;
        }

        if (!InputValidator.isValidDate(dob)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "DOB must be YYYY-MM-DD");
            alert.showAndWait();
            return;
        }

        if (!InputValidator.isValidPhone(phone)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Phone must be in Qatari format (+974 or 00974)");
            alert.showAndWait();
            return;
        }

        Patient newPatient = new Patient(fname, lname, dob, gender, phone, address);
        boolean success = PatientDAO.addPatient(newPatient);

        if (success) {
            loadData();
            fNameInput.clear();
            lNameInput.clear();
            dobInput.clear();
            genderInput.clear();
            phoneInput.clear();
            addressInput.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save patient in database");
            alert.showAndWait();
        }
    }
}
