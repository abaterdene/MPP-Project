package fields;

public class ZipValidation implements Validation {
    @Override
    public void validate(String text) {
        try {
            Integer.parseInt(text);
            if (text.length() != 5) {
                throw new InvalidFieldException("Zip code is in wrong format.");
            }
        } catch (NumberFormatException e) {
            throw new InvalidFieldException("Zip code is in wrong format.");
        }
    }
}
