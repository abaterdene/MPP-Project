package fields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsbnValidation implements Validation{
    @Override
    public void validate(String text) {
        try {
            Pattern pattern = Pattern.compile("\\d{2}-\\d{5}");
            Matcher matcher = pattern.matcher(text);
            if(!matcher.find())
                throw new InvalidFieldException("ISBN is in wrong format.");
        } catch (Exception e){
            throw new InvalidFieldException("ISBN is in wrong format.");
        }
    }
}
