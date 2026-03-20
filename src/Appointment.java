public class Appointment {

    private int id;
    private int patientId;
    private int doctorId;
    private String apptDatetime;
    private String status;

    public Appointment(int patientId, int doctorId, String apptDatetime, String status) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.apptDatetime = apptDatetime;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getApptDatetime() {
        return apptDatetime;
    }

    public void setApptDatetime(String apptDatetime) {
        this.apptDatetime = apptDatetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
