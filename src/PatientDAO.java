import java.sql.*;
import java.util.ArrayList;

public class PatientDAO {

    public static boolean addPatient(Patient p) {
        Connection con = DBUtils.establishConnection();
        boolean success = false;
        try {
            String query = "INSERT INTO patients (first_name, last_name, dob, gender, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, p.getFirstName());
            pstmt.setString(2, p.getLastName());
            pstmt.setString(3, p.getDob());
            pstmt.setString(4, p.getGender());
            pstmt.setString(5, p.getPhone());
            pstmt.setString(6, p.getAddress());
            
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

    public static ArrayList<Patient> getAllPatients() {
        Connection con = DBUtils.establishConnection();
        ArrayList<Patient> list = new ArrayList<>();
        try {
            String q = "SELECT * FROM patients";
            PreparedStatement pstmt = con.prepareStatement(q);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Patient temp = new Patient(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("dob"),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    rs.getString("address")
                );
                // set the id since constructor doesnt take it
                temp.setId(rs.getInt("id"));
                list.add(temp);
            }
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println("Error getting patients");
            System.out.println(ex.getMessage());
        }
        return list;
    }
}
