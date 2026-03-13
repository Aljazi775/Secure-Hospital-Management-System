import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthenticationService {
    public static User authenticate(String username, String suppliedPassword) {
        Connection con = DBUtils.establishConnection();
        String query = "SELECT * FROM users WHERE username = ?;";
        User loggedInUser = null;

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password_hash");
                boolean correctPassword = BCrypt.checkpw(suppliedPassword, storedPassword);
                
                if (correctPassword) {
                    loggedInUser = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        storedPassword,
                        rs.getString("role")
                    );
                    loggedInUser.setLocked(rs.getBoolean("is_locked"));
                    loggedInUser.setFailedAttempts(rs.getInt("failed_attempts"));
                }
            }
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return loggedInUser;
    }
}
