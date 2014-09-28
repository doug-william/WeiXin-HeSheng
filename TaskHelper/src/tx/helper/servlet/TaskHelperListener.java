package tx.helper.servlet;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import tx.helper.service.HelperService;

public class TaskHelperListener implements ServletContextListener {
	
    public TaskHelperListener() {}

    public void contextInitialized(ServletContextEvent arg0) {
    	
    	tx.commons.util.Util.getInstance().info(this, "TaskHelperListener Inited. Create menu and update token after.");
    	
    	final HelperService service = HelperService.getInstance();
    	
    	// Create Menu.
    	service.createMenu();
    	
    	// Update token timing 
    	new Timer("UpdateTokenTimer",false).schedule(new  TimerTask(){

			@Override
			public void run() {
				service.updateServiceToken();
			}
    		
    	}, 3600*1000, 5400*1000);
    	
    	new Timer("UpdateCRMTokenTimer",false).schedule(new TimerTask(){

			@Override
			public void run() {
				service.updateCrmToken();
			}
    		
    	}, 0,7000*1000);
    	
    	// Post message.
    	new Timer("PostMsgTimer",false).schedule(new TimerTask(){

			@Override
			public void run() {
				service.postMessage();
			}
    		
    	}, 120*1000,300*1000);
    	
    	
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }
	
}
