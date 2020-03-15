package STCM;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class DRIVER_BLUETOOTH extends DRIVER
{
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mmSocket;
	private OutputStream DATA_OUTPUT_STEAM = null;
	private InputStream DATA_INPUT_STEAM = null;
	private boolean STATO;
	private BluetoothDevice Device;
	private int TempReadLen, TempWriteLen;
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	@Override
	public void Connetti()
	{
		try
		{
			mmSocket = Device.createRfcommSocketToServiceRecord(uuid);		//tento una connesione con uuid
			mBluetoothAdapter.cancelDiscovery();
			mmSocket.connect();
			
			DATA_INPUT_STEAM = mmSocket.getInputStream();
			DATA_OUTPUT_STEAM = mmSocket.getOutputStream();
			STATO = true;
		}
		catch(Exception e)													//non sono riuscito a connettermi tramite uuid
		{
			STATO = false;
			try
			{
				mmSocket.close();	
				
				Method m = Device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});	//tento una connessione con un altro sistema
				mmSocket = (BluetoothSocket)m.invoke(Device, 1);
				mBluetoothAdapter.cancelDiscovery();
				mmSocket.connect();
				
				DATA_INPUT_STEAM = mmSocket.getInputStream();
				DATA_OUTPUT_STEAM = mmSocket.getOutputStream();
				STATO = true;
			}
			catch(Exception e2)
			{
				STATO = false;
				try
				{
					mmSocket.close();
				}
				catch(Exception e3)
				{}
			}
		}
	}


	@Override
	public void Disconnetti()
	{
		STATO = false;
		try
		{
			mmSocket.close();
			DATA_INPUT_STEAM.close();
			DATA_OUTPUT_STEAM.close();
		}
		catch(Exception e)
		{
		}
	}

	@Override
	public void Write(byte[] buffWrite)
	{
		try
		{
			DATA_OUTPUT_STEAM.write(buffWrite);
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
			if(DATA_INPUT_STEAM.available() > 0)					//se c'è qualcosa da leggere
				TempReadLen = DATA_INPUT_STEAM.read(buffRead);
			else
				TempReadLen = -1;
		}
		catch(Exception e)
		{
			TempReadLen = -1;
		}
	}

	@Override
	public boolean Connesso()
	{
		return STATO;
	}

	@Override
	public int WriteLength()
	{
		return TempWriteLen;
	}

	@Override
	public int ReadLength()
	{
		return TempReadLen;
	}
	
	public String OttieniMacAddress()
	{
		if(Device != null)
			return Device.getAddress().toString();
		
		return "";
	}
	
	public void DispositivoScelto(String NomeDevice)
	{
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(mBluetoothAdapter != null)
		{
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			
			
			for (BluetoothDevice device : pairedDevices)
				if(device.getName().compareTo(NomeDevice) == 0)					//appena trovo il device indicato
					Device = device;
		}
	}
}
