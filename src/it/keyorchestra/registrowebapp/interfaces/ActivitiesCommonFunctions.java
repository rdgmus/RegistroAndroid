package it.keyorchestra.registrowebapp.interfaces;

import android.widget.ImageButton;

public interface ActivitiesCommonFunctions {
	/**
	 * Registra il tooltip per l'ImageButton che usa 
	 * il parmetro android:contentDescription
	 * @param ib
	 */
	public void registerToolTipFor(ImageButton ib);
	
	public boolean customToast(CharSequence charSequence, int iconId, int layoutId);


}
