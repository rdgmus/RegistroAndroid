package it.keyorchestra.registrowebapp.interfaces;

import android.content.Context;

public interface GeneratePasswordInterface {

	public String GeneratePasswordFromPhp(Context context, String ip,
			String servSideScript, String length);

	public boolean EmailPasswordToUser(long id_utente, String email,
			String passwordToEncode, boolean isChangingPasswordForHimSelf);

	public void SetPasswordVisibility(boolean isVisible);

	public boolean SaveNewPassword(long id_utente, String email,
			String oldPassword, String passwordToEncode,
			boolean isChangingPasswordForHimSelf);

}
