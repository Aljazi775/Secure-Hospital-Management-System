import java.util.regex.Pattern;

public class InputValidator {

    public static boolean isValidUsername(String username) {
        // Alphanumeric, 3 to 20 chars
        String regex = "^[a-zA-Z0-9_]{3,20}$";
        return Pattern.matches(regex, username);
    }

    public static boolean isValidPhone(String phone) {
        // Qatari format: starts with +974 or 00974, followed by 8 digits
        String regex = "^(\\+974|00974)\\d{8}$";
        return Pattern.matches(regex, phone);
    }

    public static boolean isValidDate(String date) {
        // YYYY-MM-DD format
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";
        return Pattern.matches(regex, date);
    }
}
