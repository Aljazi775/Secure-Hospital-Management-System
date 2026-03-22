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
}
