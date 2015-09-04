import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;
public class CovertChannel {

	private static Scanner sc;

	private static SecurityLevel low;
	private static SecurityLevel high;

	private static ObjectManager objMngr;

	private static StringTokenizer tok;
	private static String currentToken;

	private static boolean log;

	private static FileWriter fwriter = null;
	private static PrintWriter pwriter = null;
	private static FileWriter logWriter = null;
	private static PrintWriter logPrinter = null;

	private static FileInputStream in = null;
    private static FileOutputStream out = null;

    private static Timer timer;

	public static void main(String[] args) throws IOException {
		if (args.length<1){
			System.out.println("Error!\nUsage: java CovertChannel <inputFile>");
			return;
		}
		int argnum = 0;

		if (args.length>0&&("v").equals(args[0])){
			if (args.length<2){
				System.out.println("Error!\nUsage: java CovertChannel <v> <inputFile>");
				return;
			}
			log = true;
			argnum++;
			File logFile = new File("log");
			logWriter = new FileWriter(logFile);
			logPrinter = new PrintWriter(logFile);
			System.out.println("logging activity");
		}
		else log = false;			
		String name;
		if (log)
			name = args[1];
		else
			name = args[0];

		File ouputFile = new File(name+".out");
		fwriter = new FileWriter(ouputFile);
		pwriter = new PrintWriter(ouputFile);
		File inFile = new File(args[argnum]);
		
		try{
			in = new FileInputStream(inFile);
			out = new FileOutputStream(name+".out");

		} catch(FileNotFoundException e){
			System.out.println("Error! File not found!");
			return;
		}

		low  = new SecurityLevel();
		high = new SecurityLevel(1);

		objMngr = new ObjectManager(log);

		// We add two subjects, one high and one low.

		Subject lyle = new Subject("Lyle", low);
		lyle.setFileWriter(out); // specifies the file output

		objMngr.createSubject(lyle);
		objMngr.createSubject(new Subject("Hal", high));

		// Parses and runs instructions until the end of the file is reached
		int c;
		long bitCount = 0;
		if (!log){
			timer = new Timer();
			timer.start();
		}
		while((c = in.read()) != -1){
			//parse(sc.nextLine());

			byte[] bytes = new byte[8];
			for (int i = 0; i<8; i++){
				bytes[i] = 0;
				bytes[i] |= (c >> (7-i))&1;
				if (bytes[i]==0){
					if (log)
						logPrinter.println("create Hal obj");
					parse("create Hal obj");
				}
				parse("create lyle obj");
				parse("write lyle obj 1");
				parse("read lyle obj");
				parse("destroy lyle obj");
				parse("run lyle");
				parse("destroy Hal obj");
				if (log){
					logPrinter.println("create lyle obj");
					logPrinter.println("write lyle obj 1");
					logPrinter.println("read lyle obj");
					logPrinter.println("destroy lyle obj");
					logPrinter.println("run lyle");
					logPrinter.println("destroy Hal obj");
				}
			}
			bitCount+=8;
		}
		if (!log){
			timer.stop();
			System.out.printf("Size: %d bits\nTime: %d ms\nBandwidth: %d bits/ms",bitCount,timer.getTime(), (int)(bitCount/timer.getTime()));
		}

		if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
		if (pwriter!=null){
			fwriter.close();
			pwriter.close();
		}
		if (log){
			logPrinter.close();
			logWriter.close();
		}
	}
	private static void parse(String line){
		// Fetches next line of the file
		tok = new StringTokenizer(line, " ");
		if (!nextToken()){
			// StringTokenizer cannot get anything from the string
			createBadInstruction();
			return;
		}
		doCommand(currentToken);
	}
	private static boolean nextToken(){
		// checks if StringTokenizer has additional tokens
		if (!(tok.hasMoreTokens()))
			return false;
		currentToken = tok.nextToken();
		return true;
	}
	private static void doCommand(String command){
		// parses command, executes if valid
		command = command.toLowerCase();
		switch (command){
			case("read"): createRead(command); break;
			case("write"): createWrite(command); break;
			case("create"): createCreate(command); break;
			case("destroy"): createDestroy(command); break;
			case("run"): createRun(command); break;
			default: createBadInstruction(); // command is invalid
		}
	}

	// used by the command methods
	private static boolean getArguments(String[] args){
	// parses additional arguments, if they exist
		for (int i = 0; i<args.length;i++){
			// checks to see if the desired argument exists
			if (!nextToken()){
				createBadInstruction();
				return false;
			}
			args[i]= currentToken;
		}
		// checks to see if too many arguments exist
		if (nextToken()){
			createBadInstruction();
			return false;
		}
		return true;
	}

	/* 
		Commands:
		read, write, create, destroy, run, badInstruction

		The following methods create InstructionObjects to be delivered to the ObjectManager
		for execution.
	*/

	private static void createRead(String command){
		String[] args = new String[2];
		if (getArguments(args))
			objMngr.execute(new InstructionObject(command, args[0],args[1], null));
	}
	private static void createWrite(String command){
		String[] args = new String[3];
		if (getArguments(args))
			objMngr.execute(new InstructionObject(command, args[0],args[1],args[2]));
	}
	private static void createCreate(String command){
		String[] args = new String[2];
		if (getArguments(args))
			objMngr.execute(new InstructionObject(command, args[0],args[1], null));
	}
	private static void createDestroy(String command){
		String[] args = new String[2];
		if (getArguments(args))
			objMngr.execute(new InstructionObject(command, args[0],args[1], null));
	}
	private static void createRun(String command){
		String[] args = new String[1];
		if (getArguments(args))
			objMngr.execute(new InstructionObject(command, args[0], null, null));
	}
	private static void createBadInstruction(){
		objMngr.execute(null);
	}
}