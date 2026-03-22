public class MedicalRecord {

    private int id;
    private int patientId;
    private int doctorId;
    private String diagnosisEnc;
    private String prescriptionEnc;
    private String createdAt;

    public MedicalRecord(int patientId, int doctorId, String diagnosisEnc, String prescriptionEnc) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosisEnc = diagnosisEnc;
        this.prescriptionEnc = prescriptionEnc;
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

    public String getDiagnosisEnc() {
        return diagnosisEnc;
    }

    public void setDiagnosisEnc(String diagnosisEnc) {
        this.diagnosisEnc = diagnosisEnc;
    }

    public String getPrescriptionEnc() {
        return prescriptionEnc;
    }

    public void setPrescriptionEnc(String prescriptionEnc) {
        this.prescriptionEnc = prescriptionEnc;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
