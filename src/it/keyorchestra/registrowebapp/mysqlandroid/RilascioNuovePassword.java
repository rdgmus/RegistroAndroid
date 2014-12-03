package it.keyorchestra.registrowebapp.mysqlandroid;

import it.keyorchestra.registrowebapp.R;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RilascioNuovePassword extends Activity implements
		ActivitiesCommonFunctions {

	LinearLayout llGeneratorePassword, llRichieste, llCommands;
	SeekBar sbPasswdLen;
	TextView tvPasswdLen;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rilascio_nuove_password);

		llGeneratorePassword = (LinearLayout) findViewById(R.id.llGeneratorePassword);
		llCommands = (LinearLayout) llGeneratorePassword
				.findViewById(R.id.llCommands);

		tvPasswdLen = (TextView) llCommands.findViewById(R.id.tvPasswdLen);

		sbPasswdLen = (SeekBar) llGeneratorePassword
				.findViewById(R.id.sbPasswdLen);
		sbPasswdLen.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
//				Toast.makeText(RilascioNuovePassword.this,
//						"[" + (progressChanged + 4) + "]", Toast.LENGTH_SHORT)
//						.show();
				tvPasswdLen.setText("[" + (progressChanged + 4) + "]");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				progressChanged = progress;
			}
		});
		tvPasswdLen.setText("[" + (sbPasswdLen.getProgress() + 4) + "]");
	}

	@Override
	public void registerToolTipFor(ImageButton ib) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean customToast(CharSequence charSequence, int iconId,
			int layoutId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDefaultDatabaseFromPreferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatabaseIpFromPreferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startAnimation(View ib, long durationInMilliseconds) {
		// TODO Auto-generated method stub

	}

}
