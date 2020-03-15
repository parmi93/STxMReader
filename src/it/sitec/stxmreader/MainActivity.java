package it.sitec.stxmreader;

import STCM.DEVICE;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

public class MainActivity extends Activity{
	
	GridView gridview;
	Intent intento;
	static final String[] id_activity = {"connetti", "info", "gprs", "smtp", "io", "telefoni", "schedulatori_letture", "schedulatore_allarmi", "comandi", "lista_dispositivi", "terminale"};
	static int TIMEOUT;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gridview = (GridView) findViewById(R.id.MAIN_gw_Menu);
	    gridview.setAdapter(new CreaBottoni(this));
	}

	public void click(View v)
	{
		Button btn = (Button)v;
		switch (btn.getId())
		{
			case 0:
				intento = new Intent(this, Connetti.class);
				intento.putExtra("id", id_activity[0]);
				intento.putExtra("idTerminale", id_activity[10]);
				startActivity(intento);
				break;
			case 1:
				intento = new Intent(this, Info_dispositivo.class);
				intento.putExtra("id", id_activity[1]);
				intento.putExtra("idTerminale", id_activity[10]);
				intento.putExtra("timeout", TIMEOUT);
				startActivity(intento);
				break;
			case 2:
				intento = new Intent(this, GPRS.class);
				intento.putExtra("id", id_activity[2]);
				intento.putExtra("idTerminale", id_activity[10]);
				intento.putExtra("timeout", TIMEOUT);
				startActivity(intento);
				break;
			case 3:
				intento = new Intent(this, SMTP.class);
				intento.putExtra("id", id_activity[3]);
				intento.putExtra("idTerminale", id_activity[10]);
				intento.putExtra("timeout", TIMEOUT);
				startActivity(intento);
				break;
			case 4:
				intento = new Intent(this, InputOutput.class);
				intento.putExtra("id", id_activity[4]);
				intento.putExtra("idTerminale", id_activity[10]);
				intento.putExtra("timeout", TIMEOUT);
				startActivity(intento);
				break;
			case 5:
				intento = new Intent(this, Telefoni.class);
				intento.putExtra("id", id_activity[5]);
				intento.putExtra("idTerminale", id_activity[10]);
				intento.putExtra("timeout", TIMEOUT);
				startActivity(intento);
				break;
			case 6:
				intento = new Intent(this, SchedulatoreLetture.class);
				intento.putExtra("id", id_activity[6]);
				intento.putExtra("idTerminale", id_activity[10]);
				intento.putExtra("timeout", TIMEOUT);
				startActivity(intento);
				break;
			case 7:
				intento = new Intent(this, SchedulatoreAllarmi.class);
				intento.putExtra("id", id_activity[7]);
				intento.putExtra("idTerminale", id_activity[10]);
				intento.putExtra("timeout", TIMEOUT);
				startActivity(intento);
				break;
			case 8:
				intento = new Intent(this, Comandi.class);
				intento.putExtra("id", id_activity[8]);
				intento.putExtra("idTerminale", id_activity[10]);
				intento.putExtra("timeout", TIMEOUT);
				startActivity(intento);
				break;
			case 9:
				intento = new Intent(this, ListaDispositivi.class);
				intento.putExtra("id", id_activity[9]);
				intento.putExtra("idTerminale", id_activity[10]);
				intento.putExtra("timeout", TIMEOUT);
				startActivity(intento);
				break;
			case 10:
				intento = new Intent(this, Terminale.class);
				intento.putExtra("id", id_activity[10]);
				intento.putExtra("timeout", TIMEOUT);
				startActivity(intento);
				break;
			case 11:
				intento = new Intent(this, InformazioniGenerali.class);
				startActivity(intento);
				break;
		}
	}
	
	@Override
	public void finish() {
		super.finish();		
		super.onDestroy();
		
		svuota_memoria();
		DEVICE.Disconnetti();
		ListaDispositivi.listaDispositivi.clear();
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences preferences = getSharedPreferences(id_activity[0], 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
	    TIMEOUT = preferences.getInt("timeout", 900);						//mi salvo il timeout che è stato impostato nell'activity connetti
	}

	public void svuota_memoria()
	{
		SharedPreferences preferences;
		SharedPreferences.Editor editor;
		
		for(int i = 0; i < id_activity.length; i++)
		{
			preferences = getSharedPreferences(id_activity[i], 0);
			editor = preferences.edit();
			editor.clear().commit();
		}
	}
}
