package codepath.apps.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Input Filter for text field elements to limit the number of digits entered. */
public class DecimalDigitsInputFilter implements InputFilter {
	Pattern pattern;

	/**
	 * Constructor. Creates a pattern based on number of digits allowed.
	 * @param digitsBeforeZero	number of digits before zero to allow
	 * @param digitsAfterZero	number of digits after zero to allow
	 */
	public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
		pattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		Matcher matcher = pattern.matcher(dest);
		if(!matcher.matches()) {
			return "";
		} else {
			return null;
		}
	}

}