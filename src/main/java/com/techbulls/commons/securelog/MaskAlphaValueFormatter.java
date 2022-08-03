package com.techbulls.commons.securelog;
/**
 * <h3>MaskAlphaValueFormatter Class</h3>
 * The Alpha value formatter that masks the alphabets [A-Za-z] with 'X' character
 * <p>
 * @see com.techbulls.commons.securelog.ValueFormatter
 * @author Vaibhav Ghatge
 * @version 1.0.0
 * @since  01 August 2022
 * */
public class MaskAlphaValueFormatter implements ValueFormatter {
    /**
     * This method will return the formatted maksed value
     * @param value This is the value that needs to be formatted
     * @param secureValue This is the formatted value
     * @return String This is the string value after formatting
     * */
    @Override
    public String format(Object value, String secureValue) {
        return value.toString().replaceAll("[A-Za-z]","X");
    }
}
