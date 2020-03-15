package it.sitec.stxmreader;

import STCM.DEVICE;
import STCM.OnRead;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Terminale extends Activity implements OnRead{
	
	String id_activity;					//questo è un id che mi viene passato dal main_activity
	static SharedPreferences preferences;//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	static OnRead[] read = new OnRead[1];
	int ritorno;
	
	String tempRispostaTemporanea;
	
	static EditText etMonitor, etComandi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminale);
		read[0] = this;
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();
		
		OttieniPuntatoriView();
		
		ritorno = DEVICE.WRITE(new byte[1], -1, read, "", TIPO_COMANDO.SCONOSCIUTO, false);	
		//in questo modo comincio a leggere tutto ciò che mi arriva dal device STCM, timeout -1, in questo modo rimane sempre in lettura, fino a quando non lo stoppo io
	}

	private void OttieniPuntatoriView()
	{
		etMonitor = (EditText)findViewById(R.id.TERM_et_Monitor);
		etComandi = (EditText)findViewById(R.id.TERM_et_Comandi);
	}
	
	static public void CaricaDati()										//carico i dati salvati su file
	{																	//questo metodo è statico perchè dovrà essere richiamato da tutte le activity
		if(preferences != null)											//in modo tale da poter visualizzare sul terminale tutti i comandi dati
		{
			etMonitor.setText(preferences.getString("monitor", ""));
			etComandi.setText(preferences.getString("etComandi", ""));
			
			etMonitor.setSelection(etMonitor.getText().length());		//sposto il cursore infondo al editText
			etComandi.setSelection(etComandi.getText().length());
		}
	}
	
	public void OnClickButtonInvia(View v)
	{
		String comando = etComandi.getText().toString() + "\r\n";
		int ritorno = DEVICE.WRITE(comando.getBytes(), -1, read, "", TIPO_COMANDO.SCONOSCIUTO, false);
		MetodiComuni.CreaToastComando(this, ritorno, null);
	}
	
	public void OnClickButtonPulisci(View v)
	{
		editor.putString("monitor", "");		//salvo i dati
		editor.putString("etComandi", etComandi.getText().toString());
		editor.commit();
		CaricaDati();
	}
	
	@Override
	protected void onPause()														//entro qui ogni volta che l'utente TORNA INDIETRO, GIRA IL MONITOR o quando va direttamente sulla HOME di android
	{
		super.onPause();
		
		editor.putString("monitor", etMonitor.getText().toString());		//salvo i dati
		editor.putString("etComandi", etComandi.getText().toString());
		editor.commit();						//scrivo i dati su file
	}
	
	@Override
	protected void onResume() {														//entro qui dentro ogni volta che l'utente entra in questa activity
		super.onResume();
		CaricaDati();
	}
	
	@Override
	public void Completo(String RispostaCompleta, String id, int TipoComando, boolean nessunComandoInCoda)
	{
	}
	
	@Override
	public void Temporaneo(String RispostaTemporanea, String id)
	{
		tempRispostaTemporanea = RispostaTemporanea;
		runOnUiThread(new Runnable()	//devo avviare un nuovo tread altrimenti non posso modificare la grafica di android
		{
			@Override
			public void run()
		    {
				editor.putString("monitor", preferences.getString("monitor", "") + tempRispostaTemporanea);		//salvo i dati
				editor.putString("etComandi", etComandi.getText().toString());
				editor.commit();						//scrivo i dati su file
				CaricaDati();
		    }
		});
	}

	@Override
	public void ErroreWriteComandoInCoda(int ritorno)
	{
	}

	@Override
	public void finish() {
		super.finish();
		
		if(ritorno == 1)				//se quando sono entrato in questa activity, e stato dato correttamente il comando write
			DEVICE.StopRead();			//allora fermo la lettura dal device
	}
	
	
}
