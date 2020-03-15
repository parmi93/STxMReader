package STCM;

import android.app.PendingIntent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

class DRIVER_USB extends DRIVER
{
    private UsbManager USB_MANAGER;
    private UsbDevice USB_DEVICE;
    private UsbDeviceConnection USB_DEVICE_CONNECTION;
    private UsbInterface USB_INTERFACE;
    private UsbEndpoint USB_ENDPOINT_IN;
    private UsbEndpoint USB_ENDPOINT_OUT;
    private int BAUDRATE = -1;
    private PendingIntent PERMESSO;
    private boolean STATO = false;
    private int TempReadLen, TempWriteLen;

    public DRIVER_USB(UsbManager mUsbManager)
    {
        USB_MANAGER = mUsbManager;
    }

    @Override
    public void Connetti()
    {
    	if(BAUDRATE == -1 || PERMESSO == null)
    	{
        	STATO =  false;
        	return;
        }
    	
        for (UsbDevice device : USB_MANAGER.getDeviceList().values())
        {
            if(!USB_MANAGER.hasPermission(device))                      //se non ho il permesso di accedere al device
                USB_MANAGER.requestPermission(device, PERMESSO);        //richiedo all'utente il permesso di accedere al device

            if (!USB_MANAGER.hasPermission(device))                     //se l'utente non mi ha concesso il permesso di accedere al device
            {
            	STATO =  false;
            	return;
            }
            
            if (device.getDeviceClass() == UsbConstants.USB_CLASS_COMM) //se il device è di tipo COM
            {
                for (int i = 0; i < device.getInterfaceCount(); i++)
                    if (device.getInterface(i).getInterfaceClass() == UsbConstants.USB_CLASS_CDC_DATA)
                    {
                        USB_INTERFACE = device.getInterface(i);
                        USB_DEVICE = device;
                        break;
                    }
                break;
            }
        }

        if(USB_INTERFACE == null)                                       //se non sono riuscito ad ottenere l'interfaccia del device
        {
        	STATO =  false;
        	return;
        }

        USB_DEVICE_CONNECTION = USB_MANAGER.openDevice(USB_DEVICE);     //apro la connessione con il device

        UsbEndpoint ep;

        for (int i = 0; i < 2; ++i)                                     //vado ad ottenere i 2 endPoint del device
        {
            ep = USB_INTERFACE.getEndpoint(i);
            if (ep.getDirection() == UsbConstants.USB_DIR_IN)
                USB_ENDPOINT_IN = ep;
             else
                USB_ENDPOINT_OUT = ep;
        }
        if (USB_ENDPOINT_IN == null || USB_ENDPOINT_OUT == null)        //se non sono riuscito ad ottenere uno dei 2 end point
        {
        	STATO =  false;
        	return;
        }

        if(ImpostaBaudrate(BAUDRATE) == false)
        {
        	STATO =  false;
        	return;
        }

        STATO = true;
    }

    @Override
    public void Disconnetti()
    {
    	STATO = false;
        if(USB_DEVICE_CONNECTION != null)
		    USB_DEVICE_CONNECTION.releaseInterface(USB_INTERFACE);

        if(USB_DEVICE_CONNECTION != null)
            USB_DEVICE_CONNECTION.close();
			
        USB_INTERFACE = null;
        USB_DEVICE = null;
        USB_ENDPOINT_IN = null;
        USB_ENDPOINT_OUT = null;
        USB_DEVICE_CONNECTION = null;
    }

    @Override
    public void Write(byte[] buffWrite)
    {
    	TempWriteLen = USB_DEVICE_CONNECTION.bulkTransfer(USB_ENDPOINT_OUT, buffWrite, buffWrite.length, 0);
    }

    @Override
    public void Read(byte[] buffRead)
    {
    	TempReadLen = USB_DEVICE_CONNECTION.bulkTransfer(USB_ENDPOINT_IN, buffRead, buffRead.length, 100); // RX
    }
    
    @Override
    public boolean Connesso()
    {
    	return STATO;
    }
    
	@Override
	public int WriteLength() {
		return TempWriteLen;
	}

	@Override
	public int ReadLength() {
		return TempReadLen;
	}
    
    public void BaudratePermesso(int baudrate, PendingIntent permesso)
    {
    	BAUDRATE = baudrate;
    	PERMESSO = permesso;
    }
    
    public String IdProduttore()
    {
    	if(USB_DEVICE != null)
    		return Integer.toString(USB_DEVICE.getProductId());
    	
    	return null;
    }
    
    public String IdVenditore()
    {
    	if(USB_DEVICE != null)
    		return Integer.toString(USB_DEVICE.getVendorId());
    	
    	return null;
    }
    
    private boolean ImpostaBaudrate(int Baudrate)
    {
        if(USB_DEVICE_CONNECTION.claimInterface(USB_INTERFACE, true) == false)
            return false;

		if(USB_DEVICE_CONNECTION.controlTransfer(0x21, 0x22, 0x00, 0, null, 0, 0) == -1)     //inizializzo la connessione
            return false;

        byte[] baudByte = new byte[7];

        baudByte[0] = (byte) (Baudrate & 0x000000FF);
        baudByte[1] = (byte) ((Baudrate & 0x0000FF00) >> 8);
        baudByte[2] = (byte) ((Baudrate & 0x00FF0000) >> 16);
        baudByte[3] = (byte) ((Baudrate & 0xFF000000) >> 24);
        baudByte[4] = 0x00;
        baudByte[5] = 0x00;
        baudByte[6] = 0x08;

        if(USB_DEVICE_CONNECTION.controlTransfer(0x21, 0x20, 0, 0, baudByte, 7, 0) == -1)    //imposto il baudrate
            return false;

        return true;
    }

}
