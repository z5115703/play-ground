# Notes on the project
Used ChatGPT for initialisation of the project. ChatGPT recommended using spring boot and vite when I told it I will be building in React, JavaScript and Java.
Starting off with simple authentication system and want to later expand it with additional features.

## Things I learnt

# JPA (Java Persistence API)
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

# H2 Database Engine
Lightweight SQL database written in Java. It runs inside my Java app and exist only in memory so the data is stored in RAM and will reset when the app is restarted.

# JWT (JSON Web Token)
Has 3 parts: header (algorithm info), payload (your data), signature (security check)

## Problems I faced
- Named the class for users as "User" and specified as an @Entity but this caused an sql syntax error as it auto named the table as user which h2 complained about. Renamed the table to app_users.

- I couldn't access h2 console through the browser with url http://localhost:8080/h2-console/. It returned a whitelabel error page. I asked ChatGPT for help and it kept hallucinating. Through research I found out it was due to a missing dependency that was required to access h2 console if using spring boot version 4.0+. This was because I've created the spring boot project template using the spring initializr without adding any dependencies. I've later manually added the dependencies in pom.xml and missed the dependency required for this new spring boot version. I solved it by adding dependencies in the spring initializr and comparing the pom file to add any missing ones.

## Changes I want to make
I want to either remove JPA or write one endpoint using JDBC (Java Database Connectivity) to learn more about JDBC, SQL, connection management and manual mapping.

## Come back to
Enabled cors in controller class. This should be moved to global config.
