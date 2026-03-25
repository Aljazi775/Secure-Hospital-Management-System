import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthenticationService {
    public static User authenticate(String username, String suppliedPassword) {
        Connection con = DBUtils.establishConnection();
        String query = "SELECT * FROM users WHERE username = ?";
        User loggedInUser = null;

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                boolean isLocked = rs.getBoolean("is_locked");
                int failedAttempts = rs.getInt("failed_attempts");

                // Check if account is locked first
                if (isLocked) {
                    User lockedUser = new User(id, username, "", "");
                    lockedUser.setLocked(true);
                    DBUtils.closeConnection(con, statement);
                    return lockedUser;
                }

                String storedPassword = rs.getString("password_hash");
                boolean correctPassword = BCrypt.checkpw(suppliedPassword, storedPassword);
                
                if (correctPassword) {
                    loggedInUser = new User(
                        id,
                        rs.getString("username"),
                        storedPassword,
                        rs.getString("role")
                    );
                    loggedInUser.setLocked(false);
                    loggedInUser.setFailedAttempts(0);

                    // Reset attempts to 0 on success
                    PreparedStatement uStmt = con.prepareStatement("UPDATE users SET failed_attempts = 0 WHERE id = ?");
                    uStmt.setInt(1, id);
                    uStmt.executeUpdate();
                    uStmt.close();
                } else {
                    // Password wrong, increment attempts
                    failedAttempts++;
                    PreparedStatement uStmt = con.prepareStatement("UPDATE users SET failed_attempts = ? WHERE id = ?");
                    uStmt.setInt(1, failedAttempts);
                    uStmt.setInt(2, id);
                    uStmt.executeUpdate();
                    uStmt.close();

                    if (failedAttempts >= 5) {
                        PreparedStatement lockStmt = con.prepareStatement("UPDATE users SET is_locked = 1 WHERE id = ?");
                        lockStmt.setInt(1, id);
                        lockStmt.executeUpdate();
                        lockStmt.close();
                    }
                }
            }
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return loggedInUser;
    }
}
