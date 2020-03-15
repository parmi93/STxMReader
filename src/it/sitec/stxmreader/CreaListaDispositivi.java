package it.sitec.stxmreader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

public class CreaListaDispositivi extends BaseAdapter {

	LayoutInflater inflater;
	View OGGETTO;
	Context contesto;
	
	int righeDispariColore, righePariColore;
	
	int numeroColonne = 2;
	List<String> contenuto;
	public static List<EditText> lista = new ArrayList<EditText>();
	public static List<EditText> etDescrizioniModificate = new ArrayList<EditText>();
	
	public CreaListaDispositivi(Context context, List<String> contenuto, int ColoreRighePari, int ColoreRigheDspari)
	{
		contesto = context;
		inflater = (LayoutInflater)contesto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.contenuto = contenuto;
		
		righePariColore = ColoreRighePari;
		righeDispariColore = ColoreRigheDspari;
	}
	
	@Override
	public int getCount()
	{
		return contenuto.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	int posi;
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(position < lista.size())			//se l'oggeto è già stato creato
			return lista.get(position);		//allora restituisco l'oggetto creato in precedenza
		
		EditText etDescrizione;
		
		if(position % numeroColonne == 0)	//se mi trovo nella prima colonna(colonne pari), allora mi trovo nella colonna delle informazione
		{
			OGGETTO = inflater.inflate(R.layout.oggetto_lista_dispositivi_informazione, null);
			etDescrizione = (EditText)OGGETTO.findViewById(R.id.OGGETTO_et_ListaDispositiviInformazione);
					
			etDescrizione.setText(contenuto.get(position));
				
			lista.add(etDescrizione);		//salvo l'oggetto creato nella mia lista, in modo tale da non doverlo ricreare ogni volta
		}
		else								//altrimenti mi trovo nella colonna delle descrizioni
		{
			OGGETTO = inflater.inflate(R.layout.oggetto_lista_dispositivi_descrizione, null);
			etDescrizione = (EditText)OGGETTO.findViewById(R.id.OGGETTO_et_ListaDispositiviDescrizione);
					
			etDescrizione.setText(contenuto.get(position));												//imposto il testo dell'editText
			etDescrizione.setTag(lista.get(lista.size() - 1).getText().toString().substring(4, 12));	//nel tag vado a salvare il serial number a cui la descrizione  associata
			lista.add(etDescrizione);		//salvo l'oggetto creato nella mia lista, in modo tale da non doverlo ricreare ogni volta
			
			etDescrizione.addTextChangedListener(new TextWatcher()			//entro qui dentro ogni volta che viene modificato il testo all'interno del Editext
			{
				EditText et = (EditText)lista.get(lista.size() - 1);
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count)
				{}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after)
				{}
				
				@Override
				public void afterTextChanged(Editable s)
				{
					boolean modificato = false;
					
					for(int i = 0; i < etDescrizioniModificate.size(); i++)		//verifico che l'editText non si trovi goià nela lista "etDescrizioniModificate"
						if(et == etDescrizioniModificate.get(i))
						{
							modificato = true;
							break;
						}
					
					if(modificato == false)
					{
						etDescrizioniModificate.add(et);					//aggiungo l'editText nela lista "etDescrizioniModificate"(così so che è stato modificato)
						et.setBackgroundColor(Color.GREEN);					//imposto il colore di background verde, così l'utente sa che è stato modificato
					}
				}
			});
		}
		
		if((position / numeroColonne) % 2 == 0)		//imposto il background in base alla righa in esame(righe pari, righe dispari)
		{
			etDescrizione.setBackgroundColor(righePariColore);
			etDescrizione.setId(righePariColore);				//nel id vado a salvare il colore di background, mi servirà più avanti
		}
		else
		{
			etDescrizione.setBackgroundColor(righeDispariColore);
			etDescrizione.setId(righeDispariColore);			//nel id vado a salvare il colore di background, mi servirà più avanti
		}
		
		return etDescrizione;
	}
}
