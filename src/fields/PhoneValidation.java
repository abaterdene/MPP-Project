package fields;

import javafx.scene.control.TextField;

public class PhoneValidation implements Validation {
    @Override
    public void validate(String text) {
        try {
            Integer.parseInt(text);
            if (text.length() != 10) {
                throw new InvalidFieldException("Phone number is in wrong format.");
            }
        } catch (NumberFormatException e) {
            throw new InvalidFieldException("Phone number is in wrong format.");
        }
    }
}
