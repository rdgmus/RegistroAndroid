package it.keyorchestra.registrowebapp.interfaces;

import android.content.Context;

public interface GeneratePasswordInterface {
	
	public String GeneratePasswordFromPhp(Context context, String ip,
			String servSideScript, String length);

	
	public void EmailToUser(long id_utente, String newPassword, String msg);
	
	public void SetPasswordVisibility(boolean isVisible);

	boolean SaveNewPassword(long id_utente, String email, String oldPassword,
			String passwordToEncode);

}
