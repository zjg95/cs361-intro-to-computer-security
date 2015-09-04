import java.util.concurrent.atomic.AtomicBoolean;
public class Timer {
	private Thread t;
	private long milisec;
	private int seconds;
	private int minutes;
	private int hours;
	private AtomicBoolean running;
	public Timer(){
		t = new Thread(new TimerThread());
		milisec = seconds = minutes = hours = 0;
		running = new AtomicBoolean(false);
	}
	public void start(){
		running.set(true);
		t.start();
	}
	public synchronized void stop(){
		running.set(false);
	}
	public synchronized String getTime(){
		String time = "";
		if (minutes>0)
			time+=minutes+"min ";
		if (seconds>0)
			time+=seconds+"sec";
		else
			time+=milisec+"ms";
		return time;
	}
	private synchronized void increment(){
		if (++milisec==1000){
			milisec = 0;
			if (++seconds==60){
				seconds = 0;
				minutes++;
			}
		}
	}
	private class TimerThread implements Runnable {
		public void run(){
			while (running.get()){
				try{
					Thread.sleep(1);
					increment();
				} catch (InterruptedException e){}
			}
		}
	}
}
