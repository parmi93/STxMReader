package STCM;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class DRIVER_REMOTO extends DRIVER {

	private String IP;
	private int PORTA = 0;
	private Socket SOCKET;
	private DataOutputStream DATA_OUTPUT_STEAM = null;
	private DataInputStream DATA_INPUT_STEAM = null;
	private boolean STATO = false;
	private int TempReadLen, TempWriteLen;
	
	@Override
	public void Connetti()
	{
		if(IP == null || PORTA == 0)						//se non è stato impostato l'ip o la porta
		{
			STATO = false;
			return;
		}
		
		Thread threadConnetti = new Thread(new Runnable()	//devo avviare un nuovo thread
		{
		    @Override
		    public void run()
		    {
				try
				{
					SOCKET = new Socket(IP, PORTA);			//mi connetto
					SOCKET.setSoTimeout(10);				//imposto un timeout
					DATA_OUTPUT_STEAM = new DataOutputStream(SOCKET.getOutputStream());	//ottengo 2 oggetto che mi serivranno per scrivere e leggere i dati dal device
					DATA_INPUT_STEAM = new DataInputStream(SOCKET.getInputStream());
					STATO = true;
				}
				catch(Exception e)
				{
					STATO = false;
				}
		    }
		});
		
		threadConnetti.start();								//accio partire il thread

		int tempo = 0;
		
		while(STATO == false)
		{
			try { Thread.sleep(100); } catch(Exception e){ };
			tempo += 100;
			
			if(tempo > 12000)
				break;
		}
	}
	
	@Override
	public void Disconnetti()
	{
		try
		{
			STATO = false;									//mi disconnetto
			SOCKET.close();
			DATA_OUTPUT_STEAM.close();
			DATA_INPUT_STEAM.close();
		}
		catch(Exception e) {}
	}

	@Override
	public void Write(byte[] buffWrite)
	{
		try
		{
			DATA_OUTPUT_STEAM.write(buffWrite);				//scrivo i dati
			TempWriteLen = buffWrite.length;				//salvo la lunghezza dei dati scritti
		}
		catch(Exception e)
		{
			TempWriteLen = -1;
		}
	}
	
	@Override
	public void Read(byte[] buffRead)
	{
		try
		{
			TempReadLen = DATA_INPUT_STEAM.read(buffRead);	//leggo i dati, e salvo il numero di byte letti
		}
		catch(Exception e)
		{
			TempReadLen = -1;
		}
	}

	@Override
	public boolean Connesso()
	{
		return STATO;										//mi serve per capire se sono connesso o meno
	}
	
	@Override
	public int WriteLength()
	{
		return TempWriteLen;								//restituisce la il numero di byte scritti
	}

	@Override
	public int ReadLength()
	{
		return TempReadLen;									//restituisce il numero di byte letti
	}

	public void ImpostaIpPorta(String Ip, int Porta)
	{
		IP = Ip;											//imposto Ip e porta
		PORTA = Porta;
	}
}
