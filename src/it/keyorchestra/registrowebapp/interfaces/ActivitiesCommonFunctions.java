package it.keyorchestra.registrowebapp.interfaces;

import android.view.View;
import android.widget.ImageButton;

public interface ActivitiesCommonFunctions {
	
	public  enum NewPasswordRequestState {
	    REQUEST_ABORTED , REQUEST_SUCCESS, REQUEST_EXISTS, NONE
	};
	
	/**
	 * Registra il tooltip per l'ImageButton che usa il parametro
	 * android:contentDescription
	 * 
	 * @param ib
	 */
	public void registerToolTipFor(ImageButton ib);

	public boolean customToast(CharSequence charSequence, int iconId,
			int layoutId);

	public String getDefaultDatabaseFromPreferences();

	public String getDatabaseIpFromPreferences();

	public void startAnimation(final View ib,
			final long durationInMilliseconds);
}
