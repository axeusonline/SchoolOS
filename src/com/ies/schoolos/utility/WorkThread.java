package com.ies.schoolos.utility;

import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;

/*public class WorkThread extends Thread {
	
	 int current = 1;
     public final static int MAX = 10;

     private ProgressBar progress;
     
     public WorkThread(ProgressBar progress) {
    	 this.progress = progress;
	}
     
     @Override
     public void run() {
         for (; current <= MAX; current++) {
             try {
                 Thread.sleep(1000);
             } catch (final InterruptedException e) {
                 e.printStackTrace();
             }
             synchronized (UI.getCurrent()) {
                 processed();
             }
         }
     }

     public int getCurrent() {
         return current;
     }
     
     public final void processed() {
    	 
         final int i = this.getCurrent();
         if (i == MAX) {
             UI.getCurrent().setPollInterval(-1);
        	 System.err.println("CURRENT 2");
             progress.setValue(0f);
             progress.setVisible(!progress.isIndeterminate());
         } else {
             progress.setValue((float) i / MAX);
         }
     }
}*/
public class WorkThread extends Thread {
    // Volatile because read in another thread in access()
    volatile double current = 0.0;
    private ProgressBar progress;
    private Label status;
    
    public WorkThread(ProgressBar progress,Label status) {
   	 this.progress = progress;
   	 this.status = status;
	}
    
    @Override
    public void run() {
        // Count up until 1.0 is reached
        while (current < 1.0) {
            current += 0.01;

            // Do some "heavy work"
            try {
                sleep(50); // Sleep for 50 milliseconds
            } catch (InterruptedException e) {}

            // Update the UI thread-safely
            UI.getCurrent().access(new Runnable() {
                @Override
                public void run() {
                    progress.setValue(new Float(current));
                    if (current < 1.0)
                        status.setValue("" +
                            ((int)(current*100)) + "% done");
                    else
                        status.setValue("all done");
                }
            });
        }
        
        // Show the "all done" for a while
        try {
            sleep(2000); // Sleep for 2 seconds
        } catch (InterruptedException e) {}

        // Update the UI thread-safely
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                // Restore the state to initial
                progress.setValue(new Float(0.0));
                progress.setEnabled(false);
                        
                // Stop polling
                UI.getCurrent().setPollInterval(-1);

                status.setValue("not running");
            }
        });
    }

    public void setCurrent(double current){
    	this.current = current;
    }
}

