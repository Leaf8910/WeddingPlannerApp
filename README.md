# WeddingPlannerApp

# Introduce
Javafx scaffolding, built on Oracle JDK24 + JavaFX24 + mysql-connector-j-9.2.0  + Maven


## 🎯 Features
- **User Authentication System**

    * Registration with email, username, and password
    * Login with "Remember Me" functionality
    * Session management with automatic timeout for security


- **Wedding Details Management**

    * Store bride and groom information
    * Set wedding date range
    * Track guest count
    * Manage budget ranges


- **Venue and Vendor Selection**

    * Visual selection of venues with images
    * Hall selection with preview images
    * Catering options with preview images
    * Save preferences to user profile


- **Checklist**

    * Pre-populated checklist items
    * Add, edit, and delete custom tasks
    * Mark tasks as completed
    * Track progress of wedding preparation


- **Itinerary Builder**

    * Create a detailed wedding day schedule
    * Add events with specific times
    * Edit and reorganize events
    * Full timeline visualization




## 🚀 Getting Started
### **Prerequisites**

__Ensure you have the following installed:__

* Java: Version 17 or higher
* JavaFX: Version 11 or higher
* SQLite: Version 3.36 or higher (included with the application)
* Maven: For dependency management and building the project
* IntelliJ IDEA: Recommended IDE for development and running the application

**Installation**

  __1. Clone the Repository:__
```bash
git clone https://github.com/Leaf8910/WeddingPlannerApp.git
```
  __2. Open in IntelliJ IDEA:__

  * Launch IntelliJ IDEA and select Open from the File menu.
  * Navigate to the cloned repository folder and select it.
__3. Set Up JavaFX:__

  - Configure the JavaFX SDK in IntelliJ:
      - Go to File > Project Structure > Libraries.
      - Add the JavaFX SDK by clicking + and selecting the JavaFX lib directory.
  Ensure the JavaFX modules are included in the run configuration 
  #### (e.g., --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml).

__4. Database Setup__
  

  * Install MySQL if not already installed
    * Run the database setup script:
```bash
java -cp ".:lib/*" com.DatabaseInitializer
```
or manually run the SQL script:

```bash

mysql -u root -p < setup_database.sql
```

__5. Build and Run:__

Use Maven to build the project:
```bash
mvn clean install
```
Run the application:
```bash
mvn javafx:run
```

## 📝 License
This project is licensed under the MIT License. See the [LICENSE](https://choosealicense.com/licenses/mit/)  file for details.
