import java.sql.*;
import java.util.ArrayList;

public class MedicalRecordDAO {

    public static boolean addRecord(int patientId, int doctorId, String diagnosis, String prescription) {
        Connection con = DBUtils.establishConnection();
        boolean success = false;
        try {
            // encrypt before storing
            String encDiagnosis = ClinicalService.encrypt(diagnosis);
            String encPrescription = ClinicalService.encrypt(prescription);

            String query = "INSERT INTO medical_records (patient_id, doctor_id, diagnosis_enc, prescription_enc) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, doctorId);
            pstmt.setString(3, encDiagnosis);
            pstmt.setString(4, encPrescription);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                success = true;
                // log the action for audit trail - note values are encrypted so we just log the IDs
                AuditDAO.logAction(0, "INSERT", "medical_records", "", "patient_id=" + patientId + " doctor_id=" + doctorId);
            }
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return success;
    }

    public static ArrayList<MedicalRecord> getRecordsForPatient(int patientId) {
        Connection con = DBUtils.establishConnection();
        ArrayList<MedicalRecord> list = new ArrayList<>();
        try {
            String q = "SELECT * FROM medical_records WHERE patient_id = ?";
            PreparedStatement pstmt = con.prepareStatement(q);
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // decrypt when reading back out
                String diagnosis = ClinicalService.decrypt(rs.getString("diagnosis_enc"));
                String prescription = ClinicalService.decrypt(rs.getString("prescription_enc"));

                MedicalRecord rec = new MedicalRecord(
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    diagnosis,
                    prescription
                );
                rec.setId(rs.getInt("id"));
                rec.setCreatedAt(rs.getString("created_at"));
                list.add(rec);
            }
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println("Error fetching records");
            System.out.println(ex.getMessage());
        }
        return list;
    }
    public static ArrayList<MedicalRecord> getAllRecords() {
        Connection con = DBUtils.establishConnection();
        ArrayList<MedicalRecord> list = new ArrayList<>();
        try {
            // JOIN with patients and users to get readable names
            String q = "SELECT mr.id, mr.patient_id, mr.doctor_id, mr.diagnosis_enc, mr.prescription_enc, mr.created_at, "
                     + "CONCAT(p.first_name, ' ', p.last_name) AS patient_name, u.username AS doctor_name "
                     + "FROM medical_records mr "
                     + "JOIN patients p ON mr.patient_id = p.id "
                     + "JOIN users u ON mr.doctor_id = u.id";
            PreparedStatement pstmt = con.prepareStatement(q);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // decrypt when reading back
                String diagnosis = ClinicalService.decrypt(rs.getString("diagnosis_enc"));
                String prescription = ClinicalService.decrypt(rs.getString("prescription_enc"));

                MedicalRecord rec = new MedicalRecord(
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    diagnosis,
                    prescription
                );
                rec.setId(rs.getInt("id"));
                rec.setCreatedAt(rs.getString("created_at"));
                rec.setPatientName(rs.getString("patient_name"));
                rec.setDoctorName(rs.getString("doctor_name"));
                list.add(rec);
            }
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println("Error fetching all records");
            System.out.println(ex.getMessage());
        }
        return list;
    }
}
