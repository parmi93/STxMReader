package it.sitec.stxmreader;

import STCM.DEVICE;
import STCM.OnRead;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Comandi extends Activity implements OnRead {

	String id_activity, id_terminale;	//questo è un id che mi viene passato dal main_activity
	SharedPreferences preferences;		//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	static OnRead[] read = new OnRead[1];
	
	String tempRisposta, tempID, tempRispostaTemporanea;
	int tempRitorno, tempTipoComando;
	boolean tempComandiInCoda;
	int timeout;
	
	String comandoMBLISTA, comandoMBLEGGI, comandoMAIL, comandoTEST;
	
	ProgressBar pbCreaListaDispositivi, pbLeggiDispositiviEmail, pbTestPresenzaAllarmi;
	EditText etCreaListaDispositivi, etLeggiDispositiviEmail, etTestPresenzaAllarmi;
	Button btnAnnulla;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comandi);
		read[0] = this;
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		id_terminale = getIntent().getStringExtra("idTerminale");	
		
		timeout = getIntent().getIntExtra("timeout", 900);		//salvo il timeout che mi è stato passato dal main_activity
		
		comandoMBLISTA = "MBLISTA";
		comandoMBLEGGI = "MBLEGGI";
		comandoMAIL = "MAIL";
		comandoTEST = "MBTEST";
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();
		
		OttieniPuntatoriView();
	}
	
	private void OttieniPuntatoriView()												//ottengo i puntatori ai vari View dell'intefaccia grafica
	{
		btnAnnulla = ((Button)findViewById(R.id.CMD_btn_Annulla));
		
		pbCreaListaDispositivi = ((ProgressBar)findViewById(R.id.CMD_pb_CreaListaDispositivi));
		pbLeggiDispositiviEmail = ((ProgressBar)findViewById(R.id.CMD_pb_LeggiDispositiviInviaEmail));
		pbTestPresenzaAllarmi = ((ProgressBar)findViewById(R.id.CMD_pb_TestPresenzaAllarmi));
		
		etCreaListaDispositivi = ((EditText)findViewById(R.id.CMD_et_CreaListaDispositivi));
		etLeggiDispositiviEmail = ((EditText)findViewById(R.id.CMD_et_LeggiDispositiviInviaEmail));
		etTestPresenzaAllarmi = ((EditText)findViewById(R.id.CMD_et_TestPresenzaAllarmi));
	}
	
	private void CaricaDati()	
	{
		btnAnnulla.setVisibility(preferences.getInt("btnAnnulla", View.GONE));			//vado a caricare i dati salvati
		
		pbCreaListaDispositivi.setVisibility(preferences.getInt("pbCreaListaDispositivi", View.GONE));
		pbLeggiDispositiviEmail.setVisibility(preferences.getInt("pbLeggiDispositiviEmail", View.GONE));
		pbTestPresenzaAllarmi.setVisibility(preferences.getInt("pbTestPresenzaAllarmi", View.GONE));
		
		etCreaListaDispositivi.setText(preferences.getString("etCreaListaDispositivi", ""));
		etLeggiDispositiviEmail.setText(preferences.getString("etLeggiDispositiviEmail", ""));
		etTestPresenzaAllarmi.setText(preferences.getString("etTestPresenzaAllarmi", ""));
		
		etCreaListaDispositivi.setSelection(etCreaListaDispositivi.getText().length());
		etLeggiDispositiviEmail.setSelection(etLeggiDispositiviEmail.getText().length());
		etTestPresenzaAllarmi.setSelection(etTestPresenzaAllarmi.getText().length());
	}
	
	private void SalvaDati()
	{
		editor.putInt("btnAnnulla", btnAnnulla.getVisibility());				//salvo i dati sul file
		editor.putInt("pbCreaListaDispositivi", pbCreaListaDispositivi.getVisibility());
		editor.putInt("pbLeggiDispositiviEmail", pbLeggiDispositiviEmail.getVisibility());
		editor.putInt("pbTestPresenzaAllarmi", pbTestPresenzaAllarmi.getVisibility());
		
		editor.putString("etCreaListaDispositivi", etCreaListaDispositivi.getText().toString());
		editor.putString("etLeggiDispositiviEmail", etLeggiDispositiviEmail.getText().toString());
		editor.putString("etTestPresenzaAllarmi", etTestPresenzaAllarmi.getText().toString());
		editor.commit();	//scrivo i dati su file
	}
	
	public void OnClickCreaListaDispositivi(View v)
	{
		//timeout di 5 minuti
		int ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioComando(comandoMBLISTA).getBytes(), 300000, read, comandoMBLISTA, TIPO_COMANDO.COMANDO, false);
		MetodiComuni.CreaToastComando(this, ritorno, pbCreaListaDispositivi);		//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
		if(ritorno == 1)
		{
			editor.putInt("pbCreaListaDispositivi", View.VISIBLE);
			editor.putString("etCreaListaDispositivi", "");							//svuoto l'editText
			editor.commit();
			CaricaDati();
		}
	}
	
	public void OnClickInviaMail(View v)
	{
		//timeout di 5 minuti
		int ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioComando(comandoMBLEGGI).getBytes(), 300000, read, comandoMBLEGGI, TIPO_COMANDO.COMANDO, false);
		MetodiComuni.CreaToastComando(this, ritorno, pbCreaListaDispositivi);		//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
		if(ritorno == 1)
		{
			editor.putInt("pbLeggiDispositiviEmail", View.VISIBLE);
			editor.putString("etLeggiDispositiviEmail", "");						//svuoto l'editText
			editor.putInt("btnAnnulla", View.VISIBLE);
			editor.commit();
			CaricaDati();
		}
	}
	
	public void OnClickAnnullaMail(View v)
	{
		int ritorno = DEVICE.ForceWrite(MetodiComuni.CreaComandoAtProprietarioScrivi(comandoMAIL, "STOP").getBytes());	//FORZO L'INVIO DEL COMANDO, SE NON LO FORZO NON POTREI INVIARE IL COMANDO, IN QUANTO IL DEVICE SAREBBE GIà OCCUPATO AD ESEGUIRE IN ALTRO COMANDO
		MetodiComuni.CreaToastComando(this, ritorno, null);		//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickTestAllarmi(View v)
	{
		//timeout di 1 minuto
		int ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioComando(comandoTEST).getBytes(), 60000, read, comandoTEST, TIPO_COMANDO.COMANDO, false);
		MetodiComuni.CreaToastComando(this, ritorno, pbTestPresenzaAllarmi);		//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
		if(ritorno == 1)
		{
			editor.putInt("pbTestPresenzaAllarmi", View.VISIBLE);
			editor.putString("etTestPresenzaAllarmi", "");							//svuoto l'editText
			editor.commit();
			CaricaDati();
		}
	}
	
	@Override
	public void Completo(String RispostaCompleta, String id, int TipoComando, boolean nessunComandoInCoda)
	{
		//"RispostaCompleta" contiene tutta la risposta che mi è arrivata dal device
		//"id" contiene la stessa striga che gli ho passato io, attraverso il metodo DEVICE.Write(buff, timeout, read, TipoComando, <qui è dove gli ho passato l'id>);
		//"TipoComando" mi serve per capire se il comando spedito è di tipo LETTURA, SCRIVI(imposta), o un COMENDO(ES:MBLISTA)
		
		runOnUiThread(new Runnable()	//devo avviare un nuovo thread altrimenti non posso modificare la grafica di android
		{
			@Override
			public void run()
		    {
				editor.putInt("pbCreaListaDispositivi", View.GONE);		//rendo invisibili tutte le progress bar
				editor.putInt("pbLeggiDispositiviEmail", View.GONE);
				editor.putInt("pbTestPresenzaAllarmi", View.GONE);
				editor.commit();
				
				Toast toast = Toast.makeText(getApplicationContext(), "ERRORE: Connessione interrotta!!", Toast.LENGTH_LONG);
				((View)toast.getView()).setBackgroundColor(Color.argb(150, 255, 0, 0));
				TextView tv = (TextView)toast.getView().findViewById(android.R.id.message);
				tv.setTextColor(Color.BLACK);
				tv.setTextAppearance(getApplicationContext(), Typeface.BOLD);
				toast.show();											//visualizzo un popuot di errore
				
				CaricaDati();
		    }
		});
	}
	
	@Override
	public void Temporaneo(String RispostaTemporanea, String id)
	{
		tempRispostaTemporanea = RispostaTemporanea;
		runOnUiThread(new Runnable()	//devo avviare un nuovo thread altrimenti non posso modificare la grafica di android
		{
			@Override
			public void run()
		    {
				MetodiComuni.AggiornaTerminale(tempRispostaTemporanea, getSharedPreferences(id_terminale, 0));	//oggiorno l'activity "Terminale" salvandoci tutto ciò che leggo
				if(tempRispostaTemporanea.indexOf("+" + comandoMBLISTA) != -1)		//se nella risposta ricevuta vi è presente la sottostringa "+MBLISTA"
				{
					//salvo la risposta ricevuta
					editor.putString("etCreaListaDispositivi", preferences.getString("etCreaListaDispositivi", "") + tempRispostaTemporanea);
					
					//se trovo la sottostringa "lista dispositivi creata"
					if(tempRispostaTemporanea.toLowerCase().indexOf("lista dispositivi creata") != -1)	
					{
						DEVICE.StopRead();										//allora fermo la lettura
						editor.putInt("pbCreaListaDispositivi", View.GONE);		//rendo invisibile la progress bar
						MetodiComuni.CreaToatOperazioneEseguita(getApplicationContext());
					}
					else
						if(tempRispostaTemporanea.toLowerCase().indexOf("error") != -1)	//altrimenti se trovo la sottostringa error
						{
							DEVICE.StopRead();										//allora fermo la lettura
							editor.putInt("pbCreaListaDispositivi", View.GONE);		//rendo invisibile la progress bar
							MetodiComuni.CreaToastErrore(getApplicationContext());
						}
				}
				
				//se trovo la sottostringa "+MBLEGGI" oppure se trovo "+MAIL"
				if(tempRispostaTemporanea.indexOf("+" + comandoMBLEGGI) != -1 || tempRispostaTemporanea.indexOf("+" + comandoMAIL) != -1 )
				{
					//salvo la risposta ricevuta
					editor.putString("etLeggiDispositiviEmail", preferences.getString("etLeggiDispositiviEmail", "") + tempRispostaTemporanea);
					
					//se trovo la sottostringa "invio mail eseguito" o "nessun dispositivo nella lista"
					if(tempRispostaTemporanea.toLowerCase().indexOf("invio mail eseguito\r\n") != -1 || tempRispostaTemporanea.toLowerCase().indexOf("nessun dispositivo nella lista\r\n") != -1)
					{
						DEVICE.StopRead();											//allora fermo la lettura
						editor.putInt("btnAnnulla", View.GONE);						//rendo invisibile il bottone annulla
						editor.putInt("pbLeggiDispositiviEmail", View.GONE);		//rendo invisibile la progress bar
						MetodiComuni.CreaToatOperazioneEseguita(getApplicationContext());
					}
					
					if(tempRispostaTemporanea.toLowerCase().indexOf("r10-10") != -1)//se trovo la sotto stringa "r10-10"(ovvero ha effettuato 10 tentativi per inviare l'emal)
					{
						DEVICE.StopRead();											//allora fermo la lettura
						editor.putInt("btnAnnulla", View.GONE);						//rendo invisibile il bottone annulla
						editor.putInt("pbLeggiDispositiviEmail", View.GONE);		//rendo invisibile la progress bar
						MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), "Impossibile inviate l'email!!");
					}
				}
				
				//se trovo la sottostringa "+MBLEGGI" oppure se trovo "+MAIL"
				if(tempRispostaTemporanea.indexOf("+" + comandoTEST) != -1)
				{
					//salvo la risposta ricevuta
					editor.putString("etTestPresenzaAllarmi", preferences.getString("etTestPresenzaAllarmi", "") + tempRispostaTemporanea);
					//se trovo la sottostringa "errore lettura" o "codici errore" o "stato" o" m-bus no allarmi presenti" o "nessun dispositivo nella lista"
					if(tempRispostaTemporanea.toLowerCase().indexOf("errore lettura\r\n") != -1 || tempRispostaTemporanea.toLowerCase().indexOf("codici errore\r\n") != -1 || tempRispostaTemporanea.toLowerCase().indexOf("stato\r\n") != -1 || tempRispostaTemporanea.toLowerCase().indexOf("m-bus no allarmi presenti") != -1 || tempRispostaTemporanea.toLowerCase().indexOf("nessun dispositivo nella lista\r\n") != -1 || tempRispostaTemporanea.toLowerCase().indexOf("nessun dispositivo nella lista\r\n") != -1)
					{
						DEVICE.StopRead();											//allora fermo la lettura
						editor.putInt("pbTestPresenzaAllarmi", View.GONE);			//rendo invisibile la progress bar
						MetodiComuni.CreaToatOperazioneEseguita(getApplicationContext());
					}
				}
				
				editor.commit();											//salvo le modifiche
				CaricaDati();												//visualizzo le modifiche
		    }
		});
	}

	@Override
	public void ErroreWriteComandoInCoda(int ritorno)
	{

	}
	
	@Override
	protected void onPause ()
	{
		super.onPause();
		SalvaDati();
	}

	@Override
	protected void onResume() {														//entro qui dentro ogni volta che l'utente entra in questa activity
		super.onResume();
		CaricaDati();
	}
}
