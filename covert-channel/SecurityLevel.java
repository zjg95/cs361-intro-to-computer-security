public class SecurityLevel {
	private final int level; // making this int final prevents changes to security level
	public SecurityLevel(){
		level = 0;
	}
	public SecurityLevel(int l){
		if (l >=1)
			level = 1;
		else level = 0;
	}
	public boolean dominates(SecurityLevel other){
		int l = other.getLevel();
		if (level >= l)
			return true;
		return false;
	}
	public boolean dominatedBy(SecurityLevel other){
		int l = other.getLevel();
		if (level <= l)
			return true;
		return false;
	}
	public int getLevel(){
		return level;
	}
	public String toString(){
		if (level == 0)
			return "LOW";
		return "HIGH";
	}
}