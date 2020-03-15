package STCM;

abstract public class DRIVER {
	
	abstract public void Connetti();
	
	abstract public void Disconnetti();
	
	abstract public void Write(byte[] buffWrite);
	
	abstract public void Read(byte[] buffRead);
	
	abstract public boolean Connesso();
	
	abstract public int WriteLength();
	
	abstract public int ReadLength();
}
