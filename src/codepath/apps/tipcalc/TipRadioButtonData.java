package codepath.apps.tipcalc;

import android.graphics.Typeface;
import android.widget.RadioButton;

/** Tip Radio Button (is NOT a RadioButton View class) - class to easily obtain tip value from radio button */
public class TipRadioButtonData {
	/** radio button text defined when button specifies a custom tip */
	static final String CUSTOM_BUTTON_TEXT = "Custom";

	/** tip value */
	double tip;
	/** radio button linked to this class */
	RadioButton radioButton;
	/** the initial of the radio button before string substitutaion */
	String initText;
	/** if true, then the radio button is the custom tip radio button */
	Boolean isCustom = false;

	/** constructor */
	public TipRadioButtonData(RadioButton rb) {
		radioButton = rb;
		String percentString = radioButton.getText().toString().split("%")[0];
		if (!percentString.equals(CUSTOM_BUTTON_TEXT)) {
			tip = Double.parseDouble(percentString);
			initText = radioButton.getText().toString();
		} else {
			isCustom = true;
		}
	}

	/** @return the decimal value for the tip (eg. 20% will return .2) associated with this button */
	public double getTipValueDecimal() {
		return tip * .01;
	}

	/** @return the calculated tip amount based on the pre-tip amount */
	public double calculateTip(double preTip) {
		double calculatedTip = 0;
		if (!isCustom) {
			calculatedTip = preTip * getTipValueDecimal();
		}
		return calculatedTip;
	}

	/** format the radio button text based on a string substitution */
	public void formatText(String sub) {
		if (!isCustom) {
			String formatted = String.format(initText, sub);
			radioButton.setText(formatted);
		}
	}

	/** set the typeface for the radio button text */
	public void setTypeface(Typeface face) {
		radioButton.setTypeface(face);
	}
}