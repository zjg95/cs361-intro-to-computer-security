public class InstructionObject {
	public String
		type, subjectName, objectName;
	public String value;
	public InstructionObject(String type,String sName, String oName, String v){
		this.type = type;
		subjectName = sName;
		objectName = oName;
		value = v;
	}
	public void setValue(String v){
		value = v;
	}
	public String toString(){
		return ""+type+" "+subjectName+" "+objectName;
	}
}