# CSYE 6225 - Summer 2019

## Team Information

| Name | NEU ID | Email Address |
| --- | --- | --- |
| Akshay Bhusare | 001443548 | bhusare.a@husky.neu.edu |
| Pratik Patil | 001499015 | patil.prat@husky.neu.edu |
| Rajat Acharya  | 001449207 | acharya.raj@husky.neu.edu |
| | | |

## Technology Stack
- Java SpringBoot for REST API
- Eclipse IDE
- MariaDB for Database
- Postman to test REST endpoints

## Build Instructions
1. Clone repository
2. Import maven project **LibrarySystem** in the **webapp** directory into eclipse
3. Right Click LibrarySystem > Maven > Update Maven Project > OK

## Deploy Instructions
1. Create a new database named 'librarydb' in MariaDB.
   ```
   create database librarydb
   ```
2. Add below mysql user to allow application to connect to the database 
   ```
   grant all privileges on librarydb.* to 'appuser' identified by 'pass'
   ```
   ```
   Note : If using different MySQL credentials, you need to input the correct credentials in file webapp/LibrarySystem/src/main/resources/application.properties
   ```
3. Run Eclipse project imported in above steps as **SpringBoot Application**

## Running Tests
1. Run the LibrarySystem project imported in Eclipse as **JUnit Test*

## CI/CD
k

