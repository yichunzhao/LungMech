package icumatic.toolkit;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple demo that uses java.util.Timer to schedule a task to execute
 * once 5 seconds have passed.
 */

public class Reminder {
    Timer timer;

        public Reminder(TimerTask task, int ms) {
            timer = new Timer();
            timer.schedule(task, ms);
        }

    }
	
//    public static void main(String args[]) {
//	System.out.println("About to schedule task.");
//        new Reminder(5);
//	System.out.println("Task scheduled.");
//    }
//}
    class RemindTask extends TimerTask {
        public void run() {
            System.out.println("Time's up!");
	    //timer.cancel(); //Terminate the timer thread
        }
    }
