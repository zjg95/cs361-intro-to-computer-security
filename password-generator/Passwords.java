// last modified: 11/20 @12:40pm

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Passwords {

    private static final boolean debug = false;

    private static FileInputStream input;

    private static FileWriter fwriter;
    private static PrintWriter pwriter;

    private static int n, k;

    private static int[][] followersTable;
    private static int[] startersTable;

    private static HatGrabber hatGrabber;

	public static void main(String[] args){
		// Check if the arguments are valid
		checkArgs(args);

		// Open the input file
		openInputs(args[0]);

		// Open the output file
		openOutput("output");

		// Create the followers table
		createTables();

		// Generate the passwords
		generate();

		// Close the input and output files
		closeAll();

		msg("Process completed successfully!\n");
	}
	private static void createTables() {
		/*
			Creates the followers table from
			the reference file
		*/
		msg("Creating followers table...");

		// create the tables
		followersTable = new int[26][26];
		startersTable = new int[26];

		// initialize all values to zero
		for (int i = 0; i<26; i++){
			startersTable[i] = 0;
			for (int j =0; j<26; j++){
				followersTable[i][j] = 0;
			}
		}

		// define the first and second letters
		int first, second;
		try{
			// initialize first and second
			first = (int)input.read();
			second = (int)input.read();

			// check to see if the first character is a letter
			if (isLetter(first))
				// add it to the starters table
				startersTable[makeIndex(first)]++;

			// iterate through the rest of the file
			while(first != -1 && second != -1) {
				// make sure the values are letters
				if (!isLetter(first)||!isLetter(second)){
					// check to see if the second is a starter
					if (first==' ' && isLetter(second)){
						// add to the starter count
						startersTable[makeIndex(second)]++;
					}
					// advance forward
					first = second;
					second = input.read();
					continue;
				}

				// update the count
				followersTable[makeIndex(first)][makeIndex(second)]++;

				// advance forward
				first = second;
				second = input.read();
			}
		} catch (IOException e){
			System.out.println("(Error) Unable to read from file!");
			System.exit(-1);
		}

		// print the tables
		printFollowersTable();
		printStarters();
		
		msg("done!\n");
	}
	private static void generate(){
		/*
			Generates passwords by creating a
			new HatGrabber object. The HatGrabber
			is later fed the appropriate table
			to grab values from. This method simply
			compiles all the characters into a string.
		*/
		msg("Passwords:\n");
		hatGrabber = new HatGrabber();
		for (int i = 0; i<n; i++){
			// generate n passwords
			String password = "";
			// feed -1 into nextLetter() to get a starter character
			char previous = nextLetter((char)0);
			// add it to the password
			password += previous;
			for (int j = 0; j<k; j++){
				// make it of length k
				msg("previous: '"+previous+"'\n");
				previous = nextLetter(previous);
				// add it to the password
				password +=previous;
			}
			// write the output
			System.out.println(password);
			pwriter.println(password);
		}
	}
	private static char nextLetter(char previous){
		/*
			Gets the mos probabable next letter,
			given the previous letter. If the previous
			letter is null, the most probable letter
			will be drawn from the starters pool. Otherwise
			it will be drawn from the followers pool.
		*/
		int index = (previous==0)? hatGrabber.getMostLikelyIndex(startersTable) : hatGrabber.getMostLikelyIndex(followersTable[makeIndex(previous)]);
		char c = makeLetter(index);
		return c;
	}
	private static boolean isLetter(int c){
		/*
			Check the given character to see
			if its ascii value is that of a
			letter (capital or small).
		*/
		return (c<'A'||(c>'Z' && c<'a')||c>'z')? false: true;
	}
	private static int makeIndex(int c){
		/*
			Subtract the lowest possible letter from
			the first and the second. Since the counts
			are stored in an array, we can use the actual
			letters and indexes to that array instead of
			having to use a switch or conditional to figure
			out the corresponding number.
		*/
		return (c<'a')?c-'A':c-'a';
	}
	private static char makeLetter(int c){
		return (char)(c +'a');
	}
	private static void printStarters(){
		/*
			Prints the starters table
		*/
		System.out.println("starters:");
		for (int i : startersTable)
			System.out.printf("%3d ",i);
		System.out.println();
		char chara = 'a';
		for (int i : startersTable){
			System.out.printf("%3c ",chara++);
		}
		System.out.println();
	}
	private static void printFollowersTable(){
		System.out.println();
		char c = 'A';
		System.out.printf("%2s","");
		for (int i = 0; i<26; i++){
			System.out.printf("%4c",c++);
		}
		System.out.println();
		c = 'A';
		for (int i = 0; i<26;i++){
			System.out.printf("%c:",c++);
			for (int j = 0; j<26; j++) {
				System.out.printf("%4d",followersTable[i][j]);
			}
			System.out.println();
		}
	}

	/* General use methods, used by main() */
	private static void openOutput(String text){
		/*
			Creates the output file
		*/
		msg("Creating output...");
		try {
			File file = new File(text);
			fwriter = new FileWriter(file);
			pwriter = new PrintWriter(file);
		} catch (IOException e){
			System.out.printf("(Error) Could not create '%s'\n",text);
			System.exit(-1);
		}
		msg("done!\n");
	}
	private static void openInputs(String key){
		/*
			Opens the inputs
		*/
		msg("Opening inputs...");
		try {
			input = new FileInputStream(new File(key));
		} catch (FileNotFoundException e){
			System.out.printf("(Error) Could not open '%s'\n",key);
			System.exit(-1);
		}
		msg("done!\n");
	}
	private static void closeAll(){
		/*
			Closes all the input/outputs files
		*/
		msg("Closing output...");
		try {
			input.close();

			pwriter.close();
			fwriter.close();
		} catch (IOException e){
			System.out.printf("(Error) Could not close files!\n");
			System.exit(-1);
		}
		msg("done!\n");
	}
	private static void checkArgs(String[] args){
		/*
			Method to determine if the user entered the
			correct number of arguments/flags
		*/
		msg("Checking args...");
		if (args.length!=3){
			System.out.println("(Error) Usage: java Passwords <reference-filename> <N> <k>");
			System.exit(-1);
		}
		try{
			n = Integer.parseInt(args[1]);
			k = Integer.parseInt(args[2]);
			msg("n = "+n+"\n");
			msg("k = "+k+"\n");
		} catch (NumberFormatException e){
			System.out.println("(Error) Unable to parse N or K!");
			System.exit(-1);
		}
		if (n<1||k<1){
			System.out.println("(Error) N and K must both be positive!");
			System.exit(-1);
		}
		msg("done!\n");
	}
	public static void msg(String p){
		/*
			Method to print debugging statements.
			Toggled on/off by the debug boolean
		*/
		if (debug)
			System.out.print(p);
	}
}
