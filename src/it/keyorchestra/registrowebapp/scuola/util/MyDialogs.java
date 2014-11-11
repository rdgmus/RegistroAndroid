package it.keyorchestra.registrowebapp.scuola.util;

import it.keyorchestra.registrowebapp.interfaces.AlertMagnatic;
import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;

public class MyDialogs {

	private Context alertDialogContext;

	public static void getConfirmDialog(Context mContext, String title,
			String msg, String positiveBtnCaption, String negativeBtnCaption,
			boolean isCancelable, int imageResource, final AlertMagnatic target) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		// int imageResource = android.R.drawable.ic_dialog_alert;

		Drawable image = mContext.getResources().getDrawable(imageResource);

		builder.setTitle(title)
				.setMessage(msg)
				.setIcon(image)
				.setCancelable(false)
				.setPositiveButton(positiveBtnCaption,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								target.PositiveMethod(dialog, id);
							}
						})
				.setNegativeButton(negativeBtnCaption,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								target.NegativeMethod(dialog, id);
							}
						});

		AlertDialog alert = builder.create();
		alert.setCancelable(isCancelable);
		alert.show();
		if (isCancelable) {
			alert.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					target.NegativeMethod(null, 0);
				}
			});
		}
	}

	public Context getAlertDialogContext() {
		return alertDialogContext;
	}

	public void setAlertDialogContext(Context alertDialogContext) {
		this.alertDialogContext = alertDialogContext;
	}
}
