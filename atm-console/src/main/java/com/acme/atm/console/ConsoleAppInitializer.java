package com.acme.atm.console;

import org.springframework.boot.CommandLineRunner;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import com.acme.atm.console.cmdhandler.APIHandler;

/** Entry point to the ATM Console application.
 * 	It sets up Spring Boot and configures to work as a shell based application.
 * 
 * 	@author SHarippriya
 *
 */
@SpringBootApplication
public class ConsoleAppInitializer  implements CommandLineRunner {

	@Autowired
    private ConfigurableApplicationContext context;
	
	@Autowired
	private Environment env;
	
	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConsoleAppInitializer.class);
        // disable spring banner
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
	
	 @Override
	 public void run(String... args) throws Exception {
		 
		 // This is invoked after the user exits the shell.
		 // Simply terminate the application at this point.
		 System.exit(SpringApplication.exit(context));
	 }
	 
	 @PostConstruct
	 public void init()
	 {
		 // Base URL of the ATM server that performs the DB operations
		 if(env.containsProperty("SERVER_URL")) {
			 APIHandler.getInstance().setServerBaseUrl(env.getProperty("SERVER_URL"));
		 }
		 else {
			 // default value if running using spring-boot:run from cmd line
			 APIHandler.getInstance().setServerBaseUrl("http://localhost:9090");
		 }
		 
		 // ID of this console application that mimics an ATM machine. 
		 // This must be one of the IDs defined in the default_accounts.json file.
		 // This helps to keep track of the transactions performed at a given ATM and also to track the available amount.
		 if(env.containsProperty("MACHINE_ID")) {
			 APIHandler.getInstance().setMachineId(env.getProperty("MACHINE_ID"));
		 }
		 else {
			 APIHandler.getInstance().setMachineId("0000000001"); 
		 }
		 
	 }
}
