package fields;

import javafx.scene.control.TextField;

public class TValidation implements Validation {
    @Override
    public void validate(String text) {
        if (text.isEmpty()) {
            throw new InvalidFieldException("Fields are required");
        }
        if (text.length() < 2) {
            throw new InvalidFieldException("Minimum length of text is 2");
        }
    }
}
