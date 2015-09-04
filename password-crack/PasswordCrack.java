// last modified: 12/4 @1:10am

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

public class PasswordCrack {

    private static Timer timer;

    private static Scanner passwordReader;
    private static Scanner dictionaryReader;

    private static HashSet<String> dictionary;
    private static UserList users;

	public static void main(String[] args){
		// Check if the arguments are valid
		checkArgs(args);

		// Open the input file
		openInputs(args);

		timer = new Timer();

		fillDictionary();

		getUsers();

		crackPasswords();

		// Close the input and output files
		closeAll();
	}
	private static void crackPasswords(){
		int level = 1;
		System.out.println("Beginning password crack.");
		timer.start();
		while (users.hasUsers()&&level<4){
			System.out.printf("Entering Level %d Search\n",level);
			System.out.printf(" Current time: %s\n",timer.getTime());

			nextLevel(level);

			level++;
		}
		timer.stop();
		System.out.printf("Total time: %s\n",timer.getTime());
		System.out.printf("%d out of %d passwords successfully cracked.\n",users.passCount,users.passCount+users.userNum);
	}
	private static void nextLevel(int maxLevel){
		ArrayList<Thread> threads = new ArrayList<Thread>();
		int threadCount = 0;
		for (String s: dictionary){
			Thread t = new Level(users, s, maxLevel-1);
			t.start();
			threads.add(t);
			if (++threadCount>=5){
				for (Thread thread: threads)
					try{thread.join();}catch(InterruptedException e){}
				threadCount = 0;
				threads.clear();
			}
		}
		for (Thread t: threads){
			try{t.join();}catch(InterruptedException e){}
		}
	}
	private static void getUsers(){
		users = new UserList(timer);
		while (passwordReader.hasNext()){
			StringTokenizer tok = new StringTokenizer(passwordReader.nextLine(),": ");
			String username = tok.nextToken();
			String password = tok.nextToken();
			String salt = password.substring(0,2);
			String random = tok.nextToken();
			random = tok.nextToken();
			String firstName = tok.nextToken();
			String lastName = tok.nextToken();
			User user = new User(firstName,lastName,salt,password);
			users.add(user);
			dictionary.add(firstName.toLowerCase());
			dictionary.add(lastName.toLowerCase());
		}
	}
	private static void fillDictionary(){
		dictionary = new HashSet<String>();
		while (dictionaryReader.hasNext()){
			String token = (dictionaryReader.nextLine()).toLowerCase();
			dictionary.add(token);
		}
	}

	/* General use methods, used by main() */
	private static void openInputs(String args[]){
		/*
			Opens the inputs
		*/
		try {
			passwordReader = new Scanner(new File(args[1]));
		} catch (FileNotFoundException e){
			System.out.printf("(Error) Could not open '%s'\n",args[0]);
			System.exit(-1);
		}
		try {
			dictionaryReader = new Scanner(new File(args[0]));
		} catch (FileNotFoundException e){
			System.out.println("(Error) Could not open words");
			System.exit(-1);
		}
	}
	private static void closeAll(){
		/*
			Closes all the input/outputs files
		*/
		passwordReader.close();
		dictionaryReader.close();
	}
	private static void checkArgs(String[] args){
		/*
			Method to determine if the user entered the
			correct number of arguments/flags
		*/
		if (args.length!=2){
			System.out.println("(Error) Usage: java PasswordCrack <dictionaryFile> <passwordFile>");
			System.exit(-1);
		}
	}
}
