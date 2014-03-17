package codepath.apps.tipcalc;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import codepath.apps.util.DecimalDigitsInputFilter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class TipActivity extends Activity {
	/** the path for the font used to show the final calculated values */
	static final String FINAL_VALUES_FONT_PATH = "fonts/merchant.ttf";

	/** seek bar for selecting custom percentage */
	SeekBar seekBar;
	/** text of the percentage that the seek bar is on */
	TextView tvSeekBar;
	/** text of the tip amount */
	TextView tvTipAmount;
	/** text of the total amount */
	TextView tvTotalAmount;
	/** edit text field for the pre-tip amount */
	EditText etPreTip;
	/** radio group with the percentage options */
	RadioGroup rgPercents;
	/** dictionary of radio button id to the associated TipRadioButtonData created for that rb */
	Map<Integer, TipRadioButtonData> rbIdToTipRbData;
	/** linear layout containing the seekbar and the text views on the two sides */
	LinearLayout llSeekBar;
	/** text view displaying the percent that the seekbar is at */
	TextView tvSeekBarAmount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip);

		// init view elements
		seekBar = (SeekBar) findViewById(R.id.sbPercent);
		tvSeekBar = (TextView) findViewById(R.id.tvSeekBarAmount);
		tvTipAmount = (TextView) findViewById(R.id.tvTipAmountNum);
		tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmountNum);
		etPreTip = (EditText) findViewById(R.id.etPreTip);
		rgPercents = (RadioGroup) findViewById(R.id.rgPercents);
		llSeekBar = (LinearLayout) findViewById(R.id.llSeekBar);
		tvSeekBarAmount = (TextView) findViewById(R.id.tvSeekBarAmount);
		rbIdToTipRbData = new HashMap<Integer, TipRadioButtonData>();
		rbIdToTipRbData.put(R.id.rbPercent0, new TipRadioButtonData((RadioButton) findViewById(R.id.rbPercent0)));
		rbIdToTipRbData.put(R.id.rbPercent1, new TipRadioButtonData((RadioButton) findViewById(R.id.rbPercent1)));
		rbIdToTipRbData.put(R.id.rbPercent2, new TipRadioButtonData((RadioButton) findViewById(R.id.rbPercent2)));
		rbIdToTipRbData.put(R.id.rbCustom, new TipRadioButtonData((RadioButton) findViewById(R.id.rbCustom)));

		// show/hide seekbar elements
		toggleSeekBarElts(rgPercents.getCheckedRadioButtonId() == R.id.rbCustom);

		// set up listeners
		setupSeekBarListener();
		setupPreTipEditField();
		setRadioGroupListener();

		// set text properties
		setupTextTypeFace();
		resetNumbers();
	}

	/** set up listeners for when the seek bar moves */
	private void setupSeekBarListener() {
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// set text to match the progress
				tvSeekBar.setText(progress + "%");
				// show tip value when seeking has stopped
				calculateAndDisplayTip();
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				hideKeyboard(seekBar);
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	/** set radio group listeners */
	private void setRadioGroupListener() {
		rgPercents.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				hideKeyboard(group);
				toggleSeekBarElts(checkedId == R.id.rbCustom);
				calculateAndDisplayTip();
			}
		});
	}

	/** set up listeners/filters for when the user changes the pre-tip amount */
	private void setupPreTipEditField() {
		etPreTip.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				calculateAndDisplayTip();
			}

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
		etPreTip.setFilters( new InputFilter[] { new DecimalDigitsInputFilter(10, 2) } );
		etPreTip.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				resetNumbers();
				return false;
			}
		});
	}

	/** calculates the tip and displays it on screen */
	private void calculateAndDisplayTip() {
		String preTipString = etPreTip.getText().toString();
		if (preTipString.length() > 0) {
			double tipDecimal;
			TipRadioButtonData selectedTipData = getSelectedTipData();
			if (selectedTipData.isCustom) {
				tipDecimal = seekBar.getProgress() * .01;
			} else {
				tipDecimal = selectedTipData.getTipValueDecimal();
			}

			double preTipAmount = Double.parseDouble(preTipString);
			double tipValue = tipDecimal * preTipAmount;
			double totalValue = preTipAmount + tipValue;

			DecimalFormat df = new DecimalFormat("#.00");
			tvTipAmount.setText(df.format(tipValue));
			tvTotalAmount.setText(df.format(totalValue));

			for (TipRadioButtonData data : rbIdToTipRbData.values()) {
				String dfString = df.format(data.calculateTip(preTipAmount));
				data.formatText(dfString);
			}
		}
	}

	/** reset all the numbers */
	private void resetNumbers() {
		Resources res = getResources();
		String zeroValue = res.getString(R.string.zero_value);
		tvTipAmount.setText(zeroValue);
		tvTotalAmount.setText(zeroValue);
		for (TipRadioButtonData data : rbIdToTipRbData.values()) {
			data.formatText(zeroValue);
		}
		etPreTip.setText("");
	}

	/** @return the percent portion of the selected button's text */
	private TipRadioButtonData getSelectedTipData() {
		int radioId = rgPercents.getCheckedRadioButtonId();
		return rbIdToTipRbData.get(radioId);
	}

	/** Toggles visible/enable properties of all elements associated with the seek bar */
	private void toggleSeekBarElts(Boolean enable) {
		int visibility = enable ? View.VISIBLE : View.INVISIBLE;
		llSeekBar.setEnabled(enable);
		llSeekBar.setVisibility(visibility);
		tvSeekBarAmount.setVisibility(visibility);
	}

	/** set custom typeface for all text */
	private void setupTextTypeFace() {
		Typeface face= Typeface.createFromAsset(getAssets(), FINAL_VALUES_FONT_PATH);

		tvTipAmount.setTypeface(face);
		tvTotalAmount.setTypeface(face);
		etPreTip.setTypeface(face);
		((TextView) findViewById(R.id.tvPreTipAmountDesc)).setTypeface(face);
		((TextView) findViewById(R.id.tvTipAmountDesc)).setTypeface(face);
		((TextView) findViewById(R.id.tvTotalAmountDesc)).setTypeface(face);
		for (TipRadioButtonData data : rbIdToTipRbData.values()) {
			data.setTypeface(face);
		}
	}

	/** hides the software keyboard */
	private void hideKeyboard(View v) {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
}
