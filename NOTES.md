# Notes on the project
Used ChatGPT for initialisation of the project. ChatGPT recommended using spring boot and vite when I told it I will be building in React, JavaScript and Java.
Starting off with simple authentication system and want to later expand it with additional features.

## Things I learnt
Vite is a frontend dev server and a build tool. Instead of manually bundling JS and configuring webpack it will server the app instantly and reload on changes

### JPA (Java Persistence API)
A standard specification (interface) that allows the manage of database data using Java objects. Instead of opening a database connection and writing up SQL queries manually it maps objects to tables and converts objects to sql.
JpaRepository interface has methods like save() and delete() which will add/delete entities form the database without writing SQL. 
JPA Annotations
@Entity
Specifies a table in the database
@Id
Specifies a primary key
@GeneratedValue(strategy = GenerationType.IDENTITY)
Auto generate ID
@Table(name = "")
explicitly names the table

In Spring Data JPA, you can add simple methods that are not defined in JpaRepository and Spring will automatically generate based on the method name

### H2 Database Engine
Lightweight SQL database written in Java. It runs inside my Java app and exist only in memory so the data is stored in RAM and will reset when the app is restarted.

### JWT (JSON Web Token)
Has 3 parts: header (algorithm info), payload (your data), signature (security check)

## Problems I faced
- Named the class for users as "User" and specified as an @Entity but this caused an sql syntax error as it auto named the table as user which h2 complained about. Renamed the table to app_users.

- I couldn't access h2 console through the browser with url http://localhost:8080/h2-console/. It returned a whitelabel error page. I asked ChatGPT for help and it kept hallucinating. Through research I found out it was due to a missing dependency that was required to access h2 console if using spring boot version 4.0+. This was because I've created the spring boot project template using the spring initializr without adding any dependencies. I've later manually added the dependencies in pom.xml and missed the dependency required for this new spring boot version. I solved it by adding dependencies in the spring initializr and comparing the pom file to add any missing ones.

## Changes I want to make
I want to either remove JPA or write one endpoint using JDBC (Java Database Connectivity) to learn more about JDBC, SQL, connection management and manual mapping.

## Come back to
Enabled cors in controller class. This should be moved to global config.

In BackendApplication.java, @SpringBootApplication is to tell spring to scan the project and auto configure everything.
SpringApplication.run will boot up the Tomcat web server, dependency injection system and all the controllers.

@RestController will make the class handle HTTP requests and return data. Instead of returning HTML pages it returns stings and JSON objects.
@RequestMaaping sets what enpoints in the class starts with
@PostMapping handles HTTP POST requests
@RequestBody takes JSON from the frontend and convert it into Java object

the App function in App.jsx is the root React component


## Progress
create getHello function in api.js
getHello called in App function in App.jsx
created user entity
created endpoints for register and login
added h2 database
created the UserRepository interface extending JpaRepository so that reading and writing data becomes easier not having to write sql queries.
Created /register /login endpoints
Tested using postman and verified with h2 console
Used ResponseEntity to return HTTP response
Created the api resposne class in dto (dData Transfer Object) folder
Hashed the passwords using BCryptPasswordEncoder
Created SecurityConfig.java to prevent from Spring Security locking down all endpoints by default
Using JWT to create token so that the user is identified on the HTTP requests after login

TODO
Frontend
Controller had too much responsibilities as its currently 
- finding user in DB
- checks passwords
- generates JWT
- returns responses 
Create Authservice
package com.playground.backend.service;

import com.playground.backend.model.User;
import com.playground.backend.repository.UserRepository;
import com.playground.backend.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String login(User user) {

        User existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser == null) {
            return null;
        }

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return null;
        }

        return JwtUtil.generateToken(existingUser.getUsername());
    }
}

Update AuthControler
private final AuthService authService;

public AuthController(AuthService authService) {
    this.authService = authService;
}

@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody User user) {
    User savedUser = authService.register(user);
    return ResponseEntity.ok(savedUser);
}

🔧 Login endpoint
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User user) {

    String token = authService.login(user);

    if (token == null) {
        return ResponseEntity.status(401)
                .body("Invalid credentials");
    }

    return ResponseEntity.ok(token);
}

Next improvement
atttach token automatically to requests
add axios interceptor sot hat every request automatically sends Authorization: Bearer <token>
