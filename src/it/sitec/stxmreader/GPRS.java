package it.sitec.stxmreader;

import java.util.List;

import STCM.DEVICE;
import STCM.OnRead;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GPRS extends Activity implements OnRead, OnCheckedChangeListener{

	String id_activity, id_terminale;	//questo è un id che mi viene passato dal main_activity
	SharedPreferences preferences;		//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	static OnRead[] read = new OnRead[1];
	
	String tempRisposta, tempID, tempRispostaTemporanea;
	int tempRitorno, tempTipoComando;
	boolean tempComandiInCoda;
	int timeout;
	
	String comandoGPRSCFG;
	
	RelativeLayout rlImpostazioni;
	EditText etAPN, etUtente, etPassword, etIPFisso;
	ToggleButton tbImpostaAutomaticamente, tbDataCompression, tbHeaderCompression;
	TextView tvNomeOperatore, tvNomeOperatoreDATO, tvIdOperatore, tvIdOperatoreDATO;
	ProgressBar pbCaricamento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gprs);
		read[0] = this;
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		id_terminale = getIntent().getStringExtra("idTerminale");	
		
		timeout = getIntent().getIntExtra("timeout", 900);		//salvo il timeout che mi è stato passato dal main_activity
		
		comandoGPRSCFG = "GPRSCFG";
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();
		
		OttieniPuntatoriView();
	}
	
	private void OttieniPuntatoriView()												//ottengo i puntatori ai vari View dell'intefaccia grafica
	{
		rlImpostazioni = ((RelativeLayout)findViewById(R.id.GPRS_RLayout_ImpostazioniCorrenti));
		etAPN = ((EditText)findViewById(R.id.GPRS_et_APN));
		etUtente = ((EditText)findViewById(R.id.GPRS_et_Utente));
		etPassword = ((EditText)findViewById(R.id.GPRS_et_Password));
		etIPFisso = ((EditText)findViewById(R.id.GPRS_et_IpFisso));
		tvNomeOperatore = ((TextView)findViewById(R.id.GPRS_tv_NomeOperatore));
		tvIdOperatore = ((TextView)findViewById(R.id.GPRS_tv_IdOperatore));
		tvNomeOperatoreDATO = ((TextView)findViewById(R.id.GPRS_tv_NomeOperatore_DATO));
		tvIdOperatoreDATO = ((TextView)findViewById(R.id.GPRS_tv_IdOperatore_DATO));
		tbImpostaAutomaticamente = ((ToggleButton)findViewById(R.id.GPRS_tb_ImpostaAutomaticamente));
		
		tbImpostaAutomaticamente.setOnCheckedChangeListener(this);
		
		tbDataCompression = ((ToggleButton)findViewById(R.id.GPRS_tb_DataCompression));
		tbHeaderCompression = ((ToggleButton)findViewById(R.id.GPRS_tb_HeaderCompression));
		pbCaricamento = ((ProgressBar)findViewById(R.id.GPRS_pb));
	}
	
	private void CaricaDati()	
	{
		etAPN.setText(preferences.getString("etAPN", ""));		//vado a caricare i dati salvati
		etUtente.setText(preferences.getString("etUtente", ""));
		etPassword.setText(preferences.getString("etPassword", ""));
		etIPFisso.setText(preferences.getString("etIPFisso", ""));
		tvNomeOperatoreDATO.setText(preferences.getString("tvNomeOperatoreDATO", tvNomeOperatoreDATO.getText().toString()));
		tvIdOperatoreDATO.setText(preferences.getString("tvIdOperatoreDATO", tvIdOperatoreDATO.getText().toString()));
		tbImpostaAutomaticamente.setChecked(preferences.getBoolean("tbImpostaAutomaticamente", false));
		tbDataCompression.setChecked(preferences.getBoolean("tbDataCompression", false));
		tbHeaderCompression.setChecked(preferences.getBoolean("tbHeaderCompression", false));
		pbCaricamento.setVisibility(preferences.getInt("pbCaricamento", View.GONE));
		
		onCheckedChanged(null, tbImpostaAutomaticamente.isChecked());
	}
	
	private void SalvaDati()
	{
		editor.putString("etAPN", etAPN.getText().toString());		//salvo i dati sul file
		editor.putString("etUtente", etUtente.getText().toString());
		editor.putString("etPassword", etPassword.getText().toString());
		editor.putString("etIPFisso", etIPFisso.getText().toString());
		editor.putString("tvNomeOperatoreDATO", tvNomeOperatoreDATO.getText().toString());
		editor.putString("tvIdOperatoreDATO", tvIdOperatoreDATO.getText().toString());
		editor.putBoolean("tbImpostaAutomaticamente", tbImpostaAutomaticamente.isChecked());
		editor.putBoolean("tbDataCompression", tbDataCompression.isChecked());
		editor.putBoolean("tbHeaderCompression", tbHeaderCompression.isChecked());
		editor.putInt("pbCaricamento", pbCaricamento.getVisibility());
		editor.commit();	//scrivo i dati su file
	}
	
	public void OnClickAggiornaTutto(View v)
	{
		int ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoGPRSCFG).getBytes(), timeout, read, comandoGPRSCFG, TIPO_COMANDO.LEGGI, false);
		MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaTutto(View v)
	{
		String dati;
		if(tbImpostaAutomaticamente.isChecked())
			dati = "1,\"\"";
		else
		{
			dati = "1,\"" + etAPN.getText() + "\",\"" + etUtente.getText() + "\",\"" + etPassword.getText() + "\",\"" + etIPFisso.getText() + "\",";
			dati += (tbDataCompression.isChecked() ? "1" : "0") + "," + (tbHeaderCompression.isChecked() ? "1" : "0");
		}
		
		int ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioScrivi(comandoGPRSCFG, dati).getBytes(), timeout, read, comandoGPRSCFG, TIPO_COMANDO.SCRIVI, false);
		MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);	//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		isChecked = !isChecked;
		
		etAPN.setEnabled(isChecked);
		etUtente.setEnabled(isChecked);
		etPassword.setEnabled(isChecked);
		etIPFisso.setEnabled(isChecked);
		tbDataCompression.setEnabled(isChecked);
		tbHeaderCompression.setEnabled(isChecked);
		tvIdOperatoreDATO.setEnabled(isChecked);
		tvNomeOperatoreDATO.setEnabled(isChecked);
		
		int visibility = isChecked ? View.GONE : View.VISIBLE;
		
		tvIdOperatore.setVisibility(visibility);
		tvIdOperatoreDATO.setVisibility(visibility);
		tvNomeOperatore.setVisibility(visibility);
		tvNomeOperatoreDATO.setVisibility(visibility);
	}
	
	@Override
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
							List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, true, true);
							int i = 0;
							
							if(tempID == comandoGPRSCFG)								//vado a capire quale comando è stato dato
							{															//vado a scrivere i dati su file
								String tempIdOperatore = dati.get(1)[i++].replace("\"", "").replace("Usato:", "").trim();
								
								if(tempIdOperatore.compareTo("") == 0)
								{
									editor.putString("tvIdOperatoreDATO", getResources().getString(R.string.vuoto));
									editor.putString("tvNomeOperatoreDATO", getResources().getString(R.string.vuoto));
									i++;
									editor.putBoolean("tbImpostaAutomaticamente", false);
								}
								else
								{
									editor.putBoolean("tbImpostaAutomaticamente", true);
									editor.putString("tvIdOperatoreDATO", tempIdOperatore);
									editor.putString("tvNomeOperatoreDATO", dati.get(1)[i++].replace("\"", ""));
								}
								
								editor.putString("etAPN", dati.get(1)[i++].replace("\"", ""));
								editor.putString("etUtente", dati.get(1)[i++].replace("\"", ""));
								editor.putString("etPassword", dati.get(1)[i++].replace("\"", ""));
								editor.putString("etIPFisso", dati.get(1)[i++].replace("\"", ""));
								editor.putBoolean("tbDataCompression", dati.get(1)[i++].replace("\"", "") == "1");
								editor.putBoolean("tbHeaderCompression", dati.get(1)[i++].replace("\"", "") == "1");
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
						List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, false, true);
						int i = 0;
						boolean erroriTemp = false;
						
						if(tempID == comandoGPRSCFG)											//vado a capire quale comando è stato dato
							if((erroriTemp = MetodiComuni.TrovaErrori(tempRisposta)) == false)	//se non ci sono stati errori
							{
								String tempIdOperatore = dati.get(1)[i++].replace("\"", "").replace("Usato:", "").trim();
								
								if(tempIdOperatore.compareTo("") == 0)
								{
									editor.putString("tvIdOperatoreDATO", getResources().getString(R.string.vuoto));
									editor.putString("tvNomeOperatoreDATO", getResources().getString(R.string.vuoto));
									i++;
									editor.putBoolean("tbImpostaAutomaticamente", false);
								}
								else
								{
									editor.putBoolean("tbImpostaAutomaticamente", true);
									editor.putString("tvIdOperatoreDATO", tempIdOperatore);
									editor.putString("tvNomeOperatoreDATO", dati.get(1)[i++].replace("\"", ""));
								}
								
								editor.putString("etAPN", dati.get(1)[i++].replace("\"", ""));
								editor.putString("etUtente", dati.get(1)[i++].replace("\"", ""));
								editor.putString("etPassword", dati.get(1)[i++].replace("\"", ""));
								editor.putString("etIPFisso", dati.get(1)[i++].replace("\"", ""));
								editor.putBoolean("tbDataCompression", dati.get(1)[i++].replace("\"", "") == "1");
								editor.putBoolean("tbHeaderCompression", dati.get(1)[i++].replace("\"", "") == "1");
							}
							else
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
						
						
						if(tempComandiInCoda == false)					//se non ci sono comandi in coda
							editor.putInt("pbCaricamento", View.GONE);	//rendo invisibile la progressbar circolare
							
						if(erroriTemp == false)							//se non ci sono stati errori nei parametri passati
							MetodiComuni.CreaToastOperazioneEseguitaPersonalizzata(getApplicationContext(), dati.get(0)[0]);
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
	protected void onPause ()														//entro qui ogni volta che l'utente TORNA INDIETRO, GIRA IL MONITOR o quando va direttamente sulla HOME di android
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
