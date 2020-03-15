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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class InputOutput extends Activity implements OnRead, OnCheckedChangeListener{

	String id_activity, id_terminale;	//questo è un id che mi viene passato dal main_activity
	SharedPreferences preferences;		//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	static OnRead[] read = new OnRead[1];
	
	String tempRisposta, tempID, tempRispostaTemporanea;
	int tempRitorno, tempTipoComando;
	boolean tempComandiInCoda;
	int timeout;
	int ritorno;
	
	String comandoINCFG, comandoINDSC, comandoOUTCFG, comandoOUTSET, comandoSTATO;
	
	RadioGroup rgConfIngre1, rgConfIngre2, rgConfIngre3;
	EditText etConfIngre1Descrizione, etConfIngre2Descrizione, etConfIngre3Descrizione;
	RadioButton rbConfOUT1ModoOnOff, rbConfOUT1ModoImpulsivo;
	RadioButton rbConfOUT2ModoOnOff, rbConfOUT2ModoImpulsivo;
	EditText etConfOUT1TempoImpulsivo, etConfOUT2TempoImpulsivo;
	ToggleButton tbTestUscita1, tbTestUscita2;
	TextView tvTestIngresso1, tvTestIngresso2, tvTestIngresso3;
	ProgressBar pbCaricamento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_output);
		read[0] = this;
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		id_terminale = getIntent().getStringExtra("idTerminale");	
		
		timeout = getIntent().getIntExtra("timeout", 900);		//salvo il timeout che mi è stato passato dal main_activity
		
		comandoINCFG = "INCFG";
		comandoINDSC = "INDSC";
		comandoOUTCFG = "OUTCFG";
		comandoOUTSET = "OUTSET";
		comandoSTATO = "STATO";
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();
		
		OttieniPuntatoriView();
	}
	
	private void OttieniPuntatoriView()												//ottengo i puntatori ai vari View dell'intefaccia grafica
	{
		rgConfIngre1 = ((RadioGroup)findViewById(R.id.IO_rg_ConfIngresso1));
		rgConfIngre2 = ((RadioGroup)findViewById(R.id.IO_rg_ConfIngresso2));
		rgConfIngre3 = ((RadioGroup)findViewById(R.id.IO_rg_ConfIngresso3));
		
		etConfIngre1Descrizione = ((EditText)findViewById(R.id.IO_et_DescrizioneConfIngre1));
		etConfIngre2Descrizione = ((EditText)findViewById(R.id.IO_et_DescrizioneConfIngre2));
		etConfIngre3Descrizione = ((EditText)findViewById(R.id.IO_et_DescrizioneConfIngre3));
		
		rbConfOUT1ModoOnOff = ((RadioButton)findViewById(R.id.IO_rb_ConfOUT1_ModoOnOff));
		rbConfOUT1ModoOnOff.setOnCheckedChangeListener(this);
		rbConfOUT1ModoImpulsivo = ((RadioButton)findViewById(R.id.IO_rb_ConfOUT1_ModoImpulsivo));
		rbConfOUT1ModoImpulsivo.setOnCheckedChangeListener(this);
		
		rbConfOUT2ModoOnOff = ((RadioButton)findViewById(R.id.IO_rb_ConfOUT2_ModoOnOff));
		rbConfOUT2ModoOnOff.setOnCheckedChangeListener(this);
		rbConfOUT2ModoImpulsivo = ((RadioButton)findViewById(R.id.IO_rb_ConfOUT2_ModoImpulsivo));
		rbConfOUT2ModoImpulsivo.setOnCheckedChangeListener(this);
		
		etConfOUT1TempoImpulsivo = ((EditText)findViewById(R.id.IO_et_ConfOUT1_TempoImpulsivo));
		etConfOUT2TempoImpulsivo = ((EditText)findViewById(R.id.IO_et_ConfOUT2_TempoImpulsivo));
		
		tbTestUscita1 = ((ToggleButton)findViewById(R.id.IO_tb_Uscita1));
		tbTestUscita2 = ((ToggleButton)findViewById(R.id.IO_tb_Uscita2));
		
		tvTestIngresso1 = ((TextView)findViewById(R.id.IO_tv_i1_DATO));
		tvTestIngresso2 = ((TextView)findViewById(R.id.IO_tv_i2_DATO));
		tvTestIngresso3 = ((TextView)findViewById(R.id.IO_tv_i3_DATO));
		
		pbCaricamento = ((ProgressBar)findViewById(R.id.IO_pb_Caricamento));
	}
	
	private void CaricaDati()
	{
		for(int i = 0; i < rgConfIngre1.getChildCount(); i++)
		{
			if(rgConfIngre1.getChildAt(i).getTag().toString().compareTo(preferences.getString("rgConfIngre1", "-1")) == 0)
				((RadioButton)rgConfIngre1.getChildAt(i)).setChecked(true);
			
			if(rgConfIngre2.getChildAt(i).getTag().toString().compareTo(preferences.getString("rgConfIngre2", "-1")) == 0)
				((RadioButton)rgConfIngre2.getChildAt(i)).setChecked(true);
			
			if(rgConfIngre3.getChildAt(i).getTag().toString().compareTo(preferences.getString("rgConfIngre3", "-1")) == 0)
				((RadioButton)rgConfIngre3.getChildAt(i)).setChecked(true);
		}
		
		etConfIngre1Descrizione.setText(preferences.getString("etConfIngre1Descrizione", ""));
		etConfIngre2Descrizione.setText(preferences.getString("etConfIngre2Descrizione", ""));
		etConfIngre3Descrizione.setText(preferences.getString("etConfIngre3Descrizione", ""));
		
		rbConfOUT1ModoOnOff.setChecked(preferences.getBoolean("rbConfOUT1ModoOnOff", true));
		rbConfOUT1ModoImpulsivo.setChecked(preferences.getBoolean("rbConfOUT1ModoImpulsivo", false));
		etConfOUT1TempoImpulsivo.setText(preferences.getString("etConfOUT1TempoImpulsivo", ""));
		onCheckedChanged(rbConfOUT1ModoOnOff, rbConfOUT1ModoOnOff.isChecked());
		
		rbConfOUT2ModoOnOff.setChecked(preferences.getBoolean("rbConfOUT2ModoOnOff", true));
		rbConfOUT2ModoImpulsivo.setChecked(preferences.getBoolean("rbConfOUT2ModoImpulsivo", false));
		etConfOUT2TempoImpulsivo.setText(preferences.getString("etConfOUT2TempoImpulsivo", ""));
		onCheckedChanged(rbConfOUT2ModoOnOff, rbConfOUT2ModoOnOff.isChecked());
		
		tbTestUscita1.setChecked(preferences.getBoolean("tbTestUscita1", false));
		tbTestUscita2.setChecked(preferences.getBoolean("tbTestUscita2", false));
		
		tvTestIngresso1.setText(preferences.getString("tvTestIngresso1", getResources().getString(R.string.vuoto)));
		tvTestIngresso2.setText(preferences.getString("tvTestIngresso2", getResources().getString(R.string.vuoto)));
		tvTestIngresso3.setText(preferences.getString("tvTestIngresso3", getResources().getString(R.string.vuoto)));
		
		pbCaricamento.setVisibility(preferences.getInt("pbCaricamento", View.GONE));
		
		
	}
	
	private void SalvaDati()
	{							//salvo i dati
		editor.putString("rgConfIngre1", findViewById(rgConfIngre1.getCheckedRadioButtonId()).getTag().toString());	
		editor.putString("rgConfIngre2", findViewById(rgConfIngre2.getCheckedRadioButtonId()).getTag().toString());	
		editor.putString("rgConfIngre3", findViewById(rgConfIngre3.getCheckedRadioButtonId()).getTag().toString());	
		
		editor.putString("etConfIngre1Descrizione", etConfIngre1Descrizione.getText().toString());
		editor.putString("etConfIngre2Descrizione", etConfIngre2Descrizione.getText().toString());
		editor.putString("etConfIngre3Descrizione", etConfIngre3Descrizione.getText().toString());
		
		editor.putBoolean("rbConfOUT1ModoOnOff", rbConfOUT1ModoOnOff.isChecked());
		editor.putBoolean("rbConfOUT1ModoImpulsivo", rbConfOUT1ModoImpulsivo.isChecked());
		editor.putString("etConfOUT1TempoImpulsivo", etConfOUT1TempoImpulsivo.getText().toString());
		
		editor.putBoolean("rbConfOUT2ModoImpulsivo", rbConfOUT2ModoImpulsivo.isChecked());
		editor.putBoolean("rbConfOUT2ModoOnOff", rbConfOUT2ModoOnOff.isChecked());
		editor.putString("etConfOUT2TempoImpulsivo", etConfOUT2TempoImpulsivo.getText().toString());
		
		editor.putBoolean("tbTestUscita1", tbTestUscita1.isChecked());
		editor.putBoolean("tbTestUscita2", tbTestUscita2.isChecked());
		
		editor.putString("tvTestIngresso1", tvTestIngresso1.getText().toString());
		editor.putString("tvTestIngresso2", tvTestIngresso2.getText().toString());
		editor.putString("tvTestIngresso3", tvTestIngresso3.getText().toString());
		
		editor.putInt("pbCaricamento", pbCaricamento.getVisibility());
		editor.commit();			//scrivo i dati su file
	}
	
	public void OnClickAggiornaConfInressi(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoINCFG).getBytes(), timeout, read, comandoINCFG, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)																//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickAggiornaDescrizione(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoINDSC).getBytes(), timeout, read, comandoINDSC, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)																//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickAggiornaConfUscite(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoOUTCFG).getBytes(), timeout, read, comandoOUTCFG, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)																//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickAggiornaTestUscite(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoOUTSET).getBytes(), timeout, read, comandoOUTSET, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)																//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickAggiornaTestIngessi(View v)
	{
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoSTATO).getBytes(), timeout, read, comandoSTATO, TIPO_COMANDO.LEGGI, v == null);
		if(v != null)																//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickAggiornaTutto(View v)
	{
		OnClickAggiornaConfInressi(new View(this));									//passo come parametro un oggetto creato a caso, in questo modo evito di accodare questo comando
		
		if(ritorno == 1)	//se il comando che ho appena mandato è stato eseguito, allora accodo gli altri comandi
		{
			editor.putInt("pbCaricamento", View.VISIBLE);							//rendo visibile la progressbar circolare
			OnClickAggiornaDescrizione(null);
			OnClickAggiornaConfUscite(null);
			OnClickAggiornaTestUscite(null);
			OnClickAggiornaTestIngessi(null);
		}
	}
	
	public void OnClickSalvaConfIngressi(View v)
	{
		String dati = findViewById(rgConfIngre1.getCheckedRadioButtonId()).getTag().toString() + ",";
		dati += findViewById(rgConfIngre2.getCheckedRadioButtonId()).getTag().toString() + ",";
		dati += findViewById(rgConfIngre3.getCheckedRadioButtonId()).getTag().toString();
		
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoINCFG, dati);
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoINCFG, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)																	//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaDescrizioneConf1(View v)
	{		
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoINDSC, "1," + "\"" + etConfIngre1Descrizione.getText() + "\"");
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, "1" + comandoINDSC, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)																	//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaDescrizioneConf2(View v)
	{		
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoINDSC, "2," + "\"" + etConfIngre2Descrizione.getText() + "\"");
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, "2" + comandoINDSC, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)																	//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaDescrizioneConf3(View v)
	{		
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoINDSC, "3," + "\"" + etConfIngre3Descrizione.getText() + "\"");
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, "3" + comandoINDSC, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)																	//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaConfUscite(View v)
	{
		String dati;
		if(rbConfOUT1ModoOnOff.isChecked())
			dati = "0,";
		else
			dati = etConfOUT1TempoImpulsivo.getText() + ",";
		
		if(rbConfOUT2ModoOnOff.isChecked())
			dati += "0";
		else
			dati += etConfOUT2TempoImpulsivo.getText().toString();
		
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoOUTCFG, dati);
		ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoOUTCFG, TIPO_COMANDO.SCRIVI, v == null);
		if(v != null)																	//se l'evento è stato sollevato dall'utente
			MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickOnOffUscita(View v)
	{
		ToggleButton tb = (ToggleButton)v;
		String dati;
		if(tb.isChecked())
			dati = tb.getTag().toString() + ",1";
		else
			dati = tb.getTag().toString() + ",0";
		
		ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioScrivi(comandoOUTSET, dati).getBytes(), timeout, read, comandoOUTSET, TIPO_COMANDO.SCRIVI, v == null);
		MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);				//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
		
		if(ritorno != 1)															//se il comando non è andato a buon fine
			tb.setChecked(!tb.isChecked());											//imposto lo stato del bottone a come era prima di essere cliccato
	}
	
	public void OnClickSalvaTutto(View v)
	{
		OnClickSalvaConfIngressi(new View(this));									//passo come parametro un oggetto creato a caso, in questo modo evito di accodare questo comando
		
		if(ritorno == 1)															//se il comando che ho appena mandato è stato eseguito, allora accodo gli altri comandi
		{
			editor.putInt("pbCaricamento", View.VISIBLE);							//rendo visibile la progressbar circolare 
			OnClickSalvaDescrizioneConf1(null);
			OnClickSalvaDescrizioneConf2(null);
			OnClickSalvaDescrizioneConf3(null);
			OnClickSalvaConfUscite(null);
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		if(buttonView == rbConfOUT1ModoOnOff)
			if(isChecked)
				etConfOUT1TempoImpulsivo.setEnabled(false);
			else
				etConfOUT1TempoImpulsivo.setEnabled(true);
		else
			if(buttonView == rbConfOUT2ModoOnOff)
				if(isChecked)
					etConfOUT2TempoImpulsivo.setEnabled(false);
				else
					etConfOUT2TempoImpulsivo.setEnabled(true);
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
							
							if(tempID == comandoINCFG)										//vado a capire quale comando è stato dato
							{	
								editor.putString("rgConfIngre1", dati.get(0)[i++]);	
								editor.putString("rgConfIngre2", dati.get(0)[i++]);	
								editor.putString("rgConfIngre3", dati.get(0)[i]);
							}
							
							if(tempID == comandoINDSC)
							{
								editor.putString("etConfIngre1Descrizione", dati.get(++i)[0].substring(4));
								editor.putString("etConfIngre2Descrizione", dati.get(++i)[0].substring(4));
								editor.putString("etConfIngre3Descrizione", dati.get(++i)[0].substring(4));
							}
							
							if(tempID == comandoOUTCFG)
							{
								editor.putBoolean("rbConfOUT1ModoOnOff", dati.get(0)[0].compareTo("0") == 0);
								editor.putBoolean("rbConfOUT1ModoImpulsivo", Integer.parseInt(dati.get(0)[0]) > 0);
								editor.putBoolean("rbConfOUT2ModoOnOff", dati.get(0)[1].compareTo("0") == 0);
								editor.putBoolean("rbConfOUT2ModoImpulsivo", Integer.parseInt(dati.get(0)[1]) > 0);
								
								if(dati.get(i)[0].compareTo("0") == 0)
									editor.putString("etConfOUT1TempoImpulsivo", "");
								else
									editor.putString("etConfOUT1TempoImpulsivo", dati.get(0)[0]);
								
								if(dati.get(i)[1].compareTo("0") == 0)
									editor.putString("etConfOUT2TempoImpulsivo", "");
								else
									editor.putString("etConfOUT2TempoImpulsivo", dati.get(0)[1]);
							}
							
							if(tempID == comandoOUTSET)
							{
								editor.putBoolean("tbTestUscita1", dati.get(1)[0].substring(5).compareTo("1") == 0);
								editor.putBoolean("tbTestUscita2", dati.get(2)[0].substring(5).compareTo("1") == 0);
							}
							
							
							if(tempID == comandoSTATO)
							{
								editor.putString("tvTestIngresso1", dati.get(++i)[0].substring(6, 7).compareTo("0") == 0 ? "OFF" : "ON");
								editor.putString("tvTestIngresso2", dati.get(++i)[0].substring(6, 7).compareTo("0") == 0 ? "OFF" : "ON");
								editor.putString("tvTestIngresso3", dati.get(++i)[0].substring(6, 7).compareTo("0") == 0 ? "OFF" : "ON");
								editor.putBoolean("tbTestUscita1", dati.get(++i)[0].substring(5).compareTo("1") == 0);
								editor.putBoolean("tbTestUscita2", dati.get(++i)[0].substring(5).compareTo("1") == 0);
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
						if(tempID == comandoINCFG)							//vado a capire quale comando è stato dato
						{
							List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, false, true);
							if(MetodiComuni.TrovaErrori(tempRisposta) == false)	//se non ci sono stati errori
							{
								int i = 0;
								editor.putString("rgConfIngre1", dati.get(1)[i++]);	
								editor.putString("rgConfIngre2", dati.get(1)[i++]);	
								editor.putString("rgConfIngre3", dati.get(1)[i]);
								
								MetodiComuni.CreaToastOperazioneEseguitaPersonalizzata(getApplicationContext(), dati.get(0)[0]);
							}
							else
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
						}
						
						if(tempID.substring(1).compareTo(comandoINDSC) == 0)	//vado a capire quale comando è stato dato
						{
							List<String[]> dati = MetodiComuni.OttieniDati(tempID.substring(1), tempRisposta, false, false);
							if(MetodiComuni.TrovaErrori(tempRisposta) == false)	//se non ci sono stati errori
							{
								if(tempID.charAt(0) == '1')
									editor.putString("etConfIngre1Descrizione", dati.get(1)[0].substring(4));
								else
									if(tempID.charAt(0) == '2')
										editor.putString("etConfIngre2Descrizione", dati.get(2)[0].substring(4));	
									else
										if(tempID.charAt(0) == '3')
											editor.putString("etConfIngre3Descrizione", dati.get(3)[0].substring(4));
								
								MetodiComuni.CreaToastOperazioneEseguitaPersonalizzata(getApplicationContext(), dati.get(0)[0]);
							}
							else
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
						}
						
						if(tempID == comandoOUTCFG)								//vado a capire quale comando è stato dato
						{
							List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, false, true);
							if(MetodiComuni.TrovaErrori(tempRisposta) == false)	//se non ci sono stati errori
							{
								int i = 1;
								editor.putBoolean("rbConfOUT1ModoOnOff", dati.get(i)[0].compareTo("0") == 0);
								editor.putBoolean("rbConfOUT1ModoImpulsivo", Integer.parseInt(dati.get(i)[0]) > 0);
								editor.putBoolean("rbConfOUT2ModoOnOff", dati.get(i)[1].compareTo("0") == 0);
								editor.putBoolean("rbConfOUT2ModoImpulsivo", Integer.parseInt(dati.get(i)[1]) > 0);
								
								if(dati.get(i)[0].compareTo("0") == 0)
									editor.putString("etConfOUT1TempoImpulsivo", "");
								else
									editor.putString("etConfOUT1TempoImpulsivo", dati.get(i)[0]);
								
								if(dati.get(i)[1].compareTo("0") == 0)
									editor.putString("etConfOUT2TempoImpulsivo", "");
								else
									editor.putString("etConfOUT2TempoImpulsivo", dati.get(i)[1]);
								
								MetodiComuni.CreaToastOperazioneEseguitaPersonalizzata(getApplicationContext(), dati.get(0)[0]);
							}
							else
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
						}
						
						if(tempID == comandoOUTSET)								//vado a capire quale comando è stato dato
						{
							List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, false, false);
							if(MetodiComuni.TrovaErrori(tempRisposta) == false)	//se non ci sono stati errori
							{
								editor.putBoolean("tbTestUscita1", dati.get(1)[0].substring(5).compareTo("1") == 0);
								editor.putBoolean("tbTestUscita2", dati.get(2)[0].substring(5).compareTo("1") == 0);
								
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
	protected void onPause()
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
