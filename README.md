# Avengers Parser

A Spring Boot application that parses country-to-HQ and contact data from .txt files and preloads lookup and superhero tables. 

## Requirements

- **Java JDK 17 or later** (tested with Java 21)
- **Maven** (for dependency management)
- **IntelliJ IDEA Community Edition** (for running the project)

## Project Structure

```
AvengersParser/
├── pom.xml
├── README.md
├── data/               //Input data
│   ├── country_hq.txt        
│   └── contacts/             
│       ├── Aircraft-Headquarter.txt
│       └── ...
└── src/
└── main/
├── java/
│   └── com/avengers/avengers_fe/
│       ├── AvengersFeApplication.java
│       ├── controller/
│       │   └── DashboardController.java
│       ├── model/
│       │   ├── CountryHQ.java
│       │   └── ContactTable.java
│       ├── service/
│       │   └── DataLoaderService.java 
│       └── utils/
│           └── Utils.java
└── resources/
   ├── application.properties
   └── templates/
   │  ├── index.html
   │  ├── lookup.html
   └── superhero.html
```

## How to Run

1. **Import the Project:**
   - Open IntelliJ IDEA Community Edition.
   - Click **File > Open...** and select the `pom.xml` file in the project root. IntelliJ will import the project as a Maven project.

2. **Build the Project:**
   - IntelliJ should automatically download the required dependencies.
   - You can build the project by clicking **Build > Build Project**.

3. **Run the Application:**
   - Locate the main class: `AvengersFeApplication.java` (under `com.avengers.avengers_fe`).
   - Right-click on the file and select **Run 'AvengersFeApplication'**.
   - The application will start on port 8080.

4. **Access the Endpoints:**
   - Open your browser and navigate to:
     - [http://localhost:8080/](http://localhost:8080/) for the home page.
     - [http://localhost:8080/lookup](http://localhost:8080/lookup) for the lookup dashboard.
     - [http://localhost:8080/superhero](http://localhost:8080/superhero) for the superhero dashboard.

## Notes
- **Configuration:**  
  The Thymeleaf templates are located in `src/main/resources/templates`. The view resolver is configured in `application.properties`.
```