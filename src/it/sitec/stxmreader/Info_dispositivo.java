package it.sitec.stxmreader;

import java.util.Calendar;
import java.util.List;

import STCM.DEVICE;
import STCM.OnRead;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Info_dispositivo extends Activity implements OnRead{
	String id_activity, id_terminale;	//questo è un id che mi viene passato dal main_activity
	SharedPreferences preferences;		//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	static OnRead[] read = new OnRead[1];
	
	String tempRisposta, tempID, tempRispostaTemporanea;
	int tempRitorno, tempTipoComando;
	boolean tempComandiInCoda;
	int timeout;
	int ritorno;
	
	String comandoINFO, comandoCSQ, comandoTIME, comandoSITO;
	
	TextView tvCostruttore, tvVersioneSoftware, tvModelloDispositivo, tvModoFunzionamento, tvLivelloSegnale, tvQualitaSegnale;
	EditText etData, etOra, etSitoInstallazione;
	ProgressBar pbLivelloSegnale, pbQualitaSegnale, pbCaricamento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_dispositivo);
		read[0] = this;
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		id_terminale = getIntent().getStringExtra("idTerminale");	
		
		timeout = getIntent().getIntExtra("timeout", 900);		//salvo il timeout che mi è stato passato dal main_activity
		
		comandoINFO = "INFO";
		comandoCSQ = "CSQ";
		comandoTIME = "TIME";
		comandoSITO = "SITO";
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();
		
		OttieniPuntatoriView();
	}

	private void OttieniPuntatoriView()												//ottengo i puntatori ai vari View dell'intefaccia grafica
	{
		tvCostruttore = ((TextView)findViewById(R.id.INFO_tv_Costruttore_DATO));
		tvVersioneSoftware = ((TextView)findViewById(R.id.INFO_tv_VersioneSoftware_DATO));
		tvModelloDispositivo = ((TextView)findViewById(R.id.INFO_tv_ModelloDispositivo_DATO));
		tvModoFunzionamento = ((TextView)findViewById(R.id.INFO_tv_ModoFunzionamento_DATO));
		
		pbLivelloSegnale = ((ProgressBar)findViewById(R.id.INFO_pb_LivelloSegnale));
		pbQualitaSegnale = ((ProgressBar)findViewById(R.id.INFO_pb_QualitaSegnale));
		tvLivelloSegnale = ((TextView)findViewById(R.id.INFO_tv_LivelloSegnale_DATO));
		tvQualitaSegnale = ((TextView)findViewById(R.id.INFO_tv_QualitaSegnale_DATO));
		
		etData = ((EditText)findViewById(R.id.INFO_et_Data));
		etOra = ((EditText)findViewById(R.id.INFO_et_Ora));
		
		etSitoInstallazione = ((EditText)findViewById(R.id.INFO_et_SitoIstallazione));
		
		pbCaricamento = ((ProgressBar)findViewById(R.id.INFO_pb));
	}
	
	public void CaricaDati()														//carico i dati salvati su file
	{
	    tvCostruttore.setText(preferences.getString("tvCostruttore", getResources().getString(R.string.vuoto)));				//caico i dati salvati
	    tvVersioneSoftware.setText(preferences.getString("tvVersioneSoftware", getResources().getString(R.string.vuoto)));
	    tvModelloDispositivo.setText(preferences.getString("tvModelloDispositivo", getResources().getString(R.string.vuoto)));
	    tvModoFunzionamento.setText(preferences.getString("tvModoFunzionamento", getResources().getString(R.string.vuoto)));
	    		
	    tvLivelloSegnale.setText(preferences.getString("tvLivelloSegnale", getResources().getString(R.string.vuoto)));
	    tvQualitaSegnale.setText(preferences.getString("tvQualitaSegnale", getResources().getString(R.string.vuoto)));
	    pbLivelloSegnale.setProgress(preferences.getInt("pbLivelloSegnale", 0));
	    pbQualitaSegnale.setProgress(preferences.getInt("pbQualitaSegnale", 0));
	    		
	    etData.setText(preferences.getString("etData", ""));
	    etOra.setText(preferences.getString("etOra", ""));
	    		
	    etSitoInstallazione.setText(preferences.getString("etSitoInstallazione", ""));
	    		
	    pbCaricamento.setVisibility(preferences.getInt("pbCaricamento", View.GONE));
	}
	
	private void SalvaDati()
	{
		editor.putString("tvCostruttore", tvCostruttore.getText().toString());					//salvo i dati
		editor.putString("tvVersioneSoftware", tvVersioneSoftware.getText().toString());
		editor.putString("tvModelloDispositivo", tvModelloDispositivo.getText().toString());
		editor.putString("tvModoFunzionamento", tvModoFunzionamento.getText().toString());
		
		editor.putString("tvLivelloSegnale", tvLivelloSegnale.getText().toString());
		editor.putString("tvQualitaSegnale", tvQualitaSegnale.getText().toString());
		editor.putInt("pbLivelloSegnale", pbLivelloSegnale.getProgress());
		editor.putInt("pbQualitaSegnale", pbQualitaSegnale.getProgress());
		
		editor.putString("etData", etData.getText().toString());
		editor.putString("etOra", etOra.getText().toString());
		
		editor.putString("etSitoInstallazione", etSitoInstallazione.getText().toString());
		
		editor.putInt("pbCaricamento", pbCaricamento.getVisibility());
		
		editor.commit();							//scrivo i dati su file
	}
	
	public void OnClickButtonSincronizzaOra(View v)
	{
		Calendar c = Calendar.getInstance(); 
		
		etData.setText(c.get(Calendar.DATE) + "/" + (c.get(Calendar.MONTH) + 1)+ "/" + c.get(Calendar.YEAR));
		etOra.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
		OnClickButtonSalvaTempo(new View(this));					//passo come parametro un oggetto creato a caso, in questo modo evito di accodare questo comando
	}
	
	public void OnClickButtonAggiornaInformazioni(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoINFO).getBytes(), timeout, read, comandoINFO, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)													//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickButtonAggiornaSegnale(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtBaseLeggi(comandoCSQ).getBytes(), timeout, read, comandoCSQ, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)													//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickButtonAggiornaTempo(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoTIME).getBytes(), timeout, read, comandoTIME, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)													//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickButtonAggiornaSito(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoSITO).getBytes(), timeout, read, comandoSITO, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)													//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}

	public void OnClickButtonAggiornaTutto(View v)
	{
		OnClickButtonAggiornaInformazioni(new View(this));				//passo come parametro un oggetto creato a caso, in questo modo evito di accodare questo comando
		
		if(ritorno == 1)	//se il comando che ho appena mandato è stato eseguito, allora accodo gli altri comandi
		{
			editor.putInt("pbCaricamento", View.VISIBLE);	//rendo visibile la progressbar circolare 
			OnClickButtonAggiornaSegnale(null);
			OnClickButtonAggiornaTempo(null);
		}
	}
	
	public void OnClickButtonSalvaTempo(View v)
	{
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoTIME, "\"" + etData.getText() + " " + etOra.getText() + "\"");
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoTIME, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)													//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickButtonSalvaSito(View v)
	{
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoSITO, "\"" + etSitoInstallazione.getText() + "\"");
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoSITO, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)													//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickButtonSalvaTutto(View v)
	{
		OnClickButtonSalvaTempo(new View(this));					//passo come parametro un oggetto creato a caso, in questo modo evito di accodare questo comando
		
		if(ritorno == 1)	//se il comando che ho appena mandato è stato eseguito, allora accodo gli altri comandi
		{
			editor.putInt("pbCaricamento", View.VISIBLE);			//rendo visibile la progressbar circolare
			OnClickButtonSalvaSito(null);
		}
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
					if(tempTipoComando == TIPO_COMANDO.LEGGI)
						if(MetodiComuni.TrovaErrori(tempRisposta) == false)				//se non ci sono stati errori
						{
							List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, true, true);
							int i = 0;
							
							if(tempID == comandoINFO)									//vado a capire quale comando è stato dato
							{
								editor.putString("etSitoInstallazione", dati.get(i++)[0]);	//vado a scrivere i dati su file
								editor.putString("tvModelloDispositivo", dati.get(i++)[0]);
								editor.putString("tvCostruttore", dati.get(i++)[0]);
								editor.putString("tvVersioneSoftware", dati.get(i++)[0]);
								editor.putString("tvModoFunzionamento", dati.get(i++)[0].replace("MODE", "").trim());
							}
							
							if(tempID == comandoCSQ)
							{
								int livelloSegnale, qualitaSegnale;
								if(Float.parseFloat(dati.get(i)[0]) <= 31.0)
									livelloSegnale = (int)(Float.parseFloat(dati.get(i)[0]) / 31.0 * 100.0);
								else
									livelloSegnale = 0;
								
								if(Integer.parseInt(dati.get(i)[1]) <= 7)
									qualitaSegnale = ((7 - Integer.parseInt(dati.get(i)[1])) / 7) * 100;
								else
									qualitaSegnale = 0;
								
								editor.putString("tvLivelloSegnale", livelloSegnale + "%");
								editor.putString("tvQualitaSegnale", qualitaSegnale + "%");
								editor.putInt("pbLivelloSegnale", livelloSegnale);
								editor.putInt("pbQualitaSegnale", qualitaSegnale);
							}
							
							if(tempID == comandoTIME)
							{
								editor.putString("etData", dati.get(i)[0].substring(0, dati.get(i)[0].indexOf(" ")));
								editor.putString("etOra", dati.get(i)[0].substring(dati.get(i)[0].indexOf(" ") + 1));
							}
							
							if(tempID == comandoSITO)
								editor.putString("etSitoInstallazione", dati.get(i)[0]);
							
							if(tempComandiInCoda == false)		//se non ci sono comandi in coda
							{
								MetodiComuni.CreaToatOperazioneEseguita(getApplicationContext());
								editor.putInt("pbCaricamento", View.GONE);
							}
						}
						else
						{
							editor.putInt("pbCaricamento", View.GONE);	//rendo invisibile la progressbar circolare
							MetodiComuni.CreaToastErrore(getApplicationContext());
						}
					
					if(tempTipoComando == TIPO_COMANDO.SCRIVI)
					{
						List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, false, false);
						int i = 1;
						boolean erroriTemp = false;
						
						if(tempID == comandoTIME)											//vado a capire quale comando è stato dato
							if((erroriTemp = MetodiComuni.TrovaErrori(tempRisposta)) == false)				//se non ci sono stati errori
							{
								editor.putString("etData", dati.get(i)[0].substring(0, dati.get(i)[0].indexOf(" ")));
								editor.putString("etOra", dati.get(i)[0].substring(dati.get(i)[0].indexOf(" ") + 1));
							}
							else
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
						
						if(tempID == comandoSITO)
							if((erroriTemp = MetodiComuni.TrovaErrori(tempRisposta)) == false)				//se non ci sono stati errori
								editor.putString("etSitoInstallazione", dati.get(i)[0]);
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
		runOnUiThread(new Runnable()	//devo avviare un nuovo tread altrimenti non posso modificare la grafica di android
		{
			@Override
			public void run()
		    {
				MetodiComuni.AggiornaTerminale(tempRispostaTemporanea, getSharedPreferences(id_terminale, 0));
		    }
		});
	}
	
	@Override
	public void ErroreWriteComandoInCoda(int ritorno)								//entro qui ogni volta che fallisce l'esecuzione di un comando IN CODA 
	{
		tempRitorno = ritorno;
		runOnUiThread(new Runnable()	//devo avviare un nuovo tread altrimenti non posso modificare la grafica di android
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
