import java.io.*;
public class Subject extends sObject {
	private FileOutputStream out;
	public byte[] bytes;
	private int bitCount;
	private byte result;
	public Subject(String name, SecurityLevel l) {
		super(name,l);
		bytes = new byte[8];
		bitCount = 0;
	}
	public void setFileWriter(FileOutputStream f){
		out = f;
	}
	public void run() throws IOException{
		if ("Hal".equals(name)){
			return;
		}
		else{ //lyle
			if (out ==null)
				return;
			bytes[bitCount] = (byte)Integer.parseInt(value);
			if (bitCount==7){
				result = 0;
				for (int i = 0; i<8; i++){
					result |= (bytes[i] << (7-i));
				}
				out.write(result);
				bitCount = 0;
			}
			else
				bitCount++;
		}
	}
}