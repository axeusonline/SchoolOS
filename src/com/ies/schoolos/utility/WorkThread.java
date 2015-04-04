package com.ies.schoolos.utility;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class WorkThread extends Thread {
	
	private Window window;
	
	private HorizontalLayout progressLayout;
	private ProgressBar progress;
	private Label status;
	
	public WorkThread() {
		window = new Window("กรุณารอสักครู่");
		window.setWidth("100px");
		window.setHeight("70px");
		window.center();
		UI.getCurrent().addWindow(window);
		
		progressLayout = new HorizontalLayout();
		progressLayout.setSizeFull();
		window.setContent(progressLayout);
		
		progress = new ProgressBar(new Float(0.0));
		progress.setEnabled(false);
		progressLayout.addComponent(progress);
		progressLayout.setComponentAlignment(progress, Alignment.MIDDLE_CENTER);
		
		status = new Label("not running");
		progressLayout.addComponent(status);
		progressLayout.setComponentAlignment(status, Alignment.MIDDLE_CENTER);
	}
	
    // Volatile because read in another thread in access()
    volatile double current = 0.0;

    @Override
    public void run() {
    	progress = new ProgressBar(new Float(0.0));
		progress.setEnabled(false);
		
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
                            ((int)(current*100)) + "% เสร็จสิ้น");
                    else{
                        status.setValue("เสร็จสิ้น");
                        window.close();
                    }
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
                
                status.setValue("กรุณารอสักครู่");
            }
        });
    }
}
