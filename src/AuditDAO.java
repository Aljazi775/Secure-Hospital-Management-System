import java.sql.*;
import java.util.ArrayList;

public class AuditDAO {

    public static void logAction(int userId, String action, String tableName, String oldValue, String newValue) {
        Connection con = DBUtils.establishConnection();
        try {
            String query = "INSERT INTO audit_logs (user_id, action, table_name, old_value, new_value) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            pstmt.setString(3, tableName);
            pstmt.setString(4, oldValue);
            pstmt.setString(5, newValue);
            pstmt.executeUpdate();
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println("Error writing audit log");
            System.out.println(ex.getMessage());
        }
    }

    public static ArrayList<AuditLog> getAllLogs() {
        Connection con = DBUtils.establishConnection();
        ArrayList<AuditLog> list = new ArrayList<>();
        try {
            String q = "SELECT * FROM audit_logs ORDER BY log_time DESC";
            PreparedStatement pstmt = con.prepareStatement(q);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AuditLog log = new AuditLog(
                    rs.getInt("user_id"),
                    rs.getString("action"),
                    rs.getString("table_name"),
                    rs.getString("old_value"),
                    rs.getString("new_value")
                );
                log.setId(rs.getInt("id"));
                log.setLogTime(rs.getString("log_time"));
                list.add(log);
            }
            DBUtils.closeConnection(con, pstmt);
        } catch (Exception ex) {
            System.out.println("Error fetching logs");
            System.out.println(ex.getMessage());
        }
        return list;
    }
}
