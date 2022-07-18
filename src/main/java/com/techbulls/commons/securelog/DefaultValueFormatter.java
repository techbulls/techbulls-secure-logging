package com.techbulls.commons.securelog;

public class DefaultValueFormatter implements ValueFormatter {
    @Override
    public String format(Object value, String secureValue) {
        return secureValue;
    }
}
