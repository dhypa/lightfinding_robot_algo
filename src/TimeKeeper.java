import java.util.Timer;
import java.util.TimerTask;

/**
 * Each timer instance tracks how long it has been instantiated for
 */
public class TimeKeeper {
	private static Timer timer;
	private static long timeElapsed;
	private static long startTime;

	public TimeKeeper() {
		startTimer();
	}

	private static void startTimer() {
		timer = new Timer();
		startTime = System.currentTimeMillis();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override 
			public void run() {
				timeElapsed = System.currentTimeMillis() - startTime;
			}
		},0,100);
	}
	
	
	public void resetTimer() {
		timer.cancel();
		startTimer();
	}

	public long getTimeElapsed() {
		return timeElapsed;
	}
	public void close() {
		timer.cancel();
	}
}
