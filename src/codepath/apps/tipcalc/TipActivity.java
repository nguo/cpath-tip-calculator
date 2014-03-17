package codepath.apps.tipcalc;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import codepath.apps.util.DecimalDigitsInputFilter;

import java.text.DecimalFormat;

public class TipActivity extends Activity {
	/** text of the radio button that chooses custom percentage */
	static final String CUSTOM_PERCENTAGE = "Custom";

	/** seek bar for selecting custom percentage */
	SeekBar seekBar;
	/** text of the percentage that the seek bar is on */
	TextView tvSeekBar;
	/** text of the tip amount */
	TextView tvTipAmount;
	/** text of the total amount */
	TextView tvTotalAmount;
	/** text of the pre-tip amount (different from the edit field which is displayed elsewhere) */
	TextView tvPreTipAmount;
	/** edit text field for the pre-tip amount */
	EditText etPreTip;
	/** radio group with the percentage options */
	RadioGroup rgPercents;
	/** radio button for the custom button option */
	RadioButton rbCustom;
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
		tvPreTipAmount = (TextView) findViewById(R.id.tvPreTipAmountNum);
		tvTipAmount = (TextView) findViewById(R.id.tvTipAmountNum);
		tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmountNum);
		etPreTip = (EditText) findViewById(R.id.etPreTip);
		rgPercents = (RadioGroup) findViewById(R.id.rgPercents);
		rbCustom = (RadioButton) findViewById(R.id.rbCustom);
		llSeekBar = (LinearLayout) findViewById(R.id.llSeekBar);
		tvSeekBarAmount = (TextView) findViewById(R.id.tvSeekBarAmount);

		// show/hide seekbar elements
		toggleSeekBarElts(rgPercents.getCheckedRadioButtonId() == rbCustom.getId());

		// set up listeners
		setupSeekBarListener();
		setupPreTipTextField();
		setRadioGroupListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tip, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** set up listeners for when the seek bar moves */
	private void setupSeekBarListener() {
		seekBar.setOnSeekBarChangeListener(
			new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					// set text to match the progress
					tvSeekBar.setText(progress + "%");
					// show tip value when seeking has stopped
					calculateAndDisplayTip();
				}

				public void onStopTrackingTouch(SeekBar seekBar) {}
				public void onStartTrackingTouch(SeekBar seekBar) {}
			}
		);
	}

	/** set radio group listeners */
	private void setRadioGroupListener() {
		rgPercents.setOnCheckedChangeListener(
			new RadioGroup.OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					toggleSeekBarElts(checkedId == rbCustom.getId());
					calculateAndDisplayTip();
				}
			}
		);
	}

	/** set up listeners/filters for when the user changes the pre-tip amount */
	private void setupPreTipTextField() {
		etPreTip.addTextChangedListener(
			new TextWatcher() {
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					calculateAndDisplayTip();
				}

				public void afterTextChanged(Editable s) {}
				public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			}
		);
		etPreTip.setFilters(
			new InputFilter[] { new DecimalDigitsInputFilter(10, 2) }
		);
	}

	/** calculates the tip and displays it on screen */
	private void calculateAndDisplayTip() {
		String preTipString = etPreTip.getText().toString();
		if (preTipString.length() > 0) {
			double percent;
			String percentString = getParsedSelectedRBtnText();
			if (percentString.equals(CUSTOM_PERCENTAGE)) {
				percent = seekBar.getProgress() * .01;
			} else {
				percent = Double.parseDouble(percentString) * .01;
			}

			double preTipAmount = Double.parseDouble(preTipString);
			double tipValue = percent * preTipAmount;
			double totalValue = preTipAmount + tipValue;

			DecimalFormat df = new DecimalFormat("#.00");
			tvPreTipAmount.setText(df.format(preTipAmount));
			tvTipAmount.setText(df.format(tipValue));
			tvTotalAmount.setText(df.format(totalValue));
		}
	}

	/** @return the percent portion of the selected button's text */
	private String getParsedSelectedRBtnText() {
		int radioId = rgPercents.getCheckedRadioButtonId();
		RadioButton radioButton = (RadioButton) findViewById(radioId);
		return radioButton.getText().toString().split("%")[0];
	}

	private void toggleSeekBarElts(Boolean enable) {
		int visibility = enable ? View.VISIBLE : View.INVISIBLE;
		llSeekBar.setEnabled(enable);
		llSeekBar.setVisibility(visibility);
		tvSeekBarAmount.setVisibility(visibility);
	}
}
