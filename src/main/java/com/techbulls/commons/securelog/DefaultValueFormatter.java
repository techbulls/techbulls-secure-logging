package com.techbulls.commons.securelog;

/**
 * <h3>DefaultValueFormatter Class</h3>
 * The default value formatter that masks the value with default mask value specified
 * <p>
 * @see com.techbulls.commons.securelog.ValueFormatter
 * @author Kalpak Gadre
 * @version 1.0.0
 * @since  01 August 2022
 * */

public class DefaultValueFormatter implements ValueFormatter {

    /**
     * This method will return the formatted value
     * @param value This is the value that needs to be formatted
     * @param secureValue This is the formatted value
     * @return String This is the string value after formatting
     * */
    @Override
    public String format(Object value, String secureValue) {
        return secureValue;
    }
}
