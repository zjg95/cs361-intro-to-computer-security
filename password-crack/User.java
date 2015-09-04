public class User{
	public String
		firstName, lastName, encryptedPassword, plaintextPassword, salt;
	public User(String f, String l,String s, String p){
		firstName = f;
		lastName = l;
		salt = s;
		encryptedPassword = p;
	}
	public String toString(){
		return ""+firstName+" "+lastName+" "+encryptedPassword;
	}
}