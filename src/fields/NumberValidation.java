package fields;

public class NumberValidation implements Validation{
    @Override
    public void validate(String text) {
        try {
            Integer.parseInt(text);
        } catch (NumberFormatException e){
            throw new InvalidFieldException("field is not in number format.");
        }
    }
}
