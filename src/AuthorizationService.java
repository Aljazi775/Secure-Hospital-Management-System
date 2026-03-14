public class AuthorizationService {
    public static boolean isAdmin(User currentUser) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole().equalsIgnoreCase("ADMIN");
    }

    public static boolean isDoctor(User currentUser) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole().equalsIgnoreCase("Doctor");
    }
    
    public static boolean isReceptionist(User currentUser) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole().equalsIgnoreCase("Receptionist");
    }
}
