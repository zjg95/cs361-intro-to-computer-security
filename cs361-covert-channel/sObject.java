public class sObject {
	protected final String name;
	protected final SecurityLevel level;
	protected String value;
	public sObject(String name, SecurityLevel l){
		this.name = name;
		level = l;
		value = "0";
	}
	public SecurityLevel getSecurityLevel(){
		return level;
	}
	public void setValue(String v){
		value = v;
	}
	public String getName(){
		return name;
	}
	public String toString(){
		return ""+name+" "+level+" "+value;
	}
	public String getValue(){
		return value;
	}
}