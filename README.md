# Customer Support Ticketing System

## Project Description
A secure, scalable **Customer Support Ticketing System** built with **Spring Boot 3** and **Java 21**, using **opaque token-based authentication** for enhanced security. 
The system supports role-based access for Customers and Admins, enabling ticket creation, management, and detailed tracking. Data is stored in **MongoDB**, including users, tickets, and opaque tokens.

---

## Features

- **User Authentication & Authorization**
  - Role-based access control (Customer, Admin)
  - Secure authentication using **opaque tokens** stored in MongoDB
  - Tokens are immediately deleted upon user logout to prevent any potential reuse
  - Expired tokens are automatically cleaned up periodically to maintain security and performance

- **Ticketing System**
  - Customers can create and view support tickets
  - Admins can view, assign, update ticket status, and add internal comments
  - Threaded ticket details with comments and status updates

- **Database Integration**
  - MongoDB stores users, tickets, comments, and opaque tokens

- **REST APIs**
  - Clean, structured endpoints for all operations
    
---

## Tech Stack

| Technology     | Version       |
|----------------|---------------|
| Java           | 21            |
| Spring Boot    | 3.x           |
| Spring Security| Latest        |
| MongoDB        | Latest stable |
| Gradle         | Latest        |

---

## Prerequisites

- **Java JDK 21+** installed and configured in your environment
- **MongoDB** running locally or accessible remotely
- **Gradle** (installed or use wrapper `./gradlew`)
- **IntelliJ IDEA** (recommended) or any Java IDE

---

## Setup Instructions

1. **Clone the repository**
    ```bash
    git clone https://github.com/rakshitha-bai-v/csts.git
    cd csts
    ```

2. **Configure MongoDB Connection & Server Port** (optional)  
   Update `src/main/resources/application.properties` if your MongoDB or port differ:
    ```properties
    spring.data.mongodb.uri=mongodb://localhost:27017/test_db
    server.port=8088
    ```

3. **Open the project in IntelliJ IDEA**  
   - Click **Open** and select the cloned `csts` directory  
   - Wait for Gradle to sync and download dependencies  

4. **Build and run the application**
    ```bash
    ./gradlew bootRun
    ```

5. **Access the Swagger UI**  
   Open a browser and go to: http://localhost:8088/swagger-ui/index.html

---

## Notes

- Tokens expire after 20 minutes and are automatically removed from the database
- On user logout, the opaque token is deleted immediately from the database
- Use the opaque token in the `Authorization: Bearer <token>` header to access secured endpoints

---

## Troubleshooting

- Ensure MongoDB is running before starting the app  
- If port 8088 is busy, update `server.port` in `application.properties`  
- Run `./gradlew clean build` to resolve dependency issues  
- Check application logs for token management or authentication errors  

---
