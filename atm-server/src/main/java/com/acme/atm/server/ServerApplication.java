package com.acme.atm.server;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.acme.atm.server.dao.AccountRepository;
import com.acme.atm.server.dao.DBService;
import com.acme.atm.server.dao.TransactionHistoryRepository;
import com.acme.atm.server.model.Account;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Entrypoint to the ATM Server application.
 * 	This class takes care of initializing Spring boot and initializing the DB the first time is it started.
 * 
 * @author SHarippriya
 *
 */
@SpringBootApplication
@EntityScan(basePackages = {"com.acme.atm.server.model"})
@EnableJpaRepositories(basePackages = {"com.acme.atm.server.dao"})
public class ServerApplication  extends SpringBootServletInitializer implements WebMvcConfigurer {

	/** Repository object for the data table containing account details. */
	@Autowired
	AccountRepository accountDbRepo;
	
	/** Repository object for the data table containing account transactions. */
	@Autowired
	TransactionHistoryRepository historyDbRepo;
	
	/** Load the file with seed data for the application. It contains the list of accounts - IDs, pin and balances. */
	@Value("classpath:default_accounts.json")
	private Resource accountsResource;
	
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}
	
	/** This method allows to initialize Spring when deployed as a WAR file */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServerApplication.class);
    }   

    @PostConstruct
	public void init()
	{
		DBService.getInstance().setAccountRepository(accountDbRepo);
		DBService.getInstance().setHistoryRepository(historyDbRepo);
		
		// Read the default accounts and provide it to the Data service.
		// The data service will initialize the DB with this data if there are no records in the database.
		try (Reader reader = new InputStreamReader(accountsResource.getInputStream(), StandardCharsets.UTF_8)) {
			String configJson = FileCopyUtils.copyToString(reader);
			ObjectMapper objectMapper = new ObjectMapper();
			List<Account> accountList = objectMapper.readerForListOf(Account.class).readValue(configJson);
			DBService.getInstance().setDefaultAccountList(accountList);
        } catch(Exception e){
        	System.out.println("Failed to read and populate the default account data.");
			e.printStackTrace();
		}
	}
}
