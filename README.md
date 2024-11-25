# Breakable Toy I

This repository includes the first Breakable Toy developed during the Spark program. It consists of a To Do application which uses the following stack:

- **Front-end:** React, Redux and Typescript.
- **Back-end:** Spring Boot and Java with an in-memory database (H2).

## Setup

To set up this project on your local machine follow this instructions after cloning the repository:

> Note: To run this project on your local machine you need to have installed node and the Java SDK. For the development of this project, I used **node** version ``22.11``, **npm** version ``10.9.0`` and Java version ``21.0.05``.

### Back-end

1. Go to the back-end project on your terminal

    ```bash
    cd backend-todos
    ```

2. Install the required dependencies.
    ```bash
    mvnw clean install
    ```

3. If you want to run the preloaded tests.
    ```
    mvnw test
    ```

4. Running the application server
    ```bash
    mvnw spring-boot:run
    ```

### Front-end

1. Go to the front-end project on your terminal.
    ```bash
    cd client
    ```

2. Install the required dependencies.
    ```
    npm install
    ```

3. If you want to run the preloaded tests.
    ```
    npm run test
    ```

4. Running the application
    ```bash
    npm run start
    ```

5. To access the web page, open your browser and go to [http://localhost:8080/](http://localhost:8080/)

