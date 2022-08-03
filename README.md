# ATM Project

## Overview
This is an ATM application that allows to perform transactions like deposit, withdrawals, balance check for authorized account. This readme covers the following:
* Design
* Running the application
* Test Scenarios
* Enhancements to support the non-functional requirements

  
## Design
This solution is made up of two microservices: ATM Console application & ATM Server application and is supported by the H2 Database.

<image>
* The ATM Console application supports shell commands while the Server application supports APIs.
* The applications are Spring Framework based Java microservices.
* They are available as docker images, so that they can be run even without setting up a Java development environment.
* Database:
  - The H2 database is initialized on first run of the ATM server. The seed data is available in atm-server/src/main/resources/default_accounts.json
  - This database helps to persist data across restarts. It also helps build the functionality of multiple ATM consoles interacting with the same backend server.
  - There are two main tables: Accounts and Transactions. 
  - Accounts table has data about user accounts (ID, pin, balance) and about ATM machines (ID, amount available).
  - Transactions table has details about user transactions (timestamp, accountId, amount, balance)
* ATM Server API Security:
  - Spring Security and tomcat login filters help validate the user before providing access to the APIs.
* Concurrency:
  - The deposit and withdraw functions are synchronized to support concurrent access. 
  - The read functions like get balance allow multiple threads to access simultaneously.
* Documentation:
  - The ATM console application has 'help' option to view all the supported commands.
  - The ATM server has an in-built Swagger UI (at http://localhost:9090/swagger-ui.html) for interacting with the supported APIs.

## Running the application
The ATM console application and the ATM server applications are available as docker images. 
1. Download the docker images from this repo.
2. Load the images onto your system.
3. Run the ATM console application
```
docker run --name atm-console --env SERVER_URL=http://atm-server:9090 --env MACHINE_ID=0000000001 -it atm-console
```
You can run another instance of the console application (to mimic another ATM machine) by specifying the MACHINE_ID as 0000000002 for that instance.
4. Run the ATM server application
```
docker run --name atm-server -p 127.0.0.1:9090:9090/tcp --volume atmvol:/db -it atm-server
```
5. Connect the two instances to the same network so that they can talk to each other.
```
docker network create atm-network
docker network connect atm-network atm-console
docker network connect atm-network atm-server
```
6. Execute commands in the console application

## Test Scenarios
### Login
* server not available
* success scenario
* invalid account no.
* invalid pin
* login with machine ID
* login without logout, then check balance
* Error on the server side??

### Logout
* server not available
* Not logged in
* sucess scenario
* logout twice 
* invoke logout before login
* timeout after 2 mins

### Deposit
* server not available
* amount not specified
* negative amount
* zero amount
* deposit with more than 2 decimal points
* success case
* data seen from multiple console

### Withdrawals
* success scenario
* withdraw amt that is not a multiple of 20
* atm does not have the requested amt
* atm has no cash
* account does not have the request amt
* account is in overdraft state

### Balance
* server not available
* Not logged in
* Overdraw/negative balance

### History
* server not available
* Not logged in
* Transactions found
* no transactions
* multiple txns (reverse order)
* Withdrawals shown as -ve
* Data persisted after restart
* Data seen from multiple consoles

## Enhancements to support Non-Funtional Requirements
### Consistency
ATM Console and the server application are both stateless with regards to account data. Hence the data consistency relies on the database. In the current design where a single instance of H2 is used, consistency issues do not come into play. 
If there are multiple instances of the database, then the frequency of data replication and the presence/syncing of a caching layer would affect the consistency. Eventual consistency should be the *initial* approach as the user would be interacting with one ATM machine at a time. Tighter controls could be enforced or better consistency can be implemented depending on the business need.

### Availability / Fault tolerance
Dockerized applications lend themselves to building available services. The current design can be enhanced to support this:
* Centralized IAM provider that takes care of authorization for all instances.
* Load balancer in front of the ATM server instances. The ATM console application should connect to the load balancer instead of the servers directly.
* ATM console application should have retries and backoff implemented when connecting to the ATM server application. 

### Performance
* Mutliple instances of the application should help with improving the performance.
* ATM console application connects synchronously with the ATM server. Async communication would help improve the performance and stability of the overall system.

### Traceability
* Logging and auditing should be implemented.
* CorrelationID should be added to the header of every request to trace the calls through the microservices.

