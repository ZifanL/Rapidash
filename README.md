# Rapidash
The repository contains the code to reproduce the experiments for Rapidash, an efficient system to detect violations to denial constraints.

## Requirements

To run this project, ensure that you have the following installed:

- **Java 17 or above**: The project is built and tested with Java 17. You can download the latest version of Java from the [Oracle website](https://www.oracle.com/java/technologies/javase-downloads.html) or [OpenJDK](https://jdk.java.net/).

- **Maven**: Maven is used for dependency management and building the project. You can download Maven from the [Maven website](https://maven.apache.org/download.cgi).

## Installation

1. **Verify Java Installation**: Ensure that Java 17 or above is installed by running the following command in your terminal:
    ```sh
    java -version
    ```
    The output should display a Java version of 17 or above.

2. **Verify Maven Installation**: Ensure that Maven is installed by running the following command in your terminal:
    ```sh
    mvn -version
    ```
    The output should display the Maven version.

## Building the Project

1. **Clone the Repository**: Clone the project repository to your local machine:
    ```sh
    git https://github.com/ZifanL/Rapidash.git
    cd Rapidash
    ```

2. **Build the Project**: Use Maven to build the project:
    ```sh
    mvn clean install
    ```

## Running the Project
    ```sh
    java -cp target/sigmodrevision2-1.0-SNAPSHOT-jar-with-dependencies.jar org.example.Main <dataset>
    ```
where <dataset> should be one of "tax", "tpch" and "ncvoter" 