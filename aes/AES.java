// last modified: 11/6/14 3:36pm

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AES {

	// Data structure to represent the current mode
	private enum Mode {
    	ENCODE,	DECODE
    }

    // Array of hex values. Used for outputting the cipher text to the file.
	private static final String[] hexValues = new String[]{
		"0","1","2","3","4","5","6","7",
		"8","9","A","B","C","D","E","F"
	};

    private static final boolean debug = false;
    private static final boolean timing = true;

    private static Scanner inText;
    private static Scanner inKey;
    private static FileOutputStream output;

    private static FileWriter fwriter;
    private static PrintWriter pwriter;

    private static Mode mode;

    private static Timer timer;

    private static long bytes;

	public static void main(String[] args){
		// Check if the arguments are valid
		checkArgs(args);

		// Check the first argument to determine the mode
		getMode(args[0]);
		
		// Open the input files
		openInputs(args[1],args[2]);

		// Open the output file
		openOutput(args[2]);

		bytes = 0;
		if (timing){
			timer = new Timer();
			timer.start();
		}

		switch (mode) {
			case DECODE: decode(); break;
			case ENCODE: encode(); break;
		}

		// Print the timer statistics
		if (timing)
			timerStats();

		// Close the input and output files
		closeAll();

		msg("Process completed successfully!\n");
	}

	/* Encryption/decryption methods */
	private static void encode(){
		/*
			Main encryption method. Pulls a block
			from the input, expands the key, and
			then runs the encoding process to produce
			the encrypted text. Outputs it to the
			file. Used only in encoding.
		*/
		msg("Encoding...\n");

		// get the cipher key from the file
		Block key = getKey();
		// check to make sure the key was properly parsed
		if (key==null){
			msg("key == null. Continuing to next line.\n");
			return;
		}
		// expand it for use later in the algorithm
		int[][] expandedKey = expandKey(key.getBlocks());
		int count = 0;
		// read input blocks from file as long as they exist
		while (inText.hasNext()){
			if (!timing)	
				System.out.printf("Block %d:\n",count++);	

			// get a block object (state) to represent a block from the file
			Block state = getBlock(inText);
			if (state==null){
				msg("State == null. Continuing to next line.\n");
				continue;
			}

			// add the cipher key to the state
			state.addRoundKey(key);

			// enter the main rounds of the algorithm, run 9 cycles
			for (int i = 1; i<10; i++){
				//System.out.printf("Starting round %d\n",i);
				state.subBytes();
				state.shiftRows();
				state.mixColumns();
				state.addRoundKey(getRoundKey(expandedKey,i));
			}
			// after the main 9 rounds have completed, add the final encryption layer 
			state.subBytes();
			state.shiftRows();
			state.addRoundKey(getRoundKey(expandedKey,10));
			if (!timing)	
				state.print();
			bytes+=16;

			// output the final encrypted text to the output file
			output(state);
		}
		msg("done encoding!\n");
	}
	private static void decode(){
		/*
			Main decryption method. Pulls a block
			from the input, expands the key, and
			then runs the encoding process in reverse
			to produce the unencrypted text. Outputs
			it to the file. Used only in decoding.
		*/
		msg("Decoding...");

		// get the cipher key from the file
		Block key = getKey();
		// check to make sure the key was properly parsed
		if (key==null){
			msg("key == null. Continuing to next line.\n");
			return;
		}
		// expand it for use later in the algorithm
		int[][] expandedKey = expandKey(key.getBlocks());
		int count = 0;
		// read input blocks from file as long as they exist
		while (inText.hasNext()){
			if (!timing)	
				System.out.printf("Block %d:\n",count++);

			// get a block object (state) to represent a block from the file
			Block state = getBlock(inText);
			if (state==null){
				msg("State == null. Continuing to next line.\n");
				continue;
			}

			// remove the outer encryption layer
			state.addRoundKey(getRoundKey(expandedKey,10));
			state.shiftRowsInverse();
			state.subBytesInverse();

			// enter the main rounds of the algorithm, run 9 cycles
			for (int i = 9; i>0; i--){
				//System.out.printf("Starting round %d\n",i);
				state.addRoundKey(getRoundKey(expandedKey,i));
				state.mixColumnsInverse();
				state.shiftRowsInverse();
				state.subBytesInverse();
			}
			// after the main 9 rounds have completed, remove the final encryption layer 
			state.addRoundKey(key);
			if (!timing)	
				state.print();
			bytes+=16;

			// output the final encrypted text to the output file
			output(state);
		}
		msg("done decoding!\n");
	}
	private static void output(Block state){
		/*
			Outputs a block to a file. Prints
			the values in hex to the file.
			Used in decoding and encoding
		*/
		int[][] array = state.getBlocks();
		for (int i = 0; i<4; i++){
			for (int j = 0; j<4; j++){
				int total = array[i][j];
				int x = total>>4;
				int y = total & 0xF;
				pwriter.print(""+hexValues[x]+hexValues[y]);
			}
		}
		if (inText.hasNext())
			pwriter.println("");
	}

	/* Block handling methods */
	private static String getBitString(Scanner sc){
		/*
			Reads from the input file and obtains the
			hex value string. Expects a 32 character
			string. Used in decoding and encoding
		*/
		String string = sc.nextLine();
		StringTokenizer tok = new StringTokenizer(string);
		String copy = "";
		// Trim out the spaces
		while (tok.hasMoreTokens())
			copy+=tok.nextToken();
		int length = copy.length();
		if (length!=32){
			// the string is of an unexpected size
			msg("string length = "+length+"\n");
			if (length>32){
				// Truncate the message
				System.out.print("(Warning) Message too long. Truncating message");
				copy = copy.substring(0,32);
			}
			else if (length==0){
				return null;
			}
			else {
				// Pad the message with null bytes
				System.out.println("(Warning) Message too short. Padding message");
				do {
					copy+="0";
				} while (++length<32);
				System.out.println("done padding");
			}
		}
		return copy;
	}
	private static Block getBlock(Scanner sc){
		/*
			Inputs a scanner that is used to acquire a
			string of hex values. The string is parsed
			and the method returns a new block object composed
			of those hex values. Used in decoding and encoding
		*/
		String string = getBitString(sc);
		// safety check: if no string was parsed
		if (string==null)
			return null;
		int hexValues[] = new int[16];
		boolean good = getHex(string,hexValues);
		// safety check: if hex values were not parsed
		if (!good)
			return null;
		Block state = new Block(hexValues);
		return state;
	}
	private static boolean getHex(String string, int[] array){
		/*
			Parses a string to obtain hex values
			and stores the values into an array.
			Used in decoding and encoding
		*/
		int index = 0;
		for (int i = 0; i<32; i+=2){
			try{
				array[index++] = Integer.parseInt(string.substring(i,i+2), 16);
			}catch (NumberFormatException e){
				System.out.printf("(Error) Non-hex values found in string (%s)\n",string);
				return false;
			}
		}
		return true;
	}

	/* Key handling methods */
	private static Block getKey(){
		/*
			Loads the cipher key from the input file
			specified by the user. Expects to receive
			a 32 character hex key. Used in decoding
			and encoding
		*/
		if (!(inKey.hasNext())){
			System.out.println("Error! Key not found!");
			System.exit(-1);
		}
		Block key = getBlock(inKey);
		return key;
	}
	private static int[][] expandKey(int[][] keyBlock){
		/*
			Key expansion algorithm. Inputs the cipher
			key (4x4 array) and expands it into the full
			44x4 array that is used for the addRoundKey()
			portion of encryption/decryption.
			Used in decoding and encoding
		*/
		msg("Expanding key...");
		int[][] expandedKey = new int[44][4];
		for (int i = 0; i<44; i++)
			for (int j = 0; j<4; j++)
				expandedKey[i][j] = 0;
		for (int i = 0; i<4; i++)
			expandedKey[i] = keyBlock[i];
		int column = 4;

		int rcon = 0;
		for (int round = 0; round<10; round++){
			//System.out.printf("Starting round %d\n",round);
			int endColumn = column+4;
			int[] rconCol = Rcon.getColumn(rcon++);
			boolean first = true;
			for (; column<endColumn; column++){
				//System.out.printf("working with column #%d\n",column);
				int[] currentCol = new int[4];
				expandedKey[column] = currentCol;
				int[] rotword = expandedKey[column-1];
				// if it's the first round
				if (first){
					// shift bytes on rot word
					int temp = rotword[0];
					for (int j = 0; j<3; j++)
						currentCol[j] = rotword[j+1];
					currentCol[3]= temp;
					// sub bytes on rot word
					for (int j = 0; j<4;j++)
						currentCol[j] = SBox.getSub(currentCol[j]);
				}
				// if it's not the first round
				else
					for (int j = 0; j<4; j++)
						currentCol[j] = rotword[j];
				// get the w-4 index
				int[] currentMinus4 = expandedKey[column-4];
				for (int j = 0; j<4;j++){
					//System.out.printf("0x%02x ^ 0x%02x\n",currentMinus4[j],currentCol[j]);
					currentCol[j] = currentCol[j]^currentMinus4[j];
					if (first){
						currentCol[j] ^= rconCol[j];
					}
				}
				if (first)
					first = false;
			}
		}
		msg("done!\n");
		return expandedKey;
	}
	private static Block getRoundKey(int[][] key, int x){
		/*
			Returns the desired "round" key from the
			whole expanded key. Inputs the entire expanded
			key, and an int to represent which round is desired.
			Used in decoding and encoding
		*/
		Block block;
		int[][] array = new int[][]{
			key[4*x],key[4*x+1],key[4*x+2],key[4*x+3]
		};
		int[] array2 = new int[16];
		int count = 0;
		for (int i = 0;i<4;i++)
			for (int j = 0; j<4; j++)
				array2[count++]=array[i][j];
		block = new Block(array2);
		return block;
	}
	private static void printKey(int[][] expandedKey){
		/*
			Used for debugging. Prints the full expanded key
		*/
		System.out.println("Expanded key:");
		int count = 0;
		for (int i= 0; i<4; i++){
			for (int j = 0; j<44; j++){
				System.out.printf("%02x ",expandedKey[j][i]);
				if (++count ==4){
					count = 0;
					System.out.print(" ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	/* General use methods, used by main() */
	private static void openOutput(String text){
		/*
			Creates the output file
		*/
		String ext = ".";
		switch (mode){
			case ENCODE: ext+="enc"; break;
			case DECODE: ext+="dec"; break;
		}
		msg("Creating '"+text+ext+"'...");
		try {
			File file = new File(text+ext);
			fwriter = new FileWriter(file);
			pwriter = new PrintWriter(file);
		} catch (IOException e){
			System.out.printf("(Error) Could not create '%s'\n",text+ext);
			System.exit(-1);
		}
		msg("done!\n");
	}
	private static void openInputs(String key, String text){
		/*
			Opens the inputs
		*/
		msg("Opening inputs...");
		try {
			inKey = new Scanner(new File(key));
		} catch (FileNotFoundException e){
			System.out.printf("(Error) Could not open '%s'\n",key);
			System.exit(-1);
		}
		try {
			inText = new Scanner(new File(text));
		} catch (FileNotFoundException e){
			System.out.printf("(Error) Could not open '%s'\n",text);
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
			inKey.close();
			inText.close();

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
		if (args.length!=3||!("e".equalsIgnoreCase(args[0])||"d".equalsIgnoreCase(args[0]))){
			System.out.println("(Error) Usage: java AES <option> <keyFile> <inputFile>");
			System.exit(-1);
		}
		msg("done!\n");
	}
	private static void getMode(String args){
		/*
			Method to determine which mode the user invoked.
		*/

		System.out.print("Mode: ");
		if ("e".equalsIgnoreCase(args)){
			// java AES e key plaintext
			mode = Mode.ENCODE;
			System.out.println("encode");
		}
		else if ("d".equalsIgnoreCase(args)) {
			// java AES d key plaintext.enc
			mode = Mode.DECODE;
			System.out.println("decode");
		}
		else{
			System.out.println("Unkown mode entered.");
			System.exit(-1);
		}
	}
	private static void timerStats(){
		/*
			Calculates average bandwidth of
			the encryption/decryption process
			and prints the statistics.
		*/
		timer.stop();
		System.out.printf("Total bytes: %d\n",bytes);
		bytes = (long)bytes/1024;
		long band = (long)(timer.getTime()/10);
		if (band<=0)
			band = 1;
		band = (long)(bytes/band);
		System.out.printf("Bandwidth: %dKB/sec\n",band);
	}
	private static void msg(String p){
		/*
			Method to print debugging statements.
			Toggled on/off by the debug boolean
		*/
		if (debug)
			System.out.print(p);
	}
}