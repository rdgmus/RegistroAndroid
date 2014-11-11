package it.keyorchestra.registrowebapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Iscrizione extends Activity {

	Button registraButton;
	TextView esitoRegistrazione;
	EditText nome, cognome, email, passwd, repeatPasswd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_user);
		
		registraButton = (Button) findViewById(R.id.registra_button);
		esitoRegistrazione = (TextView) findViewById(R.id.messageEsitoRegistrazione);
		nome = (EditText) findViewById(R.id.editTextNome);
		cognome = (EditText) findViewById(R.id.editTextCognome);
		email = (EditText) findViewById(R.id.editTextEmail);
		passwd = (EditText) findViewById(R.id.editTextPassword);
		repeatPasswd = (EditText) findViewById(R.id.editTextRepeatPassword);

		registraButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Boolean check = false;
				// Controlla se le due password sono uguali

				// Check formati

				if (check) {
					// Salva dati nel database con password criptata

					// Va alla pagina di login
					Intent loginUserActivity = new Intent(
							"android.intent.action.LOGIN");
					startActivity(loginUserActivity);
				}else{
					esitoRegistrazione.setText("Correggi i dati inseriti:");
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_user, menu);
		return true;
	}

}
