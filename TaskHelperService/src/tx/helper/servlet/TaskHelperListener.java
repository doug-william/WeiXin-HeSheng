package tx.helper.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import tx.helper.service.HelperService;

public class TaskHelperListener implements ServletContextListener {
	
    public TaskHelperListener() {}

    public void contextInitialized(ServletContextEvent arg0) {
    	tx.commons.util.Util.getInstance().info(this, "TaskHelperListener Inited, launch token timer.");
    	
    	HelperService.getInstance();
    	
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }
	
}
