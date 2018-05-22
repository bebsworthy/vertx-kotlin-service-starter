
# vertx  kotlin service starter project

This is a basic starter project for kotlin & vertx.

It does not provide any actual useful functionality :)


## Done

- Basic vert.x & Kotlin project
- HTTP verticle
- API using OpenAPI3 Router Factory
- Co-routines based verticle examples
- Bus service verticles
- Auto-generated proxy for bus services using kotlin interface !!MAKE SURE YOU USE JDK8 NOT JDK9!!
- Sample testing with JUnit5 & Vertx

## Todo

- Security
- Logging
- Monitoring
- Scaling
- Deployment configuration


## Issues & Help required

- Can't make vertx codegen to save the generated java file with kapt.


## Some useful command to remember

### How to set up gradle wrapper
```
gradle wrapper
```

### How to run this example with auto-recompile
```
./gradlew run
```   
    
### How to run the test
```
    ./gradlew test
```
### How to build a "fat jar"
```
    ./gradlew shadowJar
```

### How to run the fat jar
```
    java -jar build/libs/simple-project-fat.jar,
```

** How to kill all running vertx instances when gradle barf and doesn't release them
```
    pkill -9 -f io.vertx.core.Launcher
```

** Update Kotlin runtime

 - change kotlin version in build.gradle