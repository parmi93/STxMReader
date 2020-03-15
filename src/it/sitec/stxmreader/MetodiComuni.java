package it.sitec.stxmreader;

import java.util.ArrayList;
import java.util.List;

import STCM.DEVICE;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MetodiComuni 
{
    public static String[] DividiInRighe(String Risposta)          //divido la stringa in input in un array
    {
        while(Risposta.indexOf("\r\n\r\n") != -1)
            Risposta = Risposta.replace("\r\n\r\n", "\r\n");        //elimino tutti i doppi a capo

        return Risposta.split("\r\n");                              //divido la stringa per righe e le salvo in un array
    }

    public static Boolean TrovaErrori(String Risposta)             //cerco la stringa error
    {
        String[] righe = DividiInRighe(Risposta);

        for(int i = 0; i < righe.length; i++)                       //se in un qualsiasi campo dell array trovo la stringa "error" restituisco true
            if(righe[i].trim().toLowerCase().compareTo("error") == 0)
                return true;

        return false;
    }

    public static String CreaComandoAtBaseLeggi(String Comando)         //creo il comando da inviare al modem
    {
        return "AT+" + Comando + "\r\n";
    }

    public static String CreaComandoAtProprietarioLeggi(String Comando) //creo il comando da inviare al modem
    {
        return "AT+SMSSND=" + Comando + "?\r\n";
    }
    
    public static String CreaComandoAtProprietarioScrivi(String Comando, String Dati)
    {
    	return "AT+SMSSND=" + Comando + "=" + Dati + "\r\n";
    }

    public static String CreaComandoAtProprietarioComando(String Comando)
    {
    	return "AT+SMSSND=" + Comando + "\r\n";
    }
    
    public static List<String[]> OttieniDati(String Comando, String Risposta, boolean RimuoviComando, boolean SeparaPerVirgola)
    {
    	/*Questo metodo restituisce una lista di array, di cui ogni elemento di un array è diviso per il carattere ',', ed ogni array della lista è diviso per riga*/
    	
    	String[] righe = DividiInRighe(Risposta);
		List<String[]> ritorno = new ArrayList<String[]>();
    	
    	int i = 0;
		while(righe[i++].indexOf("+"+Comando+":") == -1);			//cerco la stringa "+COMANDO:"
		i--;
		
		if(RimuoviComando)
			righe[i] = righe[i].replace("+"+Comando+":", "").trim();		//quindi rimuovo la sottostringa trovata con il ciclo while qui sopra
		
		
		if(SeparaPerVirgola)
			for(; i < righe.length; i++)
			{
				int nVirgole = 1;
				for(int ii = 0; ii < righe[i].length(); ii++)
					if(righe[i].charAt(ii) == ',')
						nVirgole++;
				
				ritorno.add(new String[nVirgole]);
				
				String temp = "";
				int jj = 0;
				for(int j = 0; j < righe[i].length(); j++)
				{
					if(righe[i].charAt(j) != ',')
						temp += righe[i].charAt(j);
					else
					{
						ritorno.get(ritorno.size() - 1)[jj++] = temp;
						temp = "";
					}
				}
				ritorno.get(ritorno.size() - 1)[jj] = temp;
			}
		else
			for(; i < righe.length; i++)
				ritorno.add(new String[] {righe[i]} );
			
		
		return ritorno;
    }

    public static void AggiornaTerminale(String rispostaTemporanea, SharedPreferences preferencesTerm)
    {
		SharedPreferences.Editor editorTerm = preferencesTerm.edit();
		
		editorTerm.putString("monitor", preferencesTerm.getString("monitor", "") + rispostaTemporanea);
		editorTerm.commit();
		Terminale.CaricaDati();
    }
    
    public static void CreaToastComando(Context contesto, int risultato, ProgressBar pb)
    {
		if(risultato == 0)
		{
			Toast toast = Toast.makeText(contesto, "Il device può eseguire un comado alla volta!", Toast.LENGTH_SHORT);
			((View)toast.getView()).setBackgroundColor(Color.argb(150, 255, 215, 0));
			TextView tv = (TextView)toast.getView().findViewById(android.R.id.message);
			tv.setTextColor(Color.BLACK);
			tv.setTextAppearance(contesto, Typeface.BOLD);
			toast.show();
		}
		else
			if(risultato == -1)
			{
				Toast toast = Toast.makeText(contesto, "E' necessario prima stabilire una connessione con il device!", Toast.LENGTH_LONG);
				((View)toast.getView()).setBackgroundColor(Color.argb(150, 255, 0, 0));
				TextView tv = (TextView)toast.getView().findViewById(android.R.id.message);
				tv.setTextColor(Color.BLACK);
				tv.setTextAppearance(contesto, Typeface.BOLD);
				toast.show();
			}
			else
				if(risultato == 1)
				{
					Toast.makeText(contesto, "Attendere...", Toast.LENGTH_SHORT).show();
					if(pb != null)
						pb.setVisibility(View.VISIBLE);
				}
    }

    public static void CreaToatOperazioneEseguita(Context contesto)
    {
    	Toast toast = Toast.makeText(contesto, "Opreazione eseguita correttamente!!", Toast.LENGTH_SHORT);
		((View)toast.getView()).setBackgroundColor(Color.argb(200, 0, 255, 0));
		TextView tv = (TextView)toast.getView().findViewById(android.R.id.message);
		tv.setTextColor(Color.BLACK);
		tv.setTextAppearance(contesto, Typeface.BOLD);
		toast.show();
    }
    
    public static void CreaToastOperazioneEseguitaPersonalizzata(Context contesto, String messaggio)
    {
    	Toast toast = Toast.makeText(contesto, messaggio, Toast.LENGTH_SHORT);
		((View)toast.getView()).setBackgroundColor(Color.argb(200, 0, 255, 0));
		TextView tv = (TextView)toast.getView().findViewById(android.R.id.message);
		tv.setTextColor(Color.BLACK);
		tv.setTextAppearance(contesto, Typeface.BOLD);
		toast.show();
    }
    
    public static void CreaToastConnessioneInterrotta(Context contesto)
    {
    	Toast toast = null;
    	
    	if(DEVICE.TipoConnessione() == 0)
    		toast = Toast.makeText(contesto, "Il device non ha risposto correttamente!!\nProvare ad incrementare il TIMEOUT\nVerificare che il cavo USB sia collegato", Toast.LENGTH_LONG);
    	else
    		if(DEVICE.TipoConnessione() == 1)
    			toast = Toast.makeText(contesto, "Il device non ha risposto correttamente!!\nProvare ad incrementare il TIMEOUT\nVerificare che la connessione internet non sia caduta", Toast.LENGTH_LONG);
    		else
    			if(DEVICE.TipoConnessione() == 2)
    				toast = Toast.makeText(contesto, "Il device non ha risposto correttamente!!\nProvare ad incrementare il TIMEOUT\nVerificare di trovarsi a portata di bluetooth", Toast.LENGTH_LONG);
    	
    	((View)toast.getView()).setBackgroundColor(Color.argb(200, 255, 0, 0));
		TextView tv = (TextView)toast.getView().findViewById(android.R.id.message);
		tv.setTextColor(Color.BLACK);
		tv.setTextAppearance(contesto, Typeface.BOLD);
		toast.show();
    }

    public static void CreaToastErrore(Context contesto)
    {
    	Toast toast = Toast.makeText(contesto, "ERRORE, Consultare il manuale.", Toast.LENGTH_LONG);
		((View)toast.getView()).setBackgroundColor(Color.argb(200, 255, 0, 0));
		TextView tv = (TextView)toast.getView().findViewById(android.R.id.message);
		tv.setTextColor(Color.BLACK);
		tv.setTextAppearance(contesto, Typeface.BOLD);
		toast.show();
    }
    
    public static void CreaToastErrorePersonalizzato(Context contesto, String messaggio)
    {
    	Toast toast = Toast.makeText(contesto, "ERRORE: " + messaggio + "\nCONSULTARE IL MANUALE!!", Toast.LENGTH_LONG);
		((View)toast.getView()).setBackgroundColor(Color.argb(200, 255, 0, 0));
		TextView tv = (TextView)toast.getView().findViewById(android.R.id.message);
		tv.setTextColor(Color.BLACK);
		tv.setTextAppearance(contesto, Typeface.BOLD);
		toast.show();
    }
}
