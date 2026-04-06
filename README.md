# Database_Localization

Shopping Cart Application with Database Localization using JavaFX, Maven, and MySQL.

## Project Overview

This project is an extended version of the Week 2 shopping cart assignment. The main enhancement is the implementation of database localization for the user interface. Instead of using Java `ResourceBundle` for storing UI messages, all localization data is now stored in a MySQL database. This allows for easier management of translations and supports dynamic updates without needing to recompile the application.

In this version:

- UI localization messages are loaded from the MySQL database instead of Java `ResourceBundle`
- Shopping cart summary is stored in the `cart_records` table
- Individual cart items are stored in the `cart_items` table
- Different languages can be selected from the user interface

## Technologies Used

- Java
- JavaFX
- Maven
- MySQL / MariaDB
- JDBC
- Docker
- Jenkins

## Features

- Database-driven localization using the `localization_strings` table
- Supports multiple languages: English, Finnish, Swedish, Japanese, and Arabic
- Dynamic UI text loading based on selected language
- Shopping cart item entry with price and quantity
- Automatic subtotal calculation for each item
- Total cart cost calculation
- Save cart summary into the `cart_records` table
- Save individual cart items into the `cart_items` table
- Foreign key relationship between cart summary and cart items
- JavaFX user interface
- MySQL database integration using JDBC

## Setup Instructions

Follow the steps below to set up, configure, and run the application on your local machine.

### Prerequisites

Before starting, make sure the following software is installed on your computer:

- Java 21
- Maven
- MySQL or MariaDB
- MySQL Workbench or another SQL client
- Git
- IntelliJ IDEA or another Java IDE

You can verify Java and Maven by running:



### Step 1: Clone the Repository

- Clone the project from GitHub to your local machine:


git clone https://github.com/kumudun/Database_Localization.git
cd Database_Localization

### Step 2: Open the Project in Your IDE

- Open the `Database_Localization` folder in IntelliJ IDEA or your preferred Java IDE.If Maven dependencies do not load automatically, refresh the Maven project.

### Step 3: Set Up the Database

- Open MySQL Workbench or another SQL client and connect to your local MySQL server.
- Run the SQL commands in the file: database-schema.sql
- This will create the database: shopping_cart_localization
- It will also create the following tables: cart_records
                                            cart_items
                                            localization_strings

### Step 4: Insert Localization Data

- After creating the database and tables, run the SQL commands in: sample-localization-data.sql
- This will insert the UI translation data into the localization_strings table for the supported languages.

### Step 5: Configure the Database Connection in Java

- Open the file: src/main/java/DatabaseConnection.java
- Update the database connection details according to your MySQL setup:

private static final String URL =
        "jdbc:mysql://localhost:3306/shopping_cart_localization?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "your_password";

- Replace: root with your MySQL username
           your_password with your MySQL password

- If your MySQL server uses different host, port, or database settings, update the JDBC URL accordingly.

### Step 6: Run the Application

- To start the JavaFX application, run: mvn clean javafx:run
- If the project builds successfully, the Shopping Cart application window will open.

 ### Step 7: Use the Application

- After launching the application:

Select a language from the language dropdown
Enter the number of shopping cart items
Click Enter Items
Enter the price and quantity for each item
Click Calculate Total

- The application will:

load UI labels and messages from the localization_strings database table
calculate subtotals for each item
calculate the total cart cost
save the cart summary into the cart_records table
save each cart item into the cart_items table

### Step 8: Verify Data in the Database

- After running the application and calculating a cart, check the saved data by running these SQL queries in MySQL Workbench:

- USE shopping_cart_localization;

SELECT * FROM cart_records;
SELECT * FROM cart_items;
SELECT * FROM localization_strings;

- You should see:

cart summary data in cart_records
item-by-item data in cart_items
translation data in localization_strings


## Project Structure


src/
 ├── main/
 │   ├── java/
 │   │   ├── CartCalculator.java
 │   │   ├── CartService.java
 │   │   ├── DatabaseConnection.java
 │   │   ├── LocalizationService.java
 │   │   └── ShoppingCartApp.java
 │   └── resources/
 │       ├── main-view.fxml
 │       ├── MessagesBundle.properties
 │       ├── MessagesBundle_en_US.properties
 │       ├── MessagesBundle_fi_FI.properties
 │       ├── MessagesBundle_sv_SE.properties
 │       ├── MessagesBundle_ja_JP.properties
 │       └── MessagesBundle_ar_AR.properties
 ├── test/
pom.xml
Dockerfile
Jenkinsfile
database-schema.sql
sample-localization-data.sql
README.md