package it.sitec.stxmreader;

import java.util.ArrayList;
import java.util.List;

import STCM.DEVICE;
import STCM.OnRead;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListaDispositivi extends Activity implements OnRead{

	String id_activity, id_terminale;	//questo è un id che mi viene passato dal main_activity
	SharedPreferences preferences;		//mi serve per serializzare i dati
	SharedPreferences.Editor editor;	//mi serve per serializzare i dati
	static OnRead[] read = new OnRead[1];
	public static List<String> listaDispositivi = new ArrayList<String>();
	public static int blocchi = 0;
	
	int coloreRighePari, coloreRigheDispari;
	String tempRisposta, tempID, tempRispostaTemporanea;
	int tempRitorno, tempTipoComando;
	boolean tempComandiInCoda;
	int timeout;
	
	String comandoMBLISTA, comandoMBNOME;
	
	GridView gwIntestazione, gwContenuto;
	TextView tvTot;
	ProgressBar pbCaricamento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_dispositivi);
		read[0] = this;
		
		id_activity = getIntent().getStringExtra("id");			//salvo id che mi è stato passato dal main_activity
		id_terminale = getIntent().getStringExtra("idTerminale");	
		
		timeout = getIntent().getIntExtra("timeout", 900);		//salvo il timeout che mi è stato passato dal main_activity
		
		comandoMBLISTA = "MBLISTA";
		comandoMBNOME = "MBNOME";
		
		preferences = getSharedPreferences(id_activity, 0);		//ottengo l'accesso al file su cui salvare i dati, passandogli l'id
		editor = preferences.edit();
		
		coloreRighePari = Color.rgb(238, 232, 170);
		coloreRigheDispari = Color.rgb(240, 248, 255);
		
		OttieniPuntatoriView();
	}
	
	private void OttieniPuntatoriView()												//ottengo i puntatori ai vari View dell'intefaccia grafica
	{
		gwIntestazione = (GridView)findViewById(R.id.LISTDISP_gw_Intestazione);
		gwContenuto = (GridView)findViewById(R.id.LISTDISP_gw_Contenuto);
		tvTot = ((TextView)findViewById(R.id.LISTDISP_tv_Tot));
		pbCaricamento = ((ProgressBar)findViewById(R.id.LISTDISP_pb));
		
	}
	
	private void CaricaDati()	
	{
		gwIntestazione.setAdapter(new CreaIntestazioneListaDispositivi(this, new String[]{"Idx Sn       Ind Fab Ver Typ", "Descrizione"}));
		tvTot.setText(preferences.getString("tvTot", ""));
		pbCaricamento.setVisibility(preferences.getInt("pbCaricamento", View.GONE));
		gwContenuto.setAdapter(new CreaListaDispositivi(getApplicationContext(), listaDispositivi, coloreRighePari, coloreRigheDispari));
		gwContenuto.setSelection(preferences.getInt("gwContenuto", 0));
	}
	
	private void SalvaDati()
	{
		editor.putInt("pbCaricamento", pbCaricamento.getVisibility());
		editor.putInt("gwContenuto", gwContenuto.getFirstVisiblePosition());
		editor.putString("tvTot", tvTot.getText().toString());
		editor.commit();
	}
	
	public void OnClickLeggiListaDisp(View v)
	{
		int ritorno = DEVICE.WRITE(MetodiComuni.CreaComandoAtProprietarioLeggi(comandoMBLISTA).getBytes(), timeout + 8000, read, comandoMBLISTA, TIPO_COMANDO.LEGGI, false);
		MetodiComuni.CreaToastComando(this, ritorno, pbCaricamento);			//genero un popout in base al ritorno ottenuto dal metodo "DEVICE.WRITE()"
	}
	
	public void OnClickSalvaModifiche(View v)
	{
		int len = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoMBNOME, "").length() - 2;	//ottengo la lunghezza della stringa "at+smssnd=mbnome=" il -2 è per "\r\n"
		String dati = "";
		
		int i = 0;
		
		for(; i < CreaListaDispositivi.etDescrizioniModificate.size() && i < 7; i++)
		{
			//dal tag mi estraggo il serial number, formando una stringa del genere --> 23876938,"acqua"
			String temp = CreaListaDispositivi.etDescrizioniModificate.get(i).getTag().toString() + ",\"" + CreaListaDispositivi.etDescrizioniModificate.get(i).getText() + "\"";
			
			if(dati.length() > 0)								//se in datiTemp c'è già qualcosa
				temp = "," + temp;								//metto una virgola davanti alla stringa temp
			
			if(dati.length() + temp.length() + len <= 160)		//se la lunghezza del comando che sto andando a formare non supera i 160 caratteri
				dati += temp;
			else
				break;
		}
		
		if(dati.length() > 0)
		{
			String comando = MetodiComuni.CreaComandoAtProprietarioScrivi(comandoMBNOME, dati);
			int ritorno = DEVICE.WRITE(comando.getBytes(), 3000 + timeout, read, comandoMBNOME, TIPO_COMANDO.SCRIVI, false);
			MetodiComuni.CreaToastComando(getApplicationContext(), ritorno, pbCaricamento);
			editor.putInt("pbCaricamento", View.VISIBLE);
			editor.commit();
				
			blocchi = i;
		}
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
							List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, true, false);
							
							if(tempID == comandoMBLISTA)								//vado a capire quale comando è stato dato
							{	
								editor.putString("tvTot", dati.get(1)[0]);
								
								listaDispositivi.clear();								//pulisco tutte le liste
								
								CreaListaDispositivi.lista.clear();
								CreaListaDispositivi.etDescrizioniModificate.clear();
								
								for(int i = 3; i < dati.size(); i++)
								{
									if(dati.get(i)[0].length() > 28)
									{
										listaDispositivi.add(dati.get(i)[0].substring(0, 28));
										listaDispositivi.add(dati.get(i)[0].substring(29));
									}
								}
							}
							
							MetodiComuni.CreaToatOperazioneEseguita(getApplicationContext());//genero un popout
							editor.putInt("pbCaricamento", View.INVISIBLE);					//rendo invisibile la progressbar circolare(scrivo su file)

							editor.commit();	//rendo effettive tutte le modifiche fatte fino ad ora (scrivendole su file)
				    		CaricaDati();		//carico ("sulla grafica") i dati appena salvati
						}
						else
							MetodiComuni.CreaToastErrore(getApplicationContext());
					
					if(tempTipoComando == TIPO_COMANDO.SCRIVI)
					{
						List<String[]> dati = MetodiComuni.OttieniDati(tempID, tempRisposta, false, false);
						
						if(tempID == comandoMBNOME)							//vado a capire quale comando è stato dato
							if(MetodiComuni.TrovaErrori(tempRisposta) == false)	//se non ci sono stati errori
							{
								String messaggio = "";
								for(int i = 0; i < dati.size(); i++)
									if(dati.get(i)[0].toLowerCase().compareTo("ok") != 0)
										messaggio += dati.get(i)[0] + "\n";
									else
										break;
								
								
								for(int i = 0; i < blocchi; i++)
								{
									CreaListaDispositivi.etDescrizioniModificate.get(0).setBackgroundColor(CreaListaDispositivi.etDescrizioniModificate.get(0).getId());//reimposto il background delle ediText che sono state salvate
									CreaListaDispositivi.etDescrizioniModificate.remove(0);
								}
								
								MetodiComuni.CreaToastOperazioneEseguitaPersonalizzata(getApplicationContext(), messaggio);
								
								editor.putInt("pbCaricamento", View.INVISIBLE);							//rendo invisibile la progressbar circolare(scrivo su file)
					    		pbCaricamento.setVisibility(View.INVISIBLE);							//rendo invisibile la progressbar circolare(modifico la grafica)
					    		editor.commit();
					    		
								OnClickSalvaModifiche(null);
							}
							else
							{
								MetodiComuni.CreaToastErrorePersonalizzato(getApplicationContext(), dati.get(0)[0]);
								editor.putInt("pbCaricamento", View.INVISIBLE);				//rendo invisibile la progressbar circolare(scrivo su file)
					    		pbCaricamento.setVisibility(View.INVISIBLE);				//rendo invisibile la progressbar circolare(modifico la grafica)
					    		editor.commit();
							}
					}
		    	}
				catch(Exception e)		//se i dati che mi sono arrivati sono incompleti
				{
		    		MetodiComuni.CreaToastConnessioneInterrotta(getApplicationContext());	//genero un popout
		    		DEVICE.SvuotaCoda();
		    		editor.putInt("pbCaricamento", View.INVISIBLE);							//rendo invisibile la progressbar circolare(scrivo su file)
		    		pbCaricamento.setVisibility(View.INVISIBLE);							//rendo invisibile la progressbar circolare(modifico la grafica)
		    		editor.commit();
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
	protected void onPause()														//entro qui ogni volta che l'utente TORNA INDIETRO, GIRA IL MONITOR o quando va direttamente sulla HOME di android
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
