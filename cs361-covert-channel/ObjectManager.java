import java.io.*;
import java.util.ArrayList;
public class ObjectManager {
	private final ReferenceMonitor refMon;
	private final ArrayList<Subject> subjects;
	private final ArrayList<sObject> objects;
	private final boolean debug = false; // change this to toggle on/off debug statements
	private boolean log;

	private Subject subject;
	private sObject object;

	public ObjectManager(boolean t){
		refMon = new ReferenceMonitor();
		subjects = new ArrayList<Subject>();
		objects = new ArrayList<sObject>();
		object = null;
		subject = null;
		log = t;
	}
	public ReferenceMonitor getReferenceMonitor(){
		return refMon;
	}
	public void createObject(sObject object1){
		objects.add(object1);
		if (debug)
			System.out.printf("Successfully created object %s\n",object1);
	}
	public void createSubject(Subject subject1){
		subjects.add(subject1);
		if (debug)
			System.out.printf("Successfully created subject %s\n",subject1);
	}
	public void removeObject(sObject object1){
		objects.remove(object1);
		if (debug)
			System.out.printf("Successfully removed object %s\n",object1);
	}
	public void removeSubject(Subject subject1){
		subjects.remove(subject1);
		if (debug)
			System.out.printf("Successfully removed subject %s\n",subject1);
	}
	private boolean getSubject(String name){
		for (Subject s: subjects){
			if (s.getName().equalsIgnoreCase(name)){
				subject = s;
				return true;
			}
		}
		subject = null;
		if (debug)
			System.out.println("Fetch failed. Subject does not exist!");
		return false;
	}
	private boolean getObject(String name){
		for (sObject o: objects){
			if (o.getName().equalsIgnoreCase(name)){
				object = o;
				return true;
			}
		}
		object = null;
		if (debug)
			System.out.println("Fetch failed. Object does not exist!");
		return false;
	}

	public void execute(InstructionObject instr){
		// check to see if instruction is null
		if (instr==null){
			if (debug){
				System.out.println("Bad instruction");
				printState();
			}
			return;
		}

		switch(instr.type){
			case("read"): read(instr); break;
			case("write"): write(instr); break;
			case("run"): run(instr); break;
			case("create"): create(instr); break;
			case("destroy"): destroy(instr); break;
			default: System.out.println("unexpected error. Instruction type is unknown."); return;
		}
		if (debug)
			printState();
	}


	private void read(InstructionObject instr){
		if (!getSubject(instr.subjectName)){
			return;
		}
		if (!getObject(instr.objectName)){
			subject.setValue("0");
			return;
		}
		boolean legal = refMon.executeRead(subject, object);
		String value;
		if (debug)
		System.out.printf("%s reads from %s\n",subject.getName(),object.getName());
		if (legal){
			value = object.getValue();
		}
		else {
			if (debug)
				System.out.printf("%s is not authorized to read from %s\n",subject.getName(),object.getName());
			value = "0";
		}
		subject.setValue(value);
	}
	private void write(InstructionObject instr){
		if (!getSubject(instr.subjectName)){
			return;
		}
		if (!getObject(instr.objectName)){
			subject.setValue("0");
			return;
		}
		boolean legal = refMon.executeWrite(subject, object);
		String value = instr.value;
		if (debug)
		System.out.printf("%s writes value %s to %s\n",subject.getName(),value,object.getName());
		if (legal){
			object.setValue(value);
		}
		else {
			if (debug)
				System.out.printf("%s is not authorized to write to %s\n",subject.getName(),object.getName());
		}
	}
	private void run(InstructionObject instr){
		if (!getSubject(instr.subjectName)){
			return;
		}
		try{
		subject.run();
		} catch (IOException e){}
	}
	private void create(InstructionObject instr){
		if (!getSubject(instr.subjectName)){
			return;
		}
		if (getObject(instr.objectName)){
			if (debug)
			System.out.printf("Cannot create %s; object already exists!\n",instr.objectName);
			return;
		}
		createObject(new sObject(instr.objectName, subject.getSecurityLevel()));
		
	}
	private void destroy(InstructionObject instr){
		if (!getSubject(instr.subjectName)){
			return;
		}
		if (!getObject(instr.objectName)){
			return;
		}
		boolean legal = refMon.executeWrite(subject, object);
		if (legal){
			removeObject(object);
		}
		else {
			if (debug)
				System.out.printf("%s is not authorized to destroy %s\n",subject.getName(),object.getName());
		}
	}


	private void printState(){
		System.out.println("The current state is:");
		for (sObject o: objects)
			System.out.printf("  %s has value: %s\n",o.getName(),o.getValue());
		for (Subject s: subjects)
			System.out.printf("  %s has recently read: %s\n",s.getName(),s.getValue());
	}

	// Reference Monitor class
	private class ReferenceMonitor {
		public boolean executeWrite(Subject subject, sObject object){
			SecurityLevel sLevel = subject.getSecurityLevel();
			SecurityLevel iLevel = object.getSecurityLevel();
			return (sLevel.dominatedBy(iLevel));
		}
		public boolean executeRead(Subject subject, sObject object){
			SecurityLevel sLevel = subject.getSecurityLevel();
			SecurityLevel iLevel = object.getSecurityLevel();
			return (sLevel.dominates(iLevel));
		}
	}
}