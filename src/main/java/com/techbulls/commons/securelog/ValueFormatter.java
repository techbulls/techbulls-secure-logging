package com.techbulls.commons.securelog;

/**
 * <h3>ValueFormatter Interface</h3>
 * The base interface for formatting values
 * <p>
 * <b>Implementing Classes:</b>
 * <p>
 * DefaultValueFormatter
 * MaskAlphaValueFormatter
 * <p>
 * @author Kalpak Gadre
 * @version 1.0.0
 * @since  01 August 2022
 *
 * DefaultValueFormatter
 * */
public interface ValueFormatter {
    /**This method will return the formatted value
     * @param value This is the value that needs to be formatted
     * @param secureValue This is the formatted value
     * @return String This is the string value after formatting
     * */
    String format(Object value, String secureValue);
}
