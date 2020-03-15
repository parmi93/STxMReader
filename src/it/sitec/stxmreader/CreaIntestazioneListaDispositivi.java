package it.sitec.stxmreader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CreaIntestazioneListaDispositivi extends BaseAdapter {

	LayoutInflater inflater;
	View OGGETTO;
	Context contesto;
	
	int numeroColonne = 2;
	String[] intestazione;
	static List<View> lista = new ArrayList<View>();
	
	
	public CreaIntestazioneListaDispositivi(Context context, String[] intestazione)
	{
		contesto = context;
		inflater = (LayoutInflater)contesto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.intestazione = intestazione;
	}
	
	@Override
	public int getCount() {
		return intestazione.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(position < lista.size())			//se l'oggeto è già stato creato
			return lista.get(position);		//allora restituisco l'oggetto creato in precedenza
	
		
		OGGETTO = inflater.inflate(R.layout.oggetto_lista_dispositivi_intestazione, null);		//altrimenti vado a creare un nuovo oggetto
		TextView tvIntestazione =(TextView)OGGETTO.findViewById(R.id.OGGETTO_tv_ListaDispositiviIntestazione);
					
		tvIntestazione.setText(intestazione[position]);
				
		lista.add(tvIntestazione);		//salvo l'oggetto creato nella mia lista, in modo tale da non doverlo ricreare ogni volta
		
		return tvIntestazione;
	}
}
