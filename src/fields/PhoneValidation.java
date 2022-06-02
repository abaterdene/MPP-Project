package fields;

import javafx.scene.control.TextField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidation implements Validation {
    @Override
    public void validate(String text) {
        try {
//            Integer.parseInt(text);
//            if (text.length() != 10) {
//                throw new InvalidFieldException("Phone number is in wrong format.");
//            }

            Pattern pattern = Pattern.compile("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$");
            Matcher matcher = pattern.matcher(text);
            if(!matcher.find())
                throw new InvalidFieldException("Phone number is in wrong format.");
        } catch (NumberFormatException e) {
            throw new InvalidFieldException("Phone number is in wrong format.");
        }
    }
}
