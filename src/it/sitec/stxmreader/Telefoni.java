package it.sitec.stxmreader;

import java.util.List;

import STCM.DEVICE;
import STCM.OnRead;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class Telefoni extends Activity implements OnRead {

	String id_activity, id_terminale;	//questo è un id che mi viene passato dal main_activity
	SharedPreferences preferences;		//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	static OnRead[] read = new OnRead[1];
	
	String tempRisposta, tempID, tempRispostaTemporanea;
	int tempRitorno, tempTipoComando;
	boolean tempComandiInCoda;
	int timeout;
	
	String comandoTEL;
	
	EditText etTelefono1, etTelefono2, etTelefono3, etTelefono4;
	ProgressBar pbCaricamento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_telefoni);
		read[0] = this;
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		id_terminale = getIntent().getStringExtra("idTerminale");	
		
		timeout = getIntent().getIntExtra("timeout", 900);		//salvo il timeout che mi è stato passato dal main_activity
		
		comandoTEL = "TEL";
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();
		
		OttieniPuntatoriView();
	}
	
	private void OttieniPuntatoriView()												//ottengo i puntatori ai vari View dell'intefaccia grafica
	{
		etTelefono1 = ((EditText)findViewById(R.id.TEL_et_telefono1));
		etTelefono2 = ((EditText)findViewById(R.id.TEL_et_telefono2));
		etTelefono3 = ((EditText)findViewById(R.id.TEL_et_telefono3));
		etTelefono4 = ((EditText)findViewById(R.id.TEL_et_telefono4));		
		pbCaricamento = ((ProgressBar)findViewById(R.id.TEL_pb));	
	}
	
	private void CaricaDati()	
	{
		etTelefono1.setText(preferences.getString("etTelefono1", ""));	//vado a caricare i dati salvati
		etTelefono2.setText(preferences.getString("etTelefono2", ""));
		etTelefono3.setText(preferences.getString("etTelefono3", ""));
		etTelefono4.setText(preferences.getString("etTelefono4", ""));
		pbCaricamento.setVisibility(preferences.getInt("pbCaricamento", View.GONE));
	}
	
	private void SalvaDati()
	{
		editor.putString("etTelefono1", etTelefono1.getText().toString());	//salvo i dati sul file
		editor.putString("etTelefono2", etTelefono2.getText().toString());
		editor.putString("etTelefono3", etTelefono3.getText().toString());
		editor.putString("etTelefono4", etTelefono4.getText().toString());
		editor.putInt("pbCaricamento", pbCaricamento.getVisibility());
		editor.commit();	//scrivo i dati su file
	}
	
	public void OnClickAggiornaTutto(View v)
	{
		int ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoTEL).getBytes(), timeout, read, comandoTEL, TIPO_COMANDO.LEGGI, false);
		MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaTutto(View v)
	{
		String dati = etTelefono1.getText() + "," +etTelefono2.getText() + "," + etTelefono3.getText() + "," + etTelefono4.getText();
		String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoTEL, dati);
		int ritorno = DEVICE.WRITE(comando.getBytes(), timeout, read, comandoTEL, TIPO_COMANDO.SCRIVI, v == null);
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
							int i = 0;
							
							if(tempID == comandoTEL)									//vado a capire quale comando è stato dato
							{	
								if(dati.get(0).length > 0)
									editor.putString("etTelefono1", dati.get(0)[i++]);
								
								if(dati.get(0).length > 1)
									editor.putString("etTelefono2", dati.get(0)[i++]);
								
								if(dati.get(0).length > 2)
									editor.putString("etTelefono3", dati.get(0)[i++]);
								
								if(dati.get(0).length > 3)
									editor.putString("etTelefono4", dati.get(0)[i++]);
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
						int i = 0;
						boolean erroriTemp = false;
						
						if(tempID == comandoTEL)												//vado a capire quale comando è stato dato
							if((erroriTemp = MetodiComuni.TrovaErrori(tempRisposta)) == false)	//se non ci sono stati errori
							{
								if(dati.get(0).length > 0)
									editor.putString("etTelefono1", dati.get(1)[i++]);
									
								if(dati.get(0).length > 1)
									editor.putString("etTelefono2", dati.get(1)[i++]);
									
								if(dati.get(0).length > 2)
									editor.putString("etTelefono3", dati.get(1)[i++]);
									
								if(dati.get(0).length > 3)
									editor.putString("etTelefono4", dati.get(1)[i++]);
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
	
	@Override
	protected void onPause ()
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
