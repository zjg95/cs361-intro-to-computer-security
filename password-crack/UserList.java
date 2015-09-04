import java.util.LinkedList;
public class UserList {

	private LinkedList<User> users;
	private LinkedList<User> removeList;
	public int userNum;
	public int passCount;
	private Timer timer;

	public UserList(Timer t){
		users = new LinkedList<User>();
		removeList = new LinkedList<User>();
		userNum = 0;
		timer = t;
	}
	public synchronized void add(User u){
		users.addLast(u);
		userNum++;
	}
	private void removeUsers(){
		for (User u: removeList){
			users.remove(u);
			userNum--;
		}
		removeList.clear();
	}
	public synchronized void checkUsers(String password){
		for (User user : users){
			String encryptedWord = jcrypt.crypt(user.salt, password);
			if (user.encryptedPassword.equals(encryptedWord)){
				System.out.printf("%s = %s\n",user,password);
				System.out.printf(" Current time: %s\n",timer.getTime());
				removeList.add(user);
				passCount++;
			}
		}
		removeUsers();
	}
	public synchronized boolean hasUsers(){
		return userNum>0;
	}
}