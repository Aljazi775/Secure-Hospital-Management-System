import java.sql.*;
import java.util.ArrayList;

public class AppointmentDAO {

    public static boolean addAppointment(Appointment a) {
        Connection con = DBUtils.establishConnection();
        boolean success = false;
        try {
            String query = "INSERT INTO appointments (patient_id, doctor_id, appt_datetime, status) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, a.getPatientId());
            pstmt.setInt(2, a.getDoctorId());
            pstmt.setString(3, a.getApptDatetime());
            pstmt.setString(4, a.getStatus());

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

    public static ArrayList<Appointment> getAllAppointments() {
        Connection con = DBUtils.establishConnection();
        ArrayList<Appointment> list = new ArrayList<>();
        try {
            String q = "SELECT * FROM appointments";
            PreparedStatement pstmt = con.prepareStatement(q);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Appointment temp = new Appointment(
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getString("appt_datetime"),
                    rs.getString("status")
                );
                temp.setId(rs.getInt("id"));
                list.add(temp);
            }
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println("Error fetching appointments");
            System.out.println(ex.getMessage());
        }
        return list;
    }
}
