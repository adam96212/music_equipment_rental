package pl.booking_room.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public static boolean validPassword(String password, String repeatedPassword){
        return password.equals(repeatedPassword);
    }
}
