public class Level extends Thread {

	private UserList userList;
	private String dictionaryWord;
	private int maxLevel;

	public Level(UserList u, String s, int m){
		dictionaryWord = s;
		maxLevel = m;
		userList = u;
	}
	private void nextLevel(String s, int currentLevel){
		if (currentLevel>maxLevel || s==null)
			return;
		String mangled;
		for (int i = 0; i<13; i++){
			if (i<2){
				for (int j = 32; j<126; j++){
					mangled = mangle(s, i, j);
					compare(mangled);
					nextLevel(mangled,currentLevel+1);
				}
			}
			else {
				mangled = mangle(s, i, 0);
				compare(mangled);
				nextLevel(mangled,currentLevel+1);
			}
		}
	}
	private String mangle(String word, int method, int ascii_letter){
		return StringTransformer.transform(word,method,ascii_letter);
	}
	private void compare(String word){
		if (word==null)
			return;
		userList.checkUsers(word);
	}
	public void run(){
		nextLevel(dictionaryWord, 0);
	}
}