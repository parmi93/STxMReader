package it.sitec.stxmreader;

import java.util.List;

import STCM.DEVICE;
import STCM.OnRead;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SMTP extends Activity implements OnRead{
	
	String id_activity, id_terminale;	//questo è un id che mi viene passato dal main_activity
	SharedPreferences preferences;		//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	static OnRead[] read = new OnRead[1];
	
	String tempRisposta, tempID, tempRispostaTemporanea;
	int tempRitorno, tempTipoComando;
	boolean tempComandiInCoda;
	int timeout;
	int ritorno;
	
	String comandoMAILSUBJ, comandoSMTPCFG, ComandoMAILTO, comandoMAILCC;
	
	EditText etServerPostaUscita, etUtente, etPassword, etNPorta, etOggetto, etDestinatari, etCC;
	RadioGroup rgAutenticazione;
	ProgressBar pbCaricamento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smtp);
		read[0] = this;
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		id_terminale = getIntent().getStringExtra("idTerminale");	
		
		timeout = getIntent().getIntExtra("timeout", 900);		//salvo il timeout che mi è stato passato dal main_activity
		
		ComandoMAILTO = "MAILTO";
		comandoMAILSUBJ = "MAILSUBJ";
		comandoSMTPCFG = "SMTPCFG";
		comandoMAILCC = "MAILCC";
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();
		
		OttieniPuntatoriView();
	}
	
	private void OttieniPuntatoriView()													//ottengo i puntatori ai vari View dell'intefaccia grafica
	{
		etServerPostaUscita = ((EditText)findViewById(R.id.SMTP_et_ServerSMTPUscita));
		etUtente = ((EditText)findViewById(R.id.SMTP_et_Utente));
		etPassword = ((EditText)findViewById(R.id.SMTP_et_Password));
		etNPorta = ((EditText)findViewById(R.id.SMTP_et_NPorta));
		etOggetto = ((EditText)findViewById(R.id.SMTP_et_Oggetto));
		etDestinatari = ((EditText)findViewById(R.id.SMTP_et_Destinatari));
		etCC = ((EditText)findViewById(R.id.SMTP_et_CC));
		rgAutenticazione = ((RadioGroup)findViewById(R.id.SMTP_rg_TipoAutenticazione));
		pbCaricamento = ((ProgressBar)findViewById(R.id.SMTP_pb));
	}
	
	private void CaricaDati()	
	{
		etServerPostaUscita.setText(preferences.getString("etServerPostaUscita", ""));				//caico i dati salvati
		etUtente.setText(preferences.getString("etUtente", ""));
		etPassword.setText(preferences.getString("etPassword", ""));
		etNPorta.setText(preferences.getString("etNPorta", ""));
		etOggetto.setText(preferences.getString("etOggetto", ""));
		etDestinatari.setText(preferences.getString("etDestinatari", ""));
		etCC.setText(preferences.getString("etCC", ""));
		for(int i = 0; i < rgAutenticazione.getChildCount(); i++)
			if(rgAutenticazione.getChildAt(i).getTag().toString().compareTo(preferences.getString("rgAutenticazione", "-1")) == 0)
			{
				((RadioButton)rgAutenticazione.getChildAt(i)).setChecked(true);
				break;
			}
		pbCaricamento.setVisibility(preferences.getInt("pbCaricamento", View.GONE));
	}
	
	private void SalvaDati()
	{
		editor.putString("etServerPostaUscita", etServerPostaUscita.getText().toString());			//salvo i dati
		editor.putString("etUtente", etUtente.getText().toString());
		editor.putString("etPassword", etPassword.getText().toString());
		editor.putString("etNPorta", etNPorta.getText().toString());
		editor.putString("etOggetto", etOggetto.getText().toString());
		editor.putString("etDestinatari", etDestinatari.getText().toString());
		editor.putString("etCC", etCC.getText().toString());
		editor.putString("rgAutenticazione", findViewById(rgAutenticazione.getCheckedRadioButtonId()).getTag().toString());
		editor.putInt("pbCaricamento", pbCaricamento.getVisibility());
		editor.commit();		//scrivo i dati su file
	}
	
	public void OnClickAggiornaConfSMTP(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoSMTPCFG).getBytes(), timeout, read, comandoSMTPCFG, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)																	//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickAggiornaOggetto(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoMAILSUBJ).getBytes(), timeout, read, comandoMAILSUBJ, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)																	//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickAggiornaDestinatari(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(ComandoMAILTO).getBytes(), timeout, read, ComandoMAILTO, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)																	//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickAggiornaMailCC(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoMAILCC).getBytes(), timeout, read, comandoMAILCC, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)																	//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickAggiornaTutto(View v)
	{
		OnClickAggiornaConfSMTP(new View(this));										//passo come parametro un oggetto creato a caso, in questo modo evito di accodare questo comando
		
		if(ritorno == 1)												//se il comando che ho appena mandato è stato eseguito, allora accodo gli altri comandi
		{
			editor.putInt("pbCaricamento", View.VISIBLE);				//rendo visibile la progressbar circolare 
			OnClickAggiornaOggetto(null);
			OnClickAggiornaDestinatari(null);
			OnClickAggiornaMailCC(null);
		}
	}
	
	public void OnClickSalvaConfSMTP(View v)
	{
		String dati = "\"" + etServerPostaUscita.getText() + "\",\"" + etUtente.getText() + "\",\"" + etPassword.getText() + "\",";
		dati += findViewById(rgAutenticazione.getCheckedRadioButtonId()).getTag().toString() + "," + etNPorta.getText();
		
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoSMTPCFG, dati);
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoSMTPCFG, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)														//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);	//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickImpostaValoriPredefiniti(View v)
	{
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoSMTPCFG, "\"\"");
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoSMTPCFG, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)														//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);	//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaOggetto(View v)
	{
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoMAILSUBJ, "\"" + etOggetto.getText() + "\"");
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoMAILSUBJ, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)														//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);	//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaDestinatari(View v)
	{
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(ComandoMAILTO, etDestinatari.getText().toString());
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, ComandoMAILTO, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)														//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);	//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaMailCC(View v)
	{
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoMAILCC, etCC.getText().toString());
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoMAILCC, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)														//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);	//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaTutto(View v)
	{
		OnClickSalvaConfSMTP(new View(this));							//passo come parametro un oggetto creato a caso, in questo modo evito di accodare questo comando
		
		if(ritorno == 1)												//se il comando che ho appena mandato è stato eseguito, allora accodo gli altri comandi
		{
			editor.putInt("pbCaricamento", View.VISIBLE);				//rendo visibile la progressbar circolare 
			OnClickSalvaOggetto(null);
			OnClickSalvaDestinatari(null);
			OnClickSalvaMailCC(null);
		}
	}

	public void Completo(String RispostaCompleta, String id, int TipoComando, boolean comandiInCoda)	//entro qui dentro quando è scaduto il TIMEOUT, quindi quando è terminata la lettura
	{
		//"RispostaCompleta" contiene tutta la risposta che mi è arrivata dal device
		//"id" contiene la stessa striga che gli ho passato io, attraverso il metodo DEVICE.Write(buff, timeout, read, TipoComando, <qui è dove gli ho passato l'id>);
		//"TipoComando" mi serve per capire se il comando spedito è di tipo LETTURA, SCRIVI(imposta), o un COMENDO(ES:MBLISTA)
		
		tempRisposta = RispostaCompleta;//faccio questo perchè all'interno di "runOnUiThread" sono visibili solo i parametri dichiarati a livello di classe
		tempID = id;					
		tempComandiInCoda = comandiInCoda;
		tempTipoComando = TipoComando;
		
		runOnUiThread(new Runnable()	//devo avviare un nuovo thread altrimenti non posso modificare la grafica di android
		{
			@Override
			public void run()
		    {
				try
		    	{
					SalvaDati();
					if(tempTipoComando == TIPO_COMANDO.LEGGI)							//se il comando è un comando di lettura
						if(MetodiComuni.TrovaErrori(tempRisposta) == false)				//se non ci sono stati errori
						{
							if(tempID == comandoSMTPCFG)								//vado a capire quale comando è stato dato
							{															//vado a scrivere i dati su file
								List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, true, true);
								int i = 0;
								editor.putString("etServerPostaUscita", dati.get(0)[i++].replace("\"", ""));
								editor.putString("etUtente", dati.get(0)[i++].replace("\"", ""));
								editor.putString("etPassword", dati.get(0)[i++].replace("\"", ""));
								editor.putString("rgAutenticazione", dati.get(0)[i++].replace("\"", ""));
								editor.putString("etNPorta", dati.get(0)[i++].replace("\"", ""));
							}
							
							if(tempID == comandoMAILSUBJ)
							{
								List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, true, false);
								editor.putString("etOggetto", dati.get(0)[0]);
							}
							
							if(tempID == ComandoMAILTO)
							{
								List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, true, false);
								editor.putString("etDestinatari", dati.get(0)[0]);
							}
							
							if(tempID == comandoMAILCC)
							{
								List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, true, false);
								editor.putString("etCC", dati.get(0)[0]);
							}
							
							if(tempComandiInCoda == false)								//se non ci sono comandi in coda
							{
								MetodiComuni.CreaToatOperazioneEseguita(getApplicationContext());//genero un popout
								editor.putInt("pbCaricamento", View.GONE);				//rendo invisibile la progressbar circolare
							}
						}
						else
						{
							editor.putInt("pbCaricamento", View.GONE);	//rendo invisibile la progressbar circolare
							MetodiComuni.CreaToastErrore(getApplicationContext());
						}
					
					if(tempTipoComando == TIPO_COMANDO.SCRIVI)
					{						
						if(tempID == comandoSMTPCFG)								//vado a capire quale comando è stato dato
						{															//vado a scrivere i dati su file
							List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, false, true);
							int i = 0;
							if(MetodiComuni.TrovaErrori(tempRisposta) == false)
							{
								editor.putString("etServerPostaUscita", dati.get(1)[i++].replace("\"", ""));
								editor.putString("etUtente", dati.get(1)[i++].replace("\"", ""));
								editor.putString("etPassword", dati.get(1)[i++].replace("\"", ""));
								editor.putString("rgAutenticazione", dati.get(1)[i++].replace("\"", ""));
								editor.putString("etNPorta", dati.get(1)[i++].replace("\"", ""));
								
								MetodiComuni.CreaToastOperazioneEseguitaPersonalizzata(getApplicationContext(), dati.get(0)[0]);
							}
							else
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
						}
							
						if(tempID == comandoMAILSUBJ)
						{
							List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, false, false);
							if(MetodiComuni.TrovaErrori(tempRisposta) == false)
							{
								editor.putString("etOggetto", dati.get(1)[0]);
								MetodiComuni.CreaToastOperazioneEseguitaPersonalizzata(getApplicationContext(), dati.get(0)[0]);
							}
							else
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
						}
						
						if(tempID == ComandoMAILTO)
						{
							List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, false, false);
							if(MetodiComuni.TrovaErrori(tempRisposta) == false)
							{
								if(dati.get(1)[0].toLowerCase().compareTo("ok") == 0)	//nel caso siano state cancellate le email
									editor.putString("etDestinatari", "");
								else
									editor.putString("etDestinatari", dati.get(1)[0]);
								
								MetodiComuni.CreaToastOperazioneEseguitaPersonalizzata(getApplicationContext(), dati.get(0)[0]);
							}
							else
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
						}
						
						if(tempID == comandoMAILCC)
						{
							List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, false, false);
							if(MetodiComuni.TrovaErrori(tempRisposta) == false)
							{
								if(dati.get(1)[0].toLowerCase().compareTo("ok") == 0)	//nel caso siano state cancellate le email
									editor.putString("etCC", "");
								else
									editor.putString("etCC", dati.get(1)[0]);
								
								MetodiComuni.CreaToastOperazioneEseguitaPersonalizzata(getApplicationContext(), dati.get(0)[0]);
							}
							else
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
						}
						
						if(tempComandiInCoda == false)					//se non ci sono comandi in coda
							editor.putInt("pbCaricamento", View.GONE);	//rendo invisibile la progressbar circolare
					}
		    	}
				catch(Exception e)		//se i dati che mi sono arrivati sono incompleti
				{
		    		MetodiComuni.CreaToastConnessioneInterrotta(getApplicationContext());	//genero un popout
		    		editor.putInt("pbCaricamento", View.GONE);								//rendo invisibile la progressbar circolare
		    		DEVICE.SvuotaCoda();													//svuoto la coda dei comandi da completare
		    	}
		    	finally
		    	{
		    		editor.commit();	//rendo effettive tutte le modifiche fatte fino ad ora (scrivendole su file)
		    		CaricaDati();		//carico ("sulla grafica") i dati appena salvati
		    	}
		    }
		});
	}

	@Override
	public void Temporaneo(String RispostaTemporanea, String id)					//entro qui ogni volta viene letto qualcosa dal device
	{
		tempRispostaTemporanea = RispostaTemporanea;
		runOnUiThread(new Runnable()	//devo avviare un nuovo thread altrimenti non posso modificare la grafica di android
		{
			@Override
			public void run()
		    {
				MetodiComuni.AggiornaTerminale(tempRispostaTemporanea, getSharedPreferences(id_terminale, 0));	//oggiorno l'activity "Terminale" salvandoci tutto ciò che leggo
		    }
		});
	}
	
	@Override
	public void ErroreWriteComandoInCoda(int ritorno)
	{
		tempRitorno = ritorno;
		runOnUiThread(new Runnable()	//devo avviare un nuovo thread altrimenti non posso modificare la grafica di android
		{
			@Override
			public void run()
		    {
				MetodiComuni.CreaToastConnessioneInterrotta(getApplicationContext());
				editor.putInt("pbCaricamento", View.GONE);
				editor.commit();
				CaricaDati();
		    }
			
		});
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
