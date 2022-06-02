package fields;

import java.util.HashMap;

final public class ValidationFactory {
    private ValidationFactory() {
    }

    static HashMap<String, Validation> map = new HashMap<>();

    static {
        map.put("TField", new TValidation());
        map.put("ZipField", new ZipValidation());
        map.put("PhoneField", new PhoneValidation());
    }

    public static Validation getValidation(String className) {
        if (!map.containsKey(className))
            throw new IllegalArgumentException("No validation for this component");
        return map.get(className);
    }
}