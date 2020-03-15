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

public class SchedulatoreLetture extends Activity implements OnRead, OnCheckedChangeListener, android.widget.CompoundButton.OnCheckedChangeListener {

	String id_activity, id_terminale;	//questo è un id che mi viene passato dal main_activity
	SharedPreferences preferences;		//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	static OnRead[] read = new OnRead[1];
	
	String tempRisposta, tempID, tempRispostaTemporanea;
	int tempRitorno, tempTipoComando;
	boolean tempComandiInCoda;
	int timeout;
	
	String comandoSCHMBLEGGI, comandoSCHMBLEGGI2;
	
	RadioGroup rgSche1;
	TextView tvSche1Info;
	RelativeLayout rlSche1Giorno, rlSche1Mesi;
	TextView tvSche1IlGiorno, tvSche1OgniNSettimane, tvSche1OgniGiorniSettimana, tvSche1DelGiorno, tvSche1DescrizioneDelGiorno;
	EditText etSche1AlleOre, etSche1IlGiorno, etSche1DelGiorno;
	Spinner spSche1OgniNSettimane, spSche1OgniGiorniSettimana;
	CheckBox cbSche1TuttiIMesi, cbSche1Gennaio, cbSche1Febbraio, cbSche1Marzo, cbSche1Aprile, cbSche1Maggio, cbSche1Giugno, cbSche1Luglio, cbSche1Agosto, cbSche1Settembre, cbSche1Ottobre, cbSche1Novembre, cbSche1Dicembre;
	
	RadioGroup rgSche2;
	TextView tvSche2Info;
	RelativeLayout rlSche2Giorno, rlSche2Mesi;
	TextView tvSche2IlGiorno, tvSche2OgniNSettimane, tvSche2OgniGiorniSettimana, tvSche2DelGiorno, tvSche2DescrizioneDelGiorno;
	EditText etSche2AlleOre, etSche2IlGiorno, etSche2DelGiorno;
	Spinner spSche2OgniNSettimane, spSche2OgniGiorniSettimana;
	CheckBox cbSche2TuttiIMesi, cbSche2Gennaio, cbSche2Febbraio, cbSche2Marzo, cbSche2Aprile, cbSche2Maggio, cbSche2Giugno, cbSche2Luglio, cbSche2Agosto, cbSche2Settembre, cbSche2Ottobre, cbSche2Novembre, cbSche2Dicembre;
	
	ProgressBar pbCaricamento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedulatore_letture);
		read[0] = this;
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		id_terminale = getIntent().getStringExtra("idTerminale");	
		
		timeout = getIntent().getIntExtra("timeout", 900);		//salvo il timeout che mi è stato passato dal main_activity
		
		comandoSCHMBLEGGI = "SCHMBLEGGI";
		comandoSCHMBLEGGI2 = "SCHMBLEGGI2";
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();
		
		OttieniPuntatoriView();
	}

	private void OttieniPuntatoriView()												//ottengo i puntatori ai vari View dell'intefaccia grafica
	{
		rgSche1 = ((RadioGroup)findViewById(R.id.SCHELETT_rg_Sche1));
		rgSche1.setOnCheckedChangeListener(this);
		
		tvSche1Info = ((TextView)findViewById(R.id.SCHELETT_tv_Sche1Info));
		
		rlSche1Giorno = ((RelativeLayout)findViewById(R.id.SCHELETT_RLayout_Sche1Giorno));
		rlSche1Mesi = ((RelativeLayout)findViewById(R.id.SCHELETT_RLayout_Sche1Mesi));
		
		tvSche1IlGiorno = ((TextView)findViewById(R.id.SCHELETT_tv_Sche1IlGiorno));
		tvSche1OgniNSettimane = ((TextView)findViewById(R.id.SCHELETT_tv_Sche1OgniNSettimane));
		tvSche1OgniGiorniSettimana = ((TextView)findViewById(R.id.SCHELETT_tv_Sche1GiorniSettimana));
		tvSche1DelGiorno = ((TextView)findViewById(R.id.SCHELETT_tv_Sche1DelGiorno));
		tvSche1DescrizioneDelGiorno = ((TextView)findViewById(R.id.SCHELETT_tv_Sche1DescrizioneDelGiorno));
		
		etSche1AlleOre = ((EditText)findViewById(R.id.SCHELETT_et_Sche1AlleOre));
		etSche1IlGiorno = ((EditText)findViewById(R.id.SCHELETT_et_Sche1IlGiorno));
		etSche1DelGiorno = ((EditText)findViewById(R.id.SCHELETT_et_Sche1DelGiorno));
		
		spSche1OgniNSettimane = ((Spinner)findViewById(R.id.SCHELETT_sp_Sche1NSettimane));
		spSche1OgniGiorniSettimana = ((Spinner)findViewById(R.id.SCHELETT_sp_Sche1GiorniSettimana));
		
		cbSche1TuttiIMesi = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1TuttiIMesi));
		cbSche1Gennaio = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Gennaio));
		cbSche1Febbraio = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Febbraio));
		cbSche1Marzo = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Marzo));
		cbSche1Aprile = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Aprile));
		cbSche1Maggio = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Maggio));
		cbSche1Giugno = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Giugno));
		cbSche1Luglio = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Luglio));
		cbSche1Agosto = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Agosto));
		cbSche1Settembre = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Settembre));
		cbSche1Ottobre = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Ottobre));
		cbSche1Novembre = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Novembre));
		cbSche1Dicembre = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche1Dicembre));
		
		cbSche1TuttiIMesi.setOnCheckedChangeListener(this);
		cbSche1Gennaio.setOnCheckedChangeListener(this);
		cbSche1Febbraio.setOnCheckedChangeListener(this);
		cbSche1Marzo.setOnCheckedChangeListener(this);
		cbSche1Aprile.setOnCheckedChangeListener(this);
		cbSche1Maggio.setOnCheckedChangeListener(this);
		cbSche1Giugno.setOnCheckedChangeListener(this);
		cbSche1Luglio.setOnCheckedChangeListener(this);
		cbSche1Agosto.setOnCheckedChangeListener(this);
		cbSche1Settembre.setOnCheckedChangeListener(this);
		cbSche1Ottobre.setOnCheckedChangeListener(this);
		cbSche1Novembre.setOnCheckedChangeListener(this);
		cbSche1Dicembre.setOnCheckedChangeListener(this);
		


		rgSche2 = ((RadioGroup)findViewById(R.id.SCHELETT_rg_Sche2));
		rgSche2.setOnCheckedChangeListener(this);		
		
		tvSche2Info = ((TextView)findViewById(R.id.SCHELETT_tv_Sche2Info));
		
		rlSche2Giorno = ((RelativeLayout)findViewById(R.id.SCHELETT_RLayout_Sche2Giorno));
		rlSche2Mesi = ((RelativeLayout)findViewById(R.id.SCHELETT_RLayout_Sche2Mesi));
		
		tvSche2IlGiorno = ((TextView)findViewById(R.id.SCHELETT_tv_Sche2IlGiorno));
		tvSche2OgniNSettimane = ((TextView)findViewById(R.id.SCHELETT_tv_Sche2OgniNSettimane));
		tvSche2OgniGiorniSettimana = ((TextView)findViewById(R.id.SCHELETT_tv_Sche2GiorniSettimana));
		tvSche2DelGiorno = ((TextView)findViewById(R.id.SCHELETT_tv_Sche2DelGiorno));
		
		etSche2AlleOre = ((EditText)findViewById(R.id.SCHELETT_et_Sche2AlleOre));
		etSche2IlGiorno = ((EditText)findViewById(R.id.SCHELETT_et_Sche2IlGiorno));
		etSche2DelGiorno = ((EditText)findViewById(R.id.SCHELETT_et_Sche2DelGiorno));
		tvSche2DescrizioneDelGiorno = ((TextView)findViewById(R.id.SCHELETT_tv_Sche2DescrizioneDelGiorno));
		
		spSche2OgniNSettimane = ((Spinner)findViewById(R.id.SCHELETT_sp_Sche2NSettimane));
		spSche2OgniGiorniSettimana = ((Spinner)findViewById(R.id.SCHELETT_sp_Sche2GiorniSettimana));
		
		cbSche2TuttiIMesi = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2TuttiIMesi));
		cbSche2Gennaio = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Gennaio));
		cbSche2Febbraio = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Febbraio));
		cbSche2Marzo = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Marzo));
		cbSche2Aprile = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Aprile));
		cbSche2Maggio = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Maggio));
		cbSche2Giugno = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Giugno));
		cbSche2Luglio = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Luglio));
		cbSche2Agosto = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Agosto));
		cbSche2Settembre = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Settembre));
		cbSche2Ottobre = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Ottobre));
		cbSche2Novembre = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Novembre));
		cbSche2Dicembre = ((CheckBox)findViewById(R.id.SCHELETT_cb_Sche2Dicembre));
		
		cbSche2TuttiIMesi.setOnCheckedChangeListener(this);
		cbSche2Gennaio.setOnCheckedChangeListener(this);
		cbSche2Febbraio.setOnCheckedChangeListener(this);
		cbSche2Marzo.setOnCheckedChangeListener(this);
		cbSche2Aprile.setOnCheckedChangeListener(this);
		cbSche2Maggio.setOnCheckedChangeListener(this);
		cbSche2Giugno.setOnCheckedChangeListener(this);
		cbSche2Luglio.setOnCheckedChangeListener(this);
		cbSche2Agosto.setOnCheckedChangeListener(this);
		cbSche2Settembre.setOnCheckedChangeListener(this);
		cbSche2Ottobre.setOnCheckedChangeListener(this);
		cbSche2Novembre.setOnCheckedChangeListener(this);
		cbSche2Dicembre.setOnCheckedChangeListener(this);
		
		pbCaricamento = ((ProgressBar)findViewById(R.id.SCHELETT_pb));
	}
	
	private void CaricaDati()
	{																				//vado a caricare i dati salvati
		for(int i = 0; i < rgSche1.getChildCount(); i++)
		{//vado ad impostare i radioButton dei 2 schedulatori(utilizzo i TAG dei radioButton che sono stati impostati dal codice XML)
			if(rgSche1.getChildAt(i).getTag().toString().compareTo(preferences.getString("rgSche1", "n")) == 0)
				((RadioButton)rgSche1.getChildAt(i)).setChecked(true);
			
			if(rgSche2.getChildAt(i).getTag().toString().compareTo(preferences.getString("rgSche2", "n")) == 0)
				((RadioButton)rgSche2.getChildAt(i)).setChecked(true);
		}
		
		tvSche1Info.setText(preferences.getString("tvSche1Info", ""));
		
		etSche1AlleOre.setText(preferences.getString("etSche1AlleOre", ""));
		etSche1IlGiorno.setText(preferences.getString("etSche1IlGiorno", ""));
		etSche1DelGiorno.setText(preferences.getString("etSche1DelGiorno", ""));
		
		spSche1OgniNSettimane.setSelection(preferences.getInt("spSche1OgniNSettimane", 0));
		spSche1OgniGiorniSettimana.setSelection(preferences.getInt("spSche1OgniGiorniSettimana", 0));
		
		cbSche1TuttiIMesi.setChecked(preferences.getBoolean("cbSche1TuttiIMesi", false));
		cbSche1Gennaio.setChecked(preferences.getBoolean("cbSche1Gennaio", false));
		cbSche1Febbraio.setChecked(preferences.getBoolean("cbSche1Febbraio", false));
		cbSche1Marzo.setChecked(preferences.getBoolean("cbSche1Marzo", false));
		cbSche1Aprile.setChecked(preferences.getBoolean("cbSche1Aprile", false));
		cbSche1Maggio.setChecked(preferences.getBoolean("cbSche1Maggio", false));
		cbSche1Giugno.setChecked(preferences.getBoolean("cbSche1Giugno", false));
		cbSche1Luglio.setChecked(preferences.getBoolean("cbSche1Luglio", false));
		cbSche1Agosto.setChecked(preferences.getBoolean("cbSche1Agosto", false));
		cbSche1Settembre.setChecked(preferences.getBoolean("cbSche1Settembre", false));
		cbSche1Ottobre.setChecked(preferences.getBoolean("cbSche1Ottobre", false));
		cbSche1Novembre.setChecked(preferences.getBoolean("cbSche1Novembre", false));
		cbSche1Dicembre.setChecked(preferences.getBoolean("cbSche1Dicembre", false));
		
		
		tvSche2Info.setText(preferences.getString("tvSche2Info", ""));
		
		etSche2AlleOre.setText(preferences.getString("etSche2AlleOre", ""));
		etSche2IlGiorno.setText(preferences.getString("etSche2IlGiorno", ""));
		etSche2DelGiorno.setText(preferences.getString("etSche2DelGiorno", ""));
		
		spSche2OgniNSettimane.setSelection(preferences.getInt("spSche2OgniNSettimane", 0));
		spSche2OgniGiorniSettimana.setSelection(preferences.getInt("spSche2OgniGiorniSettimana", 0));
		
		cbSche2TuttiIMesi.setChecked(preferences.getBoolean("cbSche2TuttiIMesi", false));
		cbSche2Gennaio.setChecked(preferences.getBoolean("cbSche2Gennaio", false));
		cbSche2Febbraio.setChecked(preferences.getBoolean("cbSche2Febbraio", false));
		cbSche2Marzo.setChecked(preferences.getBoolean("cbSche2Marzo", false));
		cbSche2Aprile.setChecked(preferences.getBoolean("cbSche2Aprile", false));
		cbSche2Maggio.setChecked(preferences.getBoolean("cbSche2Maggio", false));
		cbSche2Giugno.setChecked(preferences.getBoolean("cbSche2Giugno", false));
		cbSche2Luglio.setChecked(preferences.getBoolean("cbSche2Luglio", false));
		cbSche2Agosto.setChecked(preferences.getBoolean("cbSche2Agosto", false));
		cbSche2Settembre.setChecked(preferences.getBoolean("cbSche2Settembre", false));
		cbSche2Ottobre.setChecked(preferences.getBoolean("cbSche2Ottobre", false));
		cbSche2Novembre.setChecked(preferences.getBoolean("cbSche2Novembre", false));
		cbSche2Dicembre.setChecked(preferences.getBoolean("cbSche2Dicembre", false));
		
		pbCaricamento.setVisibility(preferences.getInt("pbCaricamento", View.GONE));
	}
	
	private void SalvaDati()
	{
		editor.putString("rgSche1", findViewById(rgSche1.getCheckedRadioButtonId()).getTag().toString());	//vado a salvare i dati
		
		editor.putString("tvSche1Info", tvSche1Info.getText().toString());
		
		editor.putString("etSche1AlleOre", etSche1AlleOre.getText().toString());
		editor.putString("etSche1IlGiorno", etSche1IlGiorno.getText().toString());
		editor.putString("etSche1DelGiorno", etSche1DelGiorno.getText().toString());
		
		editor.putInt("spSche1OgniNSettimane", spSche1OgniNSettimane.getSelectedItemPosition());
		editor.putInt("spSche1OgniGiorniSettimana", spSche1OgniGiorniSettimana.getSelectedItemPosition());
		
		editor.putBoolean("cbSche1TuttiIMesi", cbSche1TuttiIMesi.isChecked());
		editor.putBoolean("cbSche1Gennaio", cbSche1Gennaio.isChecked());
		editor.putBoolean("cbSche1Febbraio", cbSche1Febbraio.isChecked());
		editor.putBoolean("cbSche1Marzo", cbSche1Marzo.isChecked());
		editor.putBoolean("cbSche1Aprile", cbSche1Aprile.isChecked());
		editor.putBoolean("cbSche1Maggio", cbSche1Maggio.isChecked());
		editor.putBoolean("cbSche1Giugno", cbSche1Giugno.isChecked());
		editor.putBoolean("cbSche1Luglio", cbSche1Luglio.isChecked());
		editor.putBoolean("cbSche1Agosto", cbSche1Agosto.isChecked());
		editor.putBoolean("cbSche1Settembre", cbSche1Settembre.isChecked());
		editor.putBoolean("cbSche1Ottobre", cbSche1Ottobre.isChecked());
		editor.putBoolean("cbSche1Novembre", cbSche1Novembre.isChecked());
		editor.putBoolean("cbSche1Dicembre", cbSche1Dicembre.isChecked());
		
		
		editor.putString("rgSche2", findViewById(rgSche2.getCheckedRadioButtonId()).getTag().toString());
		
		editor.putString("tvSche2Info", tvSche2Info.getText().toString());
		
		editor.putString("etSche2AlleOre", etSche2AlleOre.getText().toString());
		editor.putString("etSche2IlGiorno", etSche2IlGiorno.getText().toString());
		editor.putString("etSche2DelGiorno", etSche2DelGiorno.getText().toString());
		
		editor.putInt("spSche2OgniNSettimane", spSche2OgniNSettimane.getSelectedItemPosition());
		editor.putInt("spSche2OgniGiorniSettimana", spSche2OgniGiorniSettimana.getSelectedItemPosition());
		
		editor.putBoolean("cbSche2TuttiIMesi", cbSche2TuttiIMesi.isChecked());
		editor.putBoolean("cbSche2Gennaio", cbSche2Gennaio.isChecked());
		editor.putBoolean("cbSche2Febbraio", cbSche2Febbraio.isChecked());
		editor.putBoolean("cbSche2Marzo", cbSche2Marzo.isChecked());
		editor.putBoolean("cbSche2Aprile", cbSche2Aprile.isChecked());
		editor.putBoolean("cbSche2Maggio", cbSche2Maggio.isChecked());
		editor.putBoolean("cbSche2Giugno", cbSche2Giugno.isChecked());
		editor.putBoolean("cbSche2Luglio", cbSche2Luglio.isChecked());
		editor.putBoolean("cbSche2Agosto", cbSche2Agosto.isChecked());
		editor.putBoolean("cbSche2Settembre", cbSche2Settembre.isChecked());
		editor.putBoolean("cbSche2Ottobre", cbSche2Ottobre.isChecked());
		editor.putBoolean("cbSche2Novembre", cbSche2Novembre.isChecked());
		editor.putBoolean("cbSche2Dicembre", cbSche2Dicembre.isChecked());
		
		editor.putInt("pbCaricamento", pbCaricamento.getVisibility());
		editor.commit();		//scrivo i dati
	}
	
	public void OnClickAggiornaSche1(View v)
	{
		int ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoSCHMBLEGGI).getBytes(), timeout, read, comandoSCHMBLEGGI, TIPO_COMANDO.LEGGI, v == null);															//se l'evento è stato sollevato dall'utente
		MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickAggiornaSche2(View v)
	{
		int ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoSCHMBLEGGI2).getBytes(), timeout, read, comandoSCHMBLEGGI2, TIPO_COMANDO.LEGGI, v == null);															//se l'evento è stato sollevato dall'utente
		MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaSche1(View v)
	{
		String dati = findViewById(rgSche1.getCheckedRadioButtonId()).getTag().toString();
		
		if(dati.compareTo("o") == 0)
		{
			try
			{
				String giorno =  etSche1IlGiorno.getText().toString();
				dati += "," + giorno.substring(giorno.indexOf('/') + 1) + "," + giorno.substring(0, giorno.indexOf('/')) + "," + etSche1AlleOre.getText();
			}
			catch(Exception e)
			{
				MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), "Inserire il giorno nel formato gg/mm");
				return;
			}
		}
		
		if(dati.compareTo("d") == 0)
			dati += "," + etSche1AlleOre.getText();
		
		if(dati.compareTo("w") == 0)
		{
			String mesi = ottieniMesiImpostatiSche1();
			if(mesi != "")
				dati += "," + mesi;
			
			dati += "," + (spSche1OgniGiorniSettimana.getSelectedItemPosition() + 1) + "," + etSche1AlleOre.getText();
		}
		
		if(dati.compareTo("m") == 0)
		{
			String mesi = ottieniMesiImpostatiSche1();
			if(mesi != "")
				dati += "," + mesi;
			
			dati += "," + etSche1DelGiorno.getText() + "," + etSche1AlleOre.getText();
		}
		
		if(dati.compareTo("e") == 0)
		{
			String mesi = ottieniMesiImpostatiSche1();
			if(mesi != "")
				dati += "," + mesi;
			
			dati += "," + (spSche1OgniNSettimane.getSelectedItemPosition() + 1) + "," + (spSche1OgniGiorniSettimana.getSelectedItemPosition() + 1) + "," + etSche1AlleOre.getText();
		}
		
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoSCHMBLEGGI, dati);
		int ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoSCHMBLEGGI, TIPO_COMANDO.SCRIVI, false);
		MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaSche2(View v)
	{
		String dati = findViewById(rgSche2.getCheckedRadioButtonId()).getTag().toString();
		
		if(dati.compareTo("o") == 0)
		{
			try
			{
				String giorno =  etSche2IlGiorno.getText().toString();
				dati += "," + giorno.substring(giorno.indexOf('/') + 1) + "," + giorno.substring(0, giorno.indexOf('/')) + "," + etSche2AlleOre.getText();
			}
			catch(Exception e)
			{
				MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), "Inserire il giorno nel formato gg/mm");
				return;
			}
		}
		
		if(dati.compareTo("d") == 0)
			dati += "," + etSche2AlleOre.getText();
		
		if(dati.compareTo("w") == 0)
		{
			String mesi = ottieniMesiImpostatiSche2();
			if(mesi != "")
				dati += "," + mesi;
			
			dati += "," + (spSche2OgniGiorniSettimana.getSelectedItemPosition() + 1) + "," + etSche2AlleOre.getText();
		}
		
		if(dati.compareTo("m") == 0)
		{
			String mesi = ottieniMesiImpostatiSche2();
			if(mesi != "")
				dati += "," + mesi;
			
			dati += "," + etSche2DelGiorno.getText() + "," + etSche2AlleOre.getText();
		}
		
		if(dati.compareTo("e") == 0)
		{
			String mesi = ottieniMesiImpostatiSche2();
			if(mesi != "")
				dati += "," + mesi;
			
			dati += "," + (spSche2OgniNSettimane.getSelectedItemPosition() + 1) + "," + (spSche2OgniGiorniSettimana.getSelectedItemPosition() + 1) + "," + etSche2AlleOre.getText();
		}
		
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoSCHMBLEGGI2, dati);
		int ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoSCHMBLEGGI2, TIPO_COMANDO.SCRIVI, false);
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
							
							if(tempID == comandoSCHMBLEGGI)								//vado a capire quale comando è stato dato
							{	
								impostaDatiSche1(dati, 0);
							}
							
							if(tempID == comandoSCHMBLEGGI2)								//vado a capire quale comando è stato dato
							{	
								impostaDatiSche2(dati, 0);
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
						
						if(tempID == comandoSCHMBLEGGI)											//vado a capire quale comando è stato dato
							if((erroriTemp = MetodiComuni.TrovaErrori(tempRisposta)) == false)	//se non ci sono stati errori
							{
								impostaDatiSche1(dati, 1);
							}
							else
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
							
						if(tempID == comandoSCHMBLEGGI2)								//vado a capire quale comando è stato dato
							if((erroriTemp = MetodiComuni.TrovaErrori(tempRisposta)) == false)	//se non ci sono stati errori
							{
								impostaDatiSche2(dati, 1);
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
	
	private void impostaDatiSche1(List<String[]> dati, int pos)
	{
		impostaDatiSche(dati, pos, "1");
	}
	
	private void impostaDatiSche2(List<String[]> dati, int pos)
	{
		impostaDatiSche(dati, pos, "2");
	}
	
	private void impostaDatiSche(List<String[]> dati, int pos, String Sche1Sche2)
	{
		//questo metodo mi serve per impostare i dati dei schedulatori, mi viene passata la lista di dati da mostrare all'utente, inoltre mi viene passaro un intero(pos)
		//che mi serve a capire su quale elemento della lista devo lavorare(il suo valore è 0 o 1)
		//l'ultimo parametro passatomi, serve per capire se devo impostare i dati del schedulatore 1 o 2
		int i = 0;
		String parametro1 = dati.get(pos)[i++].substring(1, 2).toLowerCase();
		editor.putString("rgSche1".replace("1", Sche1Sche2), parametro1);		
		
		if(parametro1.compareTo("n") == 0)
			i--;
		
		if(parametro1.compareTo("o") == 0)
		{
			editor.putString("etSche1IlGiorno".replace("1", Sche1Sche2), dati.get(pos)[i + 1] + "/" + dati.get(pos)[i]);
			i += 2;
			editor.putString("etSche1AlleOre".replace("1", Sche1Sche2), dati.get(pos)[i].substring(0, dati.get(pos)[i].indexOf('\'')));
		}
		
		if(parametro1.compareTo("d") == 0)
			editor.putString("etSche1AlleOre".replace("1", Sche1Sche2), dati.get(pos)[i].substring(0, dati.get(pos)[i].indexOf('\'')));
		
		if(parametro1.compareTo("w") == 0)
		{
			if(dati.get(pos).length < 4)
				impostaMesiSche("", Sche1Sche2);
			else
				impostaMesiSche(dati.get(pos)[i++], Sche1Sche2);

			editor.putInt("spSche1OgniGiorniSettimana".replace("1", Sche1Sche2), Integer.parseInt(dati.get(pos)[i++]) - 1);
			editor.putString("etSche1AlleOre".replace("1", Sche1Sche2), dati.get(pos)[i].substring(0, dati.get(pos)[i].indexOf('\'')));
		}
		
		if(parametro1.compareTo("m") == 0)
		{
			if(dati.get(pos).length < 4)
				impostaMesiSche("", Sche1Sche2);
			else
				impostaMesiSche(dati.get(pos)[i++], Sche1Sche2);

			editor.putString("etSche1DelGiorno".replace("1", Sche1Sche2), dati.get(pos)[i++]);
			editor.putString("etSche1AlleOre".replace("1", Sche1Sche2), dati.get(pos)[i].substring(0, dati.get(pos)[i].indexOf('\'')));
		}
		
		if(parametro1.compareTo("e") == 0)
		{
			if(dati.get(pos).length < 5)
				impostaMesiSche("", Sche1Sche2);
			else
				impostaMesiSche(dati.get(pos)[i++], Sche1Sche2);

			editor.putInt("spSche1OgniNSettimane".replace("1", Sche1Sche2), Integer.parseInt(dati.get(pos)[i++]) - 1);
			editor.putInt("spSche1OgniGiorniSettimana".replace("1", Sche1Sche2), Integer.parseInt(dati.get(pos)[i++]) - 1);
			editor.putString("etSche1AlleOre".replace("1", Sche1Sche2), dati.get(pos)[i].substring(0, dati.get(pos)[i].indexOf('\'')));
		}
		
		editor.putString("tvSche1Info".replace("1", Sche1Sche2), dati.get(pos)[i].substring(dati.get(pos)[i].lastIndexOf('\'') + 1));
	}
	
	private void impostaMesiSche(String mesi, String Sche1Sche2)
	{
		//questo metodo mi serve per ceccare i vari checkBox dei mesi dei 2 schedulatori
		//il primo parametro contiene ovviamante i mesi (ES: 101110100001)
		//il secondo parametro mi serve per capire se devo lavorare sui mesi del schedulatore 1 o 2
		
		editor.putBoolean("cbSche1TuttiIMesi".replace("1", Sche1Sche2), mesi == "");
		int i = 0;
		editor.putBoolean("cbSche1Gennaio".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Febbraio".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Marzo".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Aprile".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Maggio".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Giugno".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Luglio".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Agosto".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Settembre".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Ottobre".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Novembre".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
		editor.putBoolean("cbSche1Dicembre".replace("1", Sche1Sche2), CondizioneMese(mesi, i++));
	}
	
	private String ottieniMesiImpostatiSche1()
	{
		String mesi = "";
		if(cbSche1TuttiIMesi.isChecked() == false)				//se il checkBox "Tutti i mesi" de chedulatore 1 non è ceccato
		{
			mesi += cbSche1Gennaio.isChecked() ? "1" : "0";
			mesi += cbSche1Febbraio.isChecked() ? "1" : "0";
			mesi += cbSche1Marzo.isChecked() ? "1" : "0";
			mesi += cbSche1Aprile.isChecked() ? "1" : "0";
			mesi += cbSche1Maggio.isChecked() ? "1" : "0";
			mesi += cbSche1Giugno.isChecked() ? "1" : "0";
			mesi += cbSche1Luglio.isChecked() ? "1" : "0";
			mesi += cbSche1Agosto.isChecked() ? "1" : "0";
			mesi += cbSche1Settembre.isChecked() ? "1" : "0";
			mesi += cbSche1Ottobre.isChecked() ? "1" : "0";
			mesi += cbSche1Novembre.isChecked() ? "1" : "0";
			mesi += cbSche1Dicembre.isChecked() ? "1" : "0";
		}
		
		if(mesi.indexOf('1') == -1)								//se la stringa che si è formata è "000000000000"
			mesi = "";
		
		return mesi;
	}
	
	private String ottieniMesiImpostatiSche2()
	{
		String mesi = "";
		if(cbSche2TuttiIMesi.isChecked() == false)				//se il checkBoc "Tutti i mesi" de chedulatore 2 non è ceccato
		{
			mesi += cbSche2Gennaio.isChecked() ? "1" : "0";
			mesi += cbSche2Febbraio.isChecked() ? "1" : "0";
			mesi += cbSche2Marzo.isChecked() ? "1" : "0";
			mesi += cbSche2Aprile.isChecked() ? "1" : "0";
			mesi += cbSche2Maggio.isChecked() ? "1" : "0";
			mesi += cbSche2Giugno.isChecked() ? "1" : "0";
			mesi += cbSche2Luglio.isChecked() ? "1" : "0";
			mesi += cbSche2Agosto.isChecked() ? "1" : "0";
			mesi += cbSche2Settembre.isChecked() ? "1" : "0";
			mesi += cbSche2Ottobre.isChecked() ? "1" : "0";
			mesi += cbSche2Novembre.isChecked() ? "1" : "0";
			mesi += cbSche2Dicembre.isChecked() ? "1" : "0";
		}
		
		if(mesi.indexOf('1') == -1)							//se la stringa che si è formata è "000000000000"
			mesi = "";
		
		return mesi;
	}
	
	private boolean CondizioneMese(String mesi, int pos)
	{
		//verifico che la lunghezza della stringa mesi sia uguale a 12, in quel caso vado a verificare che il carattere
		//in posizione "pos" sia uguale ad 1
		return mesi.length() == 12 && mesi.charAt(pos) == '1';
	}
	
	private void RendiTuttoInvisibileSche1()
	{
		tvSche1IlGiorno.setVisibility(View.GONE);
		etSche1IlGiorno.setVisibility(View.GONE);
		tvSche1OgniNSettimane.setVisibility(View.GONE);
		spSche1OgniNSettimane.setVisibility(View.GONE);
		tvSche1OgniGiorniSettimana.setVisibility(View.GONE);
		spSche1OgniGiorniSettimana.setVisibility(View.GONE);
		tvSche1DelGiorno.setVisibility(View.GONE);
		etSche1DelGiorno.setVisibility(View.GONE);
		rlSche1Giorno.setVisibility(View.GONE);
		rlSche1Mesi.setVisibility(View.GONE);
		tvSche1DescrizioneDelGiorno.setVisibility(View.GONE);
	}
	
	private void RendiTuttoInvisibileSche2()
	{
		tvSche2IlGiorno.setVisibility(View.GONE);
		etSche2IlGiorno.setVisibility(View.GONE);
		tvSche2OgniNSettimane.setVisibility(View.GONE);
		spSche2OgniNSettimane.setVisibility(View.GONE);
		tvSche2OgniGiorniSettimana.setVisibility(View.GONE);
		spSche2OgniGiorniSettimana.setVisibility(View.GONE);
		tvSche2DelGiorno.setVisibility(View.GONE);
		etSche2DelGiorno.setVisibility(View.GONE);
		rlSche2Giorno.setVisibility(View.GONE);
		rlSche2Mesi.setVisibility(View.GONE);
		tvSche2DescrizioneDelGiorno.setVisibility(View.GONE);
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {					//entro qui dentro ogni volta che viene ceccato un radioButton
			switch (group.getCheckedRadioButtonId())
			{
				case R.id.SCHELETT_rb_Sche1Disabilitato:
					RendiTuttoInvisibileSche1();
					break;
				case R.id.SCHELETT_rb_Sche1UnaSolaVolta:
					RendiTuttoInvisibileSche1();
					tvSche1IlGiorno.setVisibility(View.VISIBLE);
					etSche1IlGiorno.setVisibility(View.VISIBLE);
					rlSche1Giorno.setVisibility(View.VISIBLE);
					tvSche1DescrizioneDelGiorno.setVisibility(View.VISIBLE);
					break;
				case R.id.SCHELETT_rb_Sche1Giornaliero:
					RendiTuttoInvisibileSche1();
					rlSche1Giorno.setVisibility(View.VISIBLE);
					break;
				case R.id.SCHELETT_rb_Sche1Settimanale:
					RendiTuttoInvisibileSche1();
					tvSche1OgniGiorniSettimana.setVisibility(View.VISIBLE);
					spSche1OgniGiorniSettimana.setVisibility(View.VISIBLE);
					rlSche1Giorno.setVisibility(View.VISIBLE);
					rlSche1Mesi.setVisibility(View.VISIBLE);
					break;
				case R.id.SCHELETT_rb_Sche1Mensile:
					RendiTuttoInvisibileSche1();
					tvSche1DelGiorno.setVisibility(View.VISIBLE);
					etSche1DelGiorno.setVisibility(View.VISIBLE);
					rlSche1Giorno.setVisibility(View.VISIBLE);
					rlSche1Mesi.setVisibility(View.VISIBLE);
					tvSche1DescrizioneDelGiorno.setVisibility(View.VISIBLE);
					break;
				case R.id.SCHELETT_rb_Sche1MensileE:
					RendiTuttoInvisibileSche1();
					tvSche1OgniNSettimane.setVisibility(View.VISIBLE);
					spSche1OgniNSettimane.setVisibility(View.VISIBLE);
					tvSche1OgniGiorniSettimana.setVisibility(View.INVISIBLE);
					spSche1OgniGiorniSettimana.setVisibility(View.VISIBLE);
					rlSche1Giorno.setVisibility(View.VISIBLE);
					rlSche1Mesi.setVisibility(View.VISIBLE);
					break;
				case R.id.SCHELETT_rb_Sche2Disabilitato:
					RendiTuttoInvisibileSche2();
					break;
				case R.id.SCHELETT_rb_Sche2UnaSolaVolta:
					RendiTuttoInvisibileSche2();
					tvSche2IlGiorno.setVisibility(View.VISIBLE);
					etSche2IlGiorno.setVisibility(View.VISIBLE);
					rlSche2Giorno.setVisibility(View.VISIBLE);
					tvSche2DescrizioneDelGiorno.setVisibility(View.VISIBLE);
					break;
				case R.id.SCHELETT_rb_Sche2Giornaliero:
					RendiTuttoInvisibileSche2();
					rlSche2Giorno.setVisibility(View.VISIBLE);
					break;
				case R.id.SCHELETT_rb_Sche2Settimanale:
					RendiTuttoInvisibileSche2();
					tvSche2OgniGiorniSettimana.setVisibility(View.VISIBLE);
					spSche2OgniGiorniSettimana.setVisibility(View.VISIBLE);
					rlSche2Giorno.setVisibility(View.VISIBLE);
					rlSche2Mesi.setVisibility(View.VISIBLE);
					break;
				case R.id.SCHELETT_rb_Sche2Mensile:
					RendiTuttoInvisibileSche2();
					tvSche2DelGiorno.setVisibility(View.VISIBLE);
					etSche2DelGiorno.setVisibility(View.VISIBLE);
					rlSche2Giorno.setVisibility(View.VISIBLE);
					rlSche2Mesi.setVisibility(View.VISIBLE);
					tvSche2DescrizioneDelGiorno.setVisibility(View.VISIBLE);
					break;
				case R.id.SCHELETT_rb_Sche2MensileE:
					RendiTuttoInvisibileSche2();
					tvSche2OgniNSettimane.setVisibility(View.VISIBLE);
					spSche2OgniNSettimane.setVisibility(View.VISIBLE);
					tvSche2OgniGiorniSettimana.setVisibility(View.INVISIBLE);
					spSche2OgniGiorniSettimana.setVisibility(View.VISIBLE);
					rlSche2Giorno.setVisibility(View.VISIBLE);
					rlSche2Mesi.setVisibility(View.VISIBLE);
					break;
			}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView == cbSche1TuttiIMesi && isChecked)		//se  stato ceccato il checkBox "Tutti i mesi" del schedulatore 1
		{
			cbSche1Gennaio.setChecked(false);					//allora disabilito tutti gli altri mesi del schedulatore 1
			cbSche1Febbraio.setChecked(false);
			cbSche1Marzo.setChecked(false);
			cbSche1Aprile.setChecked(false);
			cbSche1Maggio.setChecked(false);
			cbSche1Giugno.setChecked(false);
			cbSche1Luglio.setChecked(false);
			cbSche1Agosto.setChecked(false);
			cbSche1Settembre.setChecked(false);
			cbSche1Ottobre.setChecked(false);
			cbSche1Novembre.setChecked(false);
			cbSche1Dicembre.setChecked(false);
		}
		else
			if(buttonView == cbSche2TuttiIMesi && isChecked)		//altrimenti se  stato ceccato il checkBox "Tutti i mesi" del schedulatore 2
			{
				cbSche2Gennaio.setChecked(false);					//allora disabilito tutti gli altri mesi del schedulatore 2
				cbSche2Febbraio.setChecked(false);
				cbSche2Marzo.setChecked(false);
				cbSche2Aprile.setChecked(false);
				cbSche2Maggio.setChecked(false);
				cbSche2Giugno.setChecked(false);
				cbSche2Luglio.setChecked(false);
				cbSche2Agosto.setChecked(false);
				cbSche2Settembre.setChecked(false);
				cbSche2Ottobre.setChecked(false);
				cbSche2Novembre.setChecked(false);
				cbSche2Dicembre.setChecked(false);
			}
			else
				if((RelativeLayout)buttonView.getParent() == rlSche1Mesi && buttonView.isChecked())	//altrimenti se è stato ceccato un qualsiasi checkBox del schedulatore 1
					cbSche1TuttiIMesi.setChecked(false);											//allora disabilito il checkBox "Tutti i mesi" del schedulatore 1
				else
					if((RelativeLayout)buttonView.getParent() == rlSche2Mesi && buttonView.isChecked())//altrimenti se è stato ceccato un qualsiasi checkBox del schedulatore 2
						cbSche2TuttiIMesi.setChecked(false);											//allora disabilito il checkBox "Tutti i mesi" del schedulatore 2
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
