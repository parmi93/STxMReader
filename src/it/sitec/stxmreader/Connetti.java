package it.sitec.stxmreader;

import java.util.List;

import STCM.DEVICE;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class Connetti extends Activity implements OnCheckedChangeListener{

	static final String ACTION_USB_PERMISSION = "it.sitec.stxmreader.USB_PERMISSION";

	String id_activity;					//questo è un id che mi viene passato dal main_activity
	SharedPreferences preferences;		//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	
	ScrollView svListaDeviceAssociati;
	RelativeLayout rlUSB, rlRemoto, rlBluetooth;
	TextView tvStatoUSB, tvIdProduttore, tvIdVenditore, tvBaudrate;
	TextView tvStatoRemoto, tvIp, tvPorta;
	TextView tvStatoBluetooth, tvNomeDevice, tvMacAddress;
	EditText etIp, etPorta;
	Button btnConnettiUSB, btnConnettitiRemoto, btnConnettitiBluetooth;
	Spinner spnBaudrate, spTimeout;
	RadioGroup rgTipoConnessione, rgListaDispositiviAssociati;
	RadioButton rbUSB, rbRemoto, rbBluetooth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connetti);
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();							//mi serve per modificare i dati
		
		OttieniPuntatoriView();
	}
	
	private void OttieniPuntatoriView()
	{
		svListaDeviceAssociati = (ScrollView)findViewById(R.id.CONN_ScrollView_BLUETOOTHListaDeviceAssociati);
		svListaDeviceAssociati.setOnTouchListener(new View.OnTouchListener() {		//mi serve per poter riuscire ad fare un scroll della lista dei device associati
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		
		rlUSB = ((RelativeLayout)findViewById(R.id.CONN_RLayout_USB));
		rlRemoto = ((RelativeLayout)findViewById(R.id.CONN_RLayout_REMOTO));
		rlBluetooth = ((RelativeLayout)findViewById(R.id.CONN_RLayout_Bluetooth));
		
		spTimeout = ((Spinner)findViewById(R.id.CONN_sp_Timeout));
		
		rgTipoConnessione = ((RadioGroup)findViewById(R.id.CONN_rg_TipoConnessione));
		rgTipoConnessione.setOnCheckedChangeListener(this);
		rgListaDispositiviAssociati = ((RadioGroup)findViewById(R.id.CONN_rg_BLUETOOTHDeviceAssociati));
		
		rbUSB = ((RadioButton)findViewById(R.id.CONN_rb_Seriale));
		rbRemoto = ((RadioButton)findViewById(R.id.CONN_rb_Ip));
		rbBluetooth = ((RadioButton)findViewById(R.id.CONN_rb_Bluetooth));
		
		tvStatoUSB = ((TextView)findViewById(R.id.CONN_tv_USBStato));
		tvIdProduttore = ((TextView)findViewById(R.id.CONN_tv_USBIdProduttore_DATO));
		tvIdVenditore = ((TextView)findViewById(R.id.CONN_tv_USBIdVenditore_DATO));
		tvBaudrate = ((TextView)findViewById(R.id.CONN_tv_USBBaudrateInfoDevice_DATO));
		
		btnConnettiUSB = (Button)findViewById(R.id.CONN_btn_USBConnetti);
		spnBaudrate = ((Spinner)findViewById(R.id.CONN_sp_USBBaudrate));
		
		etIp = ((EditText)findViewById(R.id.CONN_et_REMOTOIp));
		etPorta = ((EditText)findViewById(R.id.CONN_et_REMOTOPorta));
		btnConnettitiRemoto = (Button)findViewById(R.id.CONN_btn_REMOTOConnettiti);
		tvStatoRemoto = ((TextView)findViewById(R.id.CONN_tv_REMOTOStato));
		tvIp = ((TextView)findViewById(R.id.CONN_tv_REMOTOInfoIp_DATO));
		tvPorta = ((TextView)findViewById(R.id.CONN_tv_REMOTOInfoPorta_DATO));
		
		btnConnettitiBluetooth = (Button)findViewById(R.id.CONN_btn_BLUETOOTHConnettiti);
		tvStatoBluetooth = ((TextView)findViewById(R.id.CONN_tv_BLUETOOTHStato));
		tvNomeDevice = ((TextView)findViewById(R.id.CONN_tv_BLUETOOTHNomeDevice_DATO));
		tvMacAddress = ((TextView)findViewById(R.id.CONN_tv_BLUETOOTHMacAddress_DATO));
	}
	
	private void CaricaDati()	
	{
		rbUSB.setChecked(preferences.getBoolean("rbUSB", true));
		rbRemoto.setChecked(preferences.getBoolean("rbRemoto", false));
		rbBluetooth.setChecked(preferences.getBoolean("rbBluetooth", false));
		
		spTimeout.setSelection((preferences.getInt("timeout", 900) / 300) - 2);
		
		tvStatoUSB.setText(preferences.getString("tvStatoUSB", tvStatoUSB.getText().toString()));	//carico i dati dal file
		tvStatoUSB.setTextColor(preferences.getInt("tvStatoUSBColor", Color.BLACK));
		tvIdProduttore.setText(preferences.getString("tvIdProduttore", tvIdProduttore.getText().toString()));
		tvIdVenditore.setText(preferences.getString("tvIdVenditore", tvIdVenditore.getText().toString()));
		tvBaudrate.setText(preferences.getString("tvBaudrate", tvBaudrate.getText().toString()));
		btnConnettiUSB.setText(preferences.getString("btnConnettiUSB", btnConnettiUSB.getText().toString()));
		spnBaudrate.setSelection(preferences.getInt("spnBaudrate", 6));


		etIp.setText(preferences.getString("etIp", etIp.getText().toString()));
		etPorta.setText(preferences.getString("etPorta", etPorta.getText().toString()));
		btnConnettitiRemoto.setText(preferences.getString("btnConnettitiRemoto", btnConnettitiRemoto.getText().toString()));
		tvStatoRemoto.setText(preferences.getString("tvStatoRemoto", tvStatoRemoto.getText().toString()));
		tvStatoRemoto.setTextColor(preferences.getInt("tvStatoRemotoColor", Color.BLACK));
		tvIp.setText(preferences.getString("tvIp", tvIp.getText().toString()));
		tvPorta.setText(preferences.getString("tvPorta", tvPorta.getText().toString()));
		
		btnConnettitiBluetooth.setText(preferences.getString("btnConnettitiBluetooth", btnConnettitiRemoto.getText().toString()));
		tvStatoBluetooth.setText(preferences.getString("tvStatoBluetooth", tvStatoBluetooth.getText().toString()));
		tvStatoBluetooth.setTextColor(preferences.getInt("tvStatoBluetoothColor", Color.BLACK));
		tvNomeDevice.setText(preferences.getString("tvNomeDevice", tvNomeDevice.getText().toString()));
		tvMacAddress.setText(preferences.getString("tvMacAddress", tvMacAddress.getText().toString()));
	}
	
	private void SalvaDati()
	{
		editor.putBoolean("rbUSB", rbUSB.isChecked());
		editor.putBoolean("rbRemoto", rbRemoto.isChecked());
		editor.putBoolean("rbBluetooth", rbBluetooth.isChecked());
		
		editor.putInt("timeout", Integer.parseInt(spTimeout.getSelectedItem().toString()));
		
		editor.putString("tvStatoUSB", tvStatoUSB.getText().toString());	//salvo i dati sul file
		editor.putInt("tvStatoUSBColor", tvStatoUSB.getCurrentTextColor());
		editor.putString("tvIdProduttore", tvIdProduttore.getText().toString());
		editor.putString("tvIdVenditore", tvIdVenditore.getText().toString());
		editor.putString("tvBaudrate", tvBaudrate.getText().toString());
		editor.putString("btnConnettiUSB", btnConnettiUSB.getText().toString());
		editor.putInt("spnBaudrate", spnBaudrate.getSelectedItemPosition());
		
		editor.putString("etIp", etIp.getText().toString());
		editor.putString("etPorta", etPorta.getText().toString());
		editor.putString("btnConnettitiRemoto", btnConnettitiRemoto.getText().toString());
		editor.putString("tvStatoRemoto", tvStatoRemoto.getText().toString());
		editor.putInt("tvStatoRemotoColor", tvStatoRemoto.getCurrentTextColor());
		editor.putString("tvIp", tvIp.getText().toString());
		editor.putString("tvPorta", tvPorta.getText().toString());
		
		editor.putString("btnConnettitiBluetooth", btnConnettitiBluetooth.getText().toString());
		editor.putString("tvStatoBluetooth", tvStatoBluetooth.getText().toString());
		editor.putInt("tvStatoBluetoothColor", tvStatoBluetooth.getCurrentTextColor());
		editor.putString("tvNomeDevice", tvNomeDevice.getText().toString());
		editor.putString("tvMacAddress", tvMacAddress.getText().toString());
		editor.commit();					//scrivo i dati su file
	}
	
	public void ClickConnettiUSB(View v)
	{		
		if(DEVICE.Stato() == true)						//se sono connesso
			if(DEVICE.TipoConnessione() == 1)			//tramite ip
				ClickConnettiRemoto(null);				//allora mi disconetto dall'ip
			else
				if(DEVICE.TipoConnessione() == 2)		//altrimenti se sono connesso tramite bluetooth
					ClickConnettiBluetooth(null);		//allora mi disconetto dal bluetooth
			
		
		PendingIntent permesso = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);	//permessi USB
		UsbManager manager = (UsbManager)getSystemService(Context.USB_SERVICE); 		//ottengo il servizio USB di android
		int baudrate = Integer.parseInt(spnBaudrate.getSelectedItem().toString());		//ottengo il Baudrate
		
		if(DEVICE.Stato() == false)									//se non sono connesso al device
		{
			if(DEVICE.ConnettiUSB(manager, permesso, baudrate))		//se riesco a connettermi al device
			{
				tvStatoUSB.setText(R.string.connessione_stabilita);
				tvStatoUSB.setTextColor(Color.GREEN);
				tvIdProduttore.setText(DEVICE.IdProduttore());
				tvIdVenditore.setText(DEVICE.IdVenditore());
				tvBaudrate.setText(baudrate+"");
				btnConnettiUSB.setText(R.string.disconnettiti);
			}
			else													//se non riesco a connettermi al device
			{
				tvStatoUSB.setText(R.string.impossibile_stabilire_una_connessione_usb);
				tvStatoUSB.setTextColor(Color.RED);
			}
		}
		else									//altrimenti sono già connesso al device
		{
			DEVICE.Disconnetti();				//quindi mi disconnetto dal device
			tvStatoUSB.setText(R.string.disconnesso);
			tvStatoUSB.setTextColor(Color.BLACK);
			tvIdProduttore.setText(R.string.vuoto);
			tvIdVenditore.setText(R.string.vuoto);
			tvBaudrate.setText(R.string.vuoto);
			btnConnettiUSB.setText(R.string.connettiti_al_dispositivo);
		}
	}
	
	public void ClickConnettiRemoto(View v)
	{
		if(DEVICE.Stato() == true)						//se sono connesso
			if(DEVICE.TipoConnessione() == 0)			//tramite usb
				ClickConnettiUSB(null);					//allora mi disconetto dall'usb
			else
				if(DEVICE.TipoConnessione() == 2)		//altrimenti se sono connesso tramite bluetooth
					ClickConnettiBluetooth(null);		//allora mi disconetto dal bluetooth
		
		
		
		if(DEVICE.Stato() == false)						//se non sono connesso al device
		{
			String IP = "";
			int porta = 0;
			boolean errore = false;
			try
			{
				IP = etIp.getText().toString();
				if(IP.length() == 0)
					errore = true;
				
				porta = Integer.parseInt(etPorta.getText().toString());
			}
			catch(Exception e)
			{
				errore = true;
			}
			
			if(errore)
			{
				MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), "Inserire un IP e porta corretti");
				return;
			}
			
			if(DEVICE.ConnettiIP(etIp.getText().toString(), Integer.parseInt(etPorta.getText().toString())))		//se riesco a connettermi al device
			{
				tvStatoRemoto.setText(R.string.connessione_stabilita);
				tvStatoRemoto.setTextColor(Color.GREEN);
				btnConnettitiRemoto.setText(R.string.disconnettiti);
				tvIp.setText(IP);
				tvPorta.setText(porta + "");
			}
			else													//se non riesco a connettermi al device
			{
				tvStatoRemoto.setText(R.string.impossibile_stabilire_una_connessione_remota);
				tvStatoRemoto.setTextColor(Color.RED);
			}
		}
		else									//altrimenti sono già connesso al device
		{
			DEVICE.Disconnetti();				//quindi mi disconnetto dal device
			tvStatoRemoto.setText(R.string.disconnesso);
			tvStatoRemoto.setTextColor(Color.BLACK);
			btnConnettitiRemoto.setText(R.string.connettiti_al_dispositivo);
			tvIp.setText(getResources().getString(R.string.vuoto));
			tvPorta.setText(getResources().getString(R.string.vuoto));
		}
	}
	
	public void ClickConnettiBluetooth(View v)
	{
		if(DEVICE.Stato() == true)						//se sono connesso
			if(DEVICE.TipoConnessione() == 0)			//tramite usb
				ClickConnettiUSB(null);				//allora mi disconetto dall'usb
			else
				if(DEVICE.TipoConnessione() == 1)		//altrimenti se sono connesso tramite ip
					ClickConnettiRemoto(null);		//allora mi disconetto dall'ip
		
		
		if(DEVICE.Stato() == false)						//se non sono connesso al device
		{
			int idDeviceSelezionato = rgListaDispositiviAssociati.getCheckedRadioButtonId();
			String device = "";
			
			if(idDeviceSelezionato != -1)				//se è stato selezionato un device dalla lista
				device = ((RadioButton)findViewById(idDeviceSelezionato)).getText().toString();
			else
			{
				MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), "Selezionare prima un device");
				return;
			}
			
			if(DEVICE.ConnettiBLUETOOTH(device))		//se riesco a connettermi al device
			{
				tvStatoBluetooth.setText(R.string.connessione_stabilita);
				tvStatoBluetooth.setTextColor(Color.GREEN);
				btnConnettitiBluetooth.setText(R.string.disconnettiti);
				tvNomeDevice.setText(device);
				tvMacAddress.setText(DEVICE.MacAddress());
			}
			else										//se non riesco a connettermi al device
			{
				tvStatoBluetooth.setText(R.string.impossibile_stabilire_una_connessione_bluetooth);
				tvStatoBluetooth.setTextColor(Color.RED);
			}
		}
		else											//altrimenti sono già connesso al device
		{
			DEVICE.Disconnetti();						//quindi mi disconnetto dal device
			tvStatoBluetooth.setText(R.string.disconnesso);
			tvStatoBluetooth.setTextColor(Color.BLACK);
			btnConnettitiBluetooth.setText(R.string.connettiti_al_dispositivo);
			tvNomeDevice.setText(getResources().getString(R.string.vuoto));
			tvMacAddress.setText(getResources().getString(R.string.vuoto));
		}
	}
	
	public void OnClickAggiornaDeviceAssociati(View v)
	{
		List<String> device = DEVICE.OttieniDispositiviAssociatiBluetooth();
		rgListaDispositiviAssociati.removeViews(0, rgListaDispositiviAssociati.getChildCount());
		
		for(int i = 0; i < device.size(); i++)
		{
			RadioButton rb = new RadioButton(getApplicationContext());
			rgListaDispositiviAssociati.addView(rb);
			rb.setText(device.get(i));
			rb.setTextColor(Color.BLACK);
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		if(group.getCheckedRadioButtonId() == R.id.CONN_rb_Seriale)
		{
			rlUSB.setVisibility(View.VISIBLE);
			rlRemoto.setVisibility(View.GONE);
			rlBluetooth.setVisibility(View.GONE);
		}
		else
			if(group.getCheckedRadioButtonId() == R.id.CONN_rb_Ip)
			{
				rlUSB.setVisibility(View.GONE);
				rlRemoto.setVisibility(View.VISIBLE);
				rlBluetooth.setVisibility(View.GONE);
			}
			else
				if(group.getCheckedRadioButtonId() == R.id.CONN_rb_Bluetooth)
				{
					rlUSB.setVisibility(View.GONE);
					rlRemoto.setVisibility(View.GONE);
					rlBluetooth.setVisibility(View.VISIBLE);
					OnClickAggiornaDeviceAssociati(null);
				}
	}

	@Override
	protected void onPause ()						//entro qui ogni volta che l'utente TORNA INDIETRO, GIRA IL MONITOR o quando va direttamente sulla HOME di android
	{
		super.onPause();
		SalvaDati();
	}
	
	@Override
	protected void onResume()						//entro qui dentro ogni volta che l'utente entra in questa activity
	{
		super.onResume();
		CaricaDati();
	}
}
