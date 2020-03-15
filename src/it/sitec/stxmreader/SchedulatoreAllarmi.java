package it.sitec.stxmreader;

import java.util.List;

import STCM.DEVICE;
import STCM.OnRead;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SchedulatoreAllarmi extends Activity implements OnRead, OnCheckedChangeListener, android.widget.CompoundButton.OnCheckedChangeListener{
	
	String id_activity, id_terminale;	//questo è un id che mi viene passato dal main_activity
	SharedPreferences preferences;		//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	static OnRead[] read = new OnRead[1];
	
	String tempRisposta, tempID, tempRispostaTemporanea;
	int tempRitorno, tempTipoComando;
	boolean tempComandiInCoda;
	int timeout;
	
	String comandoSCHMBTEST;
	
	RadioGroup rgSche;
	RelativeLayout rlGiorno, rlMesi;
	TextView tvIlGiorno, tvOgniNSettimane, tvOgniGiorniSettimana, tvDelGiorno, tvDescrizioneDelGiorno;
	EditText etAlleOre, etIlGiorno, etDelGiorno;
	Spinner spOgniNSettimane, spOgniGiorniSettimana;
	CheckBox cbTuttiIMesi, cbGennaio, cbFebbraio, cbMarzo, cbAprile, cbMaggio, cbGiugno, cbLuglio, cbAgosto, cbSettembre, cbOttobre, cbNovembre, cbDicembre;
	TextView tvInfo;
	ProgressBar pbCaricamento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedulatore_allarmi);
		read[0] = this;
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		id_terminale = getIntent().getStringExtra("idTerminale");	
		
		timeout = getIntent().getIntExtra("timeout", 900);		//salvo il timeout che mi è stato passato dal main_activity
		
		comandoSCHMBTEST = "SCHMBTEST";
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();
		
		OttieniPuntatoriView();
	}
	
	private void OttieniPuntatoriView()												//ottengo i puntatori ai vari View dell'intefaccia grafica
	{
		rgSche = ((RadioGroup)findViewById(R.id.SCHEALL_rg));
		rgSche.setOnCheckedChangeListener(this);
		
		tvInfo = ((TextView)findViewById(R.id.SCHEALL_tv_Info));
		
		rlGiorno = ((RelativeLayout)findViewById(R.id.SCHEALL_RLayout_Giorno));
		rlMesi = ((RelativeLayout)findViewById(R.id.SCHEALL_RLayout_Mesi));
		
		tvIlGiorno = ((TextView)findViewById(R.id.SCHEALL_tv_IlGiorno));
		tvOgniNSettimane = ((TextView)findViewById(R.id.SCHEALL_tv_OgniNSettimane));
		tvOgniGiorniSettimana = ((TextView)findViewById(R.id.SCHEALL_tv_GiorniSettimana));
		tvDelGiorno = ((TextView)findViewById(R.id.SCHEALL_tv_DelGiorno));
		tvDescrizioneDelGiorno = ((TextView)findViewById(R.id.SCHEALL_tv_DescrizioneDelGiorno));
		
		etAlleOre = ((EditText)findViewById(R.id.SCHEALL_et_AlleOre));
		etIlGiorno = ((EditText)findViewById(R.id.SCHEALL_et_IlGiorno));
		etDelGiorno = ((EditText)findViewById(R.id.SCHEALL_et_DelGiorno));
		
		spOgniNSettimane = ((Spinner)findViewById(R.id.SCHEALL_sp_NSettimane));
		spOgniGiorniSettimana = ((Spinner)findViewById(R.id.SCHEALL_sp_GiorniSettimana));
		
		cbTuttiIMesi = ((CheckBox)findViewById(R.id.SCHEALL_cb_TuttiIMesi));
		cbGennaio = ((CheckBox)findViewById(R.id.SCHEALL_cb_Gennaio));
		cbFebbraio = ((CheckBox)findViewById(R.id.SCHEALL_cb_Febbraio));
		cbMarzo = ((CheckBox)findViewById(R.id.SCHEALL_cb_Marzo));
		cbAprile = ((CheckBox)findViewById(R.id.SCHEALL_cb_Aprile));
		cbMaggio = ((CheckBox)findViewById(R.id.SCHEALL_cb_Maggio));
		cbGiugno = ((CheckBox)findViewById(R.id.SCHEALL_cb_Giugno));
		cbLuglio = ((CheckBox)findViewById(R.id.SCHEALL_cb_Luglio));
		cbAgosto = ((CheckBox)findViewById(R.id.SCHEALL_cb_Agosto));
		cbSettembre = ((CheckBox)findViewById(R.id.SCHEALL_cb_Settembre));
		cbOttobre = ((CheckBox)findViewById(R.id.SCHEALL_cb_Ottobre));
		cbNovembre = ((CheckBox)findViewById(R.id.SCHEALL_cb_Novembre));
		cbDicembre = ((CheckBox)findViewById(R.id.SCHEALL_cb_Dicembre));
		
		cbTuttiIMesi.setOnCheckedChangeListener(this);
		cbGennaio.setOnCheckedChangeListener(this);
		cbFebbraio.setOnCheckedChangeListener(this);
		cbMarzo.setOnCheckedChangeListener(this);
		cbAprile.setOnCheckedChangeListener(this);
		cbMaggio.setOnCheckedChangeListener(this);
		cbGiugno.setOnCheckedChangeListener(this);
		cbLuglio.setOnCheckedChangeListener(this);
		cbAgosto.setOnCheckedChangeListener(this);
		cbSettembre.setOnCheckedChangeListener(this);
		cbOttobre.setOnCheckedChangeListener(this);
		cbNovembre.setOnCheckedChangeListener(this);
		cbDicembre.setOnCheckedChangeListener(this);
		
		pbCaricamento = ((ProgressBar)findViewById(R.id.SCHEALL_pb));
	}
	
	private void CaricaDati()	
	{
		for(int i = 0; i < rgSche.getChildCount(); i++)
		{//vado ad impostare i radioButton del schedulatore(utilizzo i TAG dei radioButton che sono stati impostati dal codice XML)
			if(rgSche.getChildAt(i).getTag().toString().compareTo(preferences.getString("rgSche", "n")) == 0)
				((RadioButton)rgSche.getChildAt(i)).setChecked(true);
		}
		
		tvInfo.setText(preferences.getString("tvInfo", ""));
		
		etAlleOre.setText(preferences.getString("etAlleOre", ""));
		etIlGiorno.setText(preferences.getString("etIlGiorno", ""));
		etDelGiorno.setText(preferences.getString("etDelGiorno", ""));
		
		spOgniNSettimane.setSelection(preferences.getInt("spOgniNSettimane", 0));
		spOgniGiorniSettimana.setSelection(preferences.getInt("spOgniGiorniSettimana", 0));
		
		cbTuttiIMesi.setChecked(preferences.getBoolean("cbTuttiIMesi", false));
		cbGennaio.setChecked(preferences.getBoolean("cbGennaio", false));
		cbFebbraio.setChecked(preferences.getBoolean("cbFebbraio", false));
		cbMarzo.setChecked(preferences.getBoolean("cbMarzo", false));
		cbAprile.setChecked(preferences.getBoolean("cbAprile", false));
		cbMaggio.setChecked(preferences.getBoolean("cbMaggio", false));
		cbGiugno.setChecked(preferences.getBoolean("cbGiugno", false));
		cbLuglio.setChecked(preferences.getBoolean("cbLuglio", false));
		cbAgosto.setChecked(preferences.getBoolean("cbAgosto", false));
		cbSettembre.setChecked(preferences.getBoolean("cbSettembre", false));
		cbOttobre.setChecked(preferences.getBoolean("cbOttobre", false));
		cbNovembre.setChecked(preferences.getBoolean("cbNovembre", false));
		cbDicembre.setChecked(preferences.getBoolean("cbDicembre", false));
		
		pbCaricamento.setVisibility(preferences.getInt("pbCaricamento", View.GONE));
	}
	
	private void SalvaDati()
	{
		editor.putString("rgSche", findViewById(rgSche.getCheckedRadioButtonId()).getTag().toString());	//vado a salvare i dati
		
		editor.putString("etAlleOre", etAlleOre.getText().toString());
		editor.putString("etIlGiorno", etIlGiorno.getText().toString());
		editor.putString("etDelGiorno", etDelGiorno.getText().toString());
		
		editor.putString("tvInfo", tvInfo.getText().toString());
		
		editor.putInt("spOgniNSettimane", spOgniNSettimane.getSelectedItemPosition());
		editor.putInt("spOgniGiorniSettimana", spOgniGiorniSettimana.getSelectedItemPosition());
		
		editor.putBoolean("cbTuttiIMesi", cbTuttiIMesi.isChecked());
		editor.putBoolean("cbGennaio", cbGennaio.isChecked());
		editor.putBoolean("cbFebbraio", cbFebbraio.isChecked());
		editor.putBoolean("cbMarzo", cbMarzo.isChecked());
		editor.putBoolean("cbAprile", cbAprile.isChecked());
		editor.putBoolean("cbMaggio", cbMaggio.isChecked());
		editor.putBoolean("cbGiugno", cbGiugno.isChecked());
		editor.putBoolean("cbLuglio", cbLuglio.isChecked());
		editor.putBoolean("cbAgosto", cbAgosto.isChecked());
		editor.putBoolean("cbSettembre", cbSettembre.isChecked());
		editor.putBoolean("cbOttobre", cbOttobre.isChecked());
		editor.putBoolean("cbNovembre", cbNovembre.isChecked());
		editor.putBoolean("cbDicembre", cbDicembre.isChecked());
		
		editor.putInt("pbCaricamento", pbCaricamento.getVisibility());
		editor.commit();	//scrivo i dati
	}
	
	public void OnClickAggiorna(View v)
	{
		int ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoSCHMBTEST).getBytes(), timeout, read, comandoSCHMBTEST, TIPO_COMANDO.LEGGI, v == null);															//se l'evento è stato sollevato dall'utente
		MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalva(View v)
	{
		String dati = findViewById(rgSche.getCheckedRadioButtonId()).getTag().toString();
		
		if(dati.compareTo("o") == 0)
		{
			try
			{
				String giorno =  etIlGiorno.getText().toString();
				dati += "," + giorno.substring(giorno.indexOf('/') + 1) + "," + giorno.substring(0, giorno.indexOf('/')) + "," + etAlleOre.getText();
			}
			catch(Exception e)
			{
				MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), "Inserire il giorno nel formato gg/mm");
				return;
			}
		}
		
		if(dati.compareTo("d") == 0)
			dati += "," + etAlleOre.getText();
		
		if(dati.compareTo("w") == 0)
		{
			String mesi = ottieniMesiImpostati();
			if(mesi != "")
				dati += "," + mesi;
			
			dati += "," + (spOgniGiorniSettimana.getSelectedItemPosition() + 1) + "," + etAlleOre.getText();
		}
		
		if(dati.compareTo("m") == 0)
		{
			String mesi = ottieniMesiImpostati();
			if(mesi != "")
				dati += "," + mesi;
			
			dati += "," + etDelGiorno.getText() + "," + etAlleOre.getText();
		}
		
		if(dati.compareTo("e") == 0)
		{
			String mesi = ottieniMesiImpostati();
			if(mesi != "")
				dati += "," + mesi;
			
			dati += "," + (spOgniNSettimane.getSelectedItemPosition() + 1) + "," + (spOgniGiorniSettimana.getSelectedItemPosition() + 1) + "," + etAlleOre.getText();
		}
		
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoSCHMBTEST, dati);
		int ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoSCHMBTEST, TIPO_COMANDO.SCRIVI, false);
		MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
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
							
							if(tempID == comandoSCHMBTEST)										//vado a capire quale comando è stato dato
							{	
								impostaDati(dati, 0);
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
						boolean erroriTemp = false;
						
						if(tempID == comandoSCHMBTEST)													//vado a capire quale comando è stato dato
							if((erroriTemp = MetodiComuni.TrovaErrori(tempRisposta)) == false)	//se non ci sono stati errori
							{
								impostaDati(dati, 1);
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
	
	private void impostaDati(List<String[]> dati, int pos)
	{
		//questo metodo mi serve per impostare i dati dei schedulatori, mi viene passata la lista di dati da mostrare all'utente, inoltre mi viene passaro un intero(pos)
		//che mi serve a capire su quale elemento della lista devo lavorare(il suo valore è 0 o 1)
		int i = 0;
		String parametro1 = dati.get(pos)[i++].substring(1, 2).toLowerCase();
		editor.putString("rgSche", parametro1);		
		
		if(parametro1.compareTo("n") == 0)
			i--;
		
		if(parametro1.compareTo("o") == 0)
		{
			editor.putString("etIlGiorno", dati.get(pos)[i + 1] + "/" + dati.get(pos)[i]);
			i += 2;
			editor.putString("etAlleOre", dati.get(pos)[i].substring(0, dati.get(pos)[i].indexOf('\'')));
		}
		
		if(parametro1.compareTo("d") == 0)
			editor.putString("etAlleOre", dati.get(pos)[i].substring(0, dati.get(pos)[i].indexOf('\'')));
		
		if(parametro1.compareTo("w") == 0)
		{
			if(dati.get(pos).length < 4)
				impostaMesi("");
			else
				impostaMesi(dati.get(pos)[i++]);

			editor.putInt("spOgniGiorniSettimana", Integer.parseInt(dati.get(pos)[i++]) - 1);
			editor.putString("etAlleOre", dati.get(pos)[i].substring(0, dati.get(pos)[i].indexOf('\'')));
		}
		
		if(parametro1.compareTo("m") == 0)
		{
			if(dati.get(pos).length < 4)
				impostaMesi("");
			else
				impostaMesi(dati.get(pos)[i++]);

			editor.putString("etDelGiorno", dati.get(pos)[i++]);
			editor.putString("etAlleOre", dati.get(pos)[i].substring(0, dati.get(pos)[i].indexOf('\'')));
		}
		
		if(parametro1.compareTo("e") == 0)
		{
			if(dati.get(pos).length < 5)
				impostaMesi("");
			else
				impostaMesi(dati.get(pos)[i++]);

			editor.putInt("spOgniNSettimane", Integer.parseInt(dati.get(pos)[i++]) - 1);
			editor.putInt("spOgniGiorniSettimana", Integer.parseInt(dati.get(pos)[i++]) - 1);
			editor.putString("etAlleOre", dati.get(pos)[i].substring(0, dati.get(pos)[i].indexOf('\'')));
		}
		
		editor.putString("tvInfo", dati.get(pos)[i].substring(dati.get(pos)[i].lastIndexOf('\'') + 1));
	}
	
	private void impostaMesi(String mesi)
	{
		//questo metodo mi serve per ceccare i vari checkBox dei mesi dei 2 schedulatori
		//il primo parametro contiene ovviamante i mesi (ES: 101110100001)
		//il secondo parametro mi serve per capire se devo lavorare sui mesi del schedulatore 1 o 2
		
		editor.putBoolean("cbTuttiIMesi", mesi == "");
		int i = 0;
		editor.putBoolean("cbGennaio", CondizioneMese(mesi, i++));
		editor.putBoolean("cbFebbraio", CondizioneMese(mesi, i++));
		editor.putBoolean("cbMarzo", CondizioneMese(mesi, i++));
		editor.putBoolean("cbAprile", CondizioneMese(mesi, i++));
		editor.putBoolean("cbMaggio", CondizioneMese(mesi, i++));
		editor.putBoolean("cbGiugno", CondizioneMese(mesi, i++));
		editor.putBoolean("cbLuglio", CondizioneMese(mesi, i++));
		editor.putBoolean("cbAgosto", CondizioneMese(mesi, i++));
		editor.putBoolean("cbSettembre", CondizioneMese(mesi, i++));
		editor.putBoolean("cbOttobre", CondizioneMese(mesi, i++));
		editor.putBoolean("cbNovembre", CondizioneMese(mesi, i++));
		editor.putBoolean("cbDicembre", CondizioneMese(mesi, i++));
	}
	
	private String ottieniMesiImpostati()
	{
		String mesi = "";
		if(cbTuttiIMesi.isChecked() == false)				//se il checkBox "Tutti i mesi" de chedulatore 1 non è ceccato
		{
			mesi += cbGennaio.isChecked() ? "1" : "0";
			mesi += cbFebbraio.isChecked() ? "1" : "0";
			mesi += cbMarzo.isChecked() ? "1" : "0";
			mesi += cbAprile.isChecked() ? "1" : "0";
			mesi += cbMaggio.isChecked() ? "1" : "0";
			mesi += cbGiugno.isChecked() ? "1" : "0";
			mesi += cbLuglio.isChecked() ? "1" : "0";
			mesi += cbAgosto.isChecked() ? "1" : "0";
			mesi += cbSettembre.isChecked() ? "1" : "0";
			mesi += cbOttobre.isChecked() ? "1" : "0";
			mesi += cbNovembre.isChecked() ? "1" : "0";
			mesi += cbDicembre.isChecked() ? "1" : "0";
		}
		
		if(mesi.indexOf('1') == -1)								//se la stringa che si è formata è "000000000000"
			mesi = "";
		
		return mesi;
	}
	
	private boolean CondizioneMese(String mesi, int pos)
	{
		//verifico che la lunghezza della stringa mesi sia uguale a 12, in quel caso vado a verificare che il carattere
		//in posizione "pos" sia uguale ad 1
		return mesi.length() == 12 && mesi.charAt(pos) == '1';
	}
	
	private void RendiTuttoInvisibile()
	{
		tvIlGiorno.setVisibility(View.GONE);
		etIlGiorno.setVisibility(View.GONE);
		tvOgniNSettimane.setVisibility(View.GONE);
		spOgniNSettimane.setVisibility(View.GONE);
		tvOgniGiorniSettimana.setVisibility(View.GONE);
		spOgniGiorniSettimana.setVisibility(View.GONE);
		tvDelGiorno.setVisibility(View.GONE);
		etDelGiorno.setVisibility(View.GONE);
		rlGiorno.setVisibility(View.GONE);
		rlMesi.setVisibility(View.GONE);
		tvDescrizioneDelGiorno.setVisibility(View.GONE);
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {					//entro qui dentro ogni volta che viene ceccato un radioButton
		switch (group.getCheckedRadioButtonId())
		{
			case R.id.SCHEALL_rb_Disabilitato:
				RendiTuttoInvisibile();
				break;
			case R.id.SCHEALL_rb_UnaSolaVolta:
				RendiTuttoInvisibile();
				tvIlGiorno.setVisibility(View.VISIBLE);
				etIlGiorno.setVisibility(View.VISIBLE);
				rlGiorno.setVisibility(View.VISIBLE);
				tvDescrizioneDelGiorno.setVisibility(View.VISIBLE);
				break;
			case R.id.SCHEALL_rb_Giornaliero:
				RendiTuttoInvisibile();
				rlGiorno.setVisibility(View.VISIBLE);
				break;
			case R.id.SCHEALL_rb_Settimanale:
				RendiTuttoInvisibile();
				tvOgniGiorniSettimana.setVisibility(View.VISIBLE);
				spOgniGiorniSettimana.setVisibility(View.VISIBLE);
				rlGiorno.setVisibility(View.VISIBLE);
				rlMesi.setVisibility(View.VISIBLE);
				break;
			case R.id.SCHEALL_rb_Mensile:
				RendiTuttoInvisibile();
				tvDelGiorno.setVisibility(View.VISIBLE);
				etDelGiorno.setVisibility(View.VISIBLE);
				rlGiorno.setVisibility(View.VISIBLE);
				rlMesi.setVisibility(View.VISIBLE);
				tvDescrizioneDelGiorno.setVisibility(View.VISIBLE);
				break;
			case R.id.SCHEALL_rb_MensileE:
				RendiTuttoInvisibile();
				tvOgniNSettimane.setVisibility(View.VISIBLE);
				spOgniNSettimane.setVisibility(View.VISIBLE);
				tvOgniGiorniSettimana.setVisibility(View.INVISIBLE);
				spOgniGiorniSettimana.setVisibility(View.VISIBLE);
				rlGiorno.setVisibility(View.VISIBLE);
				rlMesi.setVisibility(View.VISIBLE);
				break;
		}
		
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		if(buttonView == cbTuttiIMesi && isChecked)						//se  stato ceccato il checkBox "Tutti i mesi"
		{
			cbGennaio.setChecked(false);								//allora disabilito tutti gli altri mesi del
			cbFebbraio.setChecked(false);
			cbMarzo.setChecked(false);
			cbAprile.setChecked(false);
			cbMaggio.setChecked(false);
			cbGiugno.setChecked(false);
			cbLuglio.setChecked(false);
			cbAgosto.setChecked(false);
			cbSettembre.setChecked(false);
			cbOttobre.setChecked(false);
			cbNovembre.setChecked(false);
			cbDicembre.setChecked(false);
		}
		else
			if(buttonView.isChecked())									//altrimenti se è stato ceccato un qualsiasi checkBox del schedulatore
				cbTuttiIMesi.setChecked(false);							//allora disabilito il checkBox "Tutti i mesi" del schedulatore
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
