package STCM;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.hardware.usb.UsbManager;

public class DEVICE {
    static private boolean OCCUPATO = false;                    //mi serve per capire se √® in esecuzione qualche comando
    static private String RISPOSTA_COMPLETA;                    //conterr√† tutti i dati letti allo scadere
    static private int TIMEOUT;                                 //conetrr√† il TIMEOUT
    static private int TEMPO_TRASCORSO;                         //conterr√† il tempo trascorso dall'ultimo read fatto al device
    static private Timer TIMER;                                 //mi servir√† per fare i read al device ad intervalli di tempo regolari
    static private int INTERVALLO_DI_RILETTURA = 300;           //questo √® l'intervallo di tempo dei vari read da fare al device
    static private DRIVER mDRIVER;                      		//conterr√† i driver per poter accedere al device
    static private OnRead[] ON_READ;                            //questa √® l'intefaccia che contiene il metodo da eseguire al termine del timeout
    static private boolean CONNESSO = false;                    //mi serve per sapere se sono connesso al device
    static private int TIPO_CONNESSIONE = -1;
    static private boolean InLettura = false;
    static private String ID;
    static private int TIPO_COMANDO;
    static private String ID_PRODUTTORE;
    static private String ID_VENDITORE;
    static private String MAC_ADDRESS;
    static private List<byte[]> bufferCoda = new ArrayList<byte[]>();
    static private List<Integer> timeoutCoda = new ArrayList<Integer>();
    static private List<OnRead[]> OnReadCoda = new ArrayList<OnRead[]>();
    static private List<String> IdCoda = new ArrayList<String>();
    static private List<Integer> TipoComandoCoda = new ArrayList<Integer>();
    
    static public boolean ConnettiUSB(UsbManager mUsbManager, PendingIntent Permesso, int Baudrate)	//mi connetto da USB
    {
    	TIPO_CONNESSIONE = 0;
    	mDRIVER = new DRIVER_USB(mUsbManager);
        ((DRIVER_USB)mDRIVER).BaudratePermesso(Baudrate, Permesso);
        mDRIVER.Connetti();
        CONNESSO = mDRIVER.Connesso();
        ID_PRODUTTORE = ((DRIVER_USB)mDRIVER).IdProduttore();
        ID_VENDITORE = ((DRIVER_USB)mDRIVER).IdVenditore();
        return CONNESSO;
    }
    
    static public boolean ConnettiIP(String Ip, int Porta)		//mi connetto da remoto
    {
    	TIPO_CONNESSIONE = 1;
    	mDRIVER = new DRIVER_REMOTO();
    	((DRIVER_REMOTO)mDRIVER).ImpostaIpPorta(Ip, Porta);
    	mDRIVER.Connetti();
    	CONNESSO = mDRIVER.Connesso();
    	return CONNESSO;
    }
    
    static public boolean ConnettiBLUETOOTH(String NomeDevice)
    {
    	TIPO_CONNESSIONE = 2;
    	mDRIVER = new DRIVER_BLUETOOTH();
    	((DRIVER_BLUETOOTH)mDRIVER).DispositivoScelto(NomeDevice);
    	mDRIVER.Connetti();
    	CONNESSO = mDRIVER.Connesso();
    	MAC_ADDRESS = ((DRIVER_BLUETOOTH)mDRIVER).OttieniMacAddress();
    	return CONNESSO;
    }
    
    static public void Disconnetti()
    {
    	StopRead();
    	SvuotaCoda();
        if(CONNESSO)
        	mDRIVER.Disconnetti();
        CONNESSO = false;
    }

    static public int WRITE(byte[] buffer, int timeout, OnRead[] mOnRead, String id, int TipoComando, boolean mettiInCoda)
    {
    	if(CONNESSO == false)									//se non sono connesso al device
    		return -1;
    	
        if(OCCUPATO == false)               					//se non sono gi‡† occupato ad eseguire qualche altro comando oppure il timeout Ë minore di 0
        {
            OCCUPATO = true;                                    //mi segno che il device Ë accupato ad eseguire un comando
            ON_READ = mOnRead;
            TIMEOUT = timeout;
            TEMPO_TRASCORSO = 0;
            RISPOSTA_COMPLETA = "";
            ID = id;
            TIPO_COMANDO = TipoComando;

            mDRIVER.Write(buffer);
            if(mDRIVER.WriteLength() == -1)                 	//eseguo il write sul device, se la scrittura fallisce, restituisco -1
            {
                OCCUPATO = false;								//quindi il device non Ë occupato
                return -1;
            }
            
            RunRead();											//faccio partire il read
            
            return 1;                                       	//ritorno 1, vuol dire che il comando Ë stato spedito al device
        }
        else
        	if(mettiInCoda)										//se mi Ë stato chiesto di mettere in coda il comando
        	{
        		bufferCoda.add(buffer);							//metto in coda il comando
        		timeoutCoda.add(timeout);
        		OnReadCoda.add(mOnRead);
        		IdCoda.add(id);
        		TipoComandoCoda.add(TipoComando);
        		return 2;										//vuol dire che il comando Ë stato messo in coda
        	}
        	else
        		return 0;                                       //vuol dire che il device Ë gi‡† occupato e che quindi non ha eseguito il comando
    }

    static public int ForceWrite(byte[] buffer)					//questo metodo esegue un write sul device indipendentemente dal fatto che il device sia occupato meno
    {
    	if(CONNESSO == false)									//se non sono connesso al device
    		return -1;
    	
    	mDRIVER.Write(buffer);
    	if(mDRIVER.WriteLength() == -1)                 		//eseguo il write sul device, se la scrittura fallisce, restituisco -1
			return -1;
            
		return 1; 
    }
    
    static public void StopRead()
    {
        if(CONNESSO == true)
        {
        	if(TIMER != null)
        		TIMER.cancel();
        	
            OCCUPATO = false;
            InLettura = false;
        }
    }

    static public boolean Stato()
    {
    	return CONNESSO;    	
    }
    
    static public String IdProduttore()
    {
    	return ID_PRODUTTORE;
    }
    
    static public String IdVenditore()
    {
    	return ID_VENDITORE;
    }
    
    static public String MacAddress()
    {
    	return MAC_ADDRESS;
    }
    
	static public List<String> OttieniDispositiviAssociatiBluetooth()
	{
		List<String> dispositivi = new ArrayList<String>();
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		
		for (BluetoothDevice device : pairedDevices)
			dispositivi.add(device.getName());
		
		return dispositivi;
	}
    
    static public void SvuotaCoda()
    {
    	bufferCoda.clear();
    	timeoutCoda.clear();
    	OnReadCoda.clear();
    	IdCoda.clear();
    	TipoComandoCoda.clear();
    }
    
    static public int TipoConnessione()
    {
    	return TIPO_CONNESSIONE;
    }
    
    static public boolean SeiInFaseDiLettura()
    {
    	return InLettura;
    }
    
    static private void RunRead()
    {
    	if(InLettura == false)									//se non sto gi‡ leggendo
    		READ();
    }
    
    static private void READ()
    { 
        TIMER = new Timer();

        TimerTask ttk = new TimerTask()
        {
            @Override public void run()
            {
            	InLettura = true;
                int i, len;
                byte[] rbuf = new byte[1024];                   //questo Ë il buffer di 1KB che conterr‡† ciÚ che viene letto dal device

                mDRIVER.Read(rbuf);                   			//leggo i dati dal device

                len = mDRIVER.ReadLength();
                
                String RispostaTemporanea = "";
                for(i = 0; i < len; i++)                        //salvo i dati letti nella stringa RISPOSTA_DEL_DEVICE
                    RispostaTemporanea += (char)rbuf[i];
                
                if(len <= 0)                                    //se non Ë stato letto nulla dal device
                    TEMPO_TRASCORSO += INTERVALLO_DI_RILETTURA; //allora incremento il tempo trascorso
                else
                {
                    TEMPO_TRASCORSO = 0;                        //altrimenti resetto il tempo trascorso
                    RISPOSTA_COMPLETA += RispostaTemporanea;
                    for(int j = 0; j < ON_READ.length; j++)		//Sollevo l'evento ON_READ.Temporaneo, passano come parametro ciÚ che Ë stato appena letto
                    	ON_READ[j].Temporaneo(RispostaTemporanea, ID);
                }

                if(TIMEOUT < 0)									//ne caso di timeout negativo, mi segno che il device non Ë occupato, in questo modo sar‡ sempre possibile
                	OCCUPATO = false;							//inviare comandi al device(come ad esempio dal terminale)
                
                if(TIMEOUT > 0 && TEMPO_TRASCORSO >= TIMEOUT)  //se il tempo trascorso supera il timeout
                {
                    TIMER.cancel();                             //fermo il timer
                    OCCUPATO = false;                           //mi segno che il device non Ë pi˘ occupato ad eseguire alcun comando
            		InLettura = false;
                    for(int j = 0; j < ON_READ.length; j++)		//Sollevo l'evento ON_READ.Completo, passano come parametro tutto ciÚ che Ë stato letto fino allo scadere del timeout
                    	if(bufferCoda.size() == 0)				//se non c'Ë nessun comando in coda
                    		ON_READ[i].Completo(RISPOSTA_COMPLETA, ID, TIPO_COMANDO, false);
                    	else
                    		ON_READ[i].Completo(RISPOSTA_COMPLETA, ID, TIPO_COMANDO, true);
                    
                    if(bufferCoda.size() > 0)					//se la coda dei comandi da eseguire non Ë vuota
                    {
                    	int ritorno = WRITE(bufferCoda.get(0), timeoutCoda.get(0), OnReadCoda.get(0), IdCoda.get(0), TipoComandoCoda.get(0), false);	//eseguo il primo comando in coda
                    	
                    	if(ritorno == 1)						//se il comando Ë stato eseguito
                    	{
                        	bufferCoda.remove(0);				//rimuovo dalla coda il comando appena eseguito	
                        	timeoutCoda.remove(0);
                        	OnReadCoda.remove(0);
                        	IdCoda.remove(0);
                        	TipoComandoCoda.remove(0);
                    	}
                    	else									//altrimento sollevo l'evento "ErroreWriteComandoInCoda", e svuoto tutta la coda
                    	{
                    		for(int j = 0; j < ON_READ.length; j++)
                    			ON_READ[j].ErroreWriteComandoInCoda(ritorno);
                    		
                    		SvuotaCoda();
                    	}
                    }
                }
            }
        };

        TIMER.scheduleAtFixedRate(ttk, 100, INTERVALLO_DI_RILETTURA);
        //faccio partire il timer in ritardo di 100ms, eseguira "ttk" ad intervalli regolari
        //100ms Ë il tempo necessario perchË mi arrivi una prima risposta dal device stcm
    }
}