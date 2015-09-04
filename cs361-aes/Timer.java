/*
	Timer class used for timing the throughput
	of the program.
*/
import java.util.concurrent.atomic.AtomicBoolean;
public class Timer {
	private Thread t;
	private long milisec;
	private AtomicBoolean running;
	public Timer(){
		t = new Thread(new TimerThread());
		milisec = 0;
		running = new AtomicBoolean(false);
	}
	public void start(){
		running.set(true);
		t.start();
	}
	public void stop(){
		running.set(false);
	}
	public long getTime(){
		return milisec;
	}
	private class TimerThread implements Runnable {
		public void run(){
			while (running.get()){
				try{
					Thread.sleep(100);
					milisec++;
				} catch (InterruptedException e){}
			}
		}
	}
}
