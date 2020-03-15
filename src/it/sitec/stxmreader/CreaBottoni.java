package it.sitec.stxmreader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class CreaBottoni extends BaseAdapter {
	
	LayoutInflater inflater;
	View OGGETTO;
	Button btnMenu;
	Context constesto;
	
	String[] titoli = {"Connetti", "Info dispositivo", "GPRS", "Email", "Input\nOutput", "Numeri di telefono", "Schedulatori letture", "Schedulatore allarmi", "Comandi", "Lista dispositivi", "Terminale", "Info"};
	int[] icone = {R.drawable.connetti, R.drawable.info, R.drawable.gprs, R.drawable.email, R.drawable.io, R.drawable.telefono, R.drawable.schedulatore, R.drawable.alarm, R.drawable.comandi, R.drawable.lista_dispositivi, R.drawable.terminale, R.drawable.info_generali};
	static List<View> lista = new ArrayList<View>();
	
	public CreaBottoni(Context context) 
	{
		constesto = context;
		inflater = (LayoutInflater)constesto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return titoli.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(position < lista.size())				//se l'oggeto è già stato creato
			return lista.get(position);			//allora restituisco l'oggetto creato in precedenza
		
		OGGETTO = inflater.inflate(R.layout.oggetto_main_btn_menu, null);		//altrimenti vado a creare un nuovo oggetto
		
		
		btnMenu = (Button)OGGETTO.findViewById(R.id.OGGETTO_btn_Menu);
		btnMenu.setText(titoli[position]);
		btnMenu.setCompoundDrawablesWithIntrinsicBounds(0, icone[position], 0, 0);
		lista.add(btnMenu);			//salvo l'oggetto creato nella mia lista, in modo tale da non doverlo ricreare ogni volta
		btnMenu.setId(position);
			
		return OGGETTO;
	}
}
