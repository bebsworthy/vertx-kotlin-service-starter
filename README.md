
# vertx-kotlin-service-starter

This has for objective to be a basic application that explores 
the capabilities of vert.x & Kotlin to build micro-services.

It does not provide any actual useful functionality :)

## Done

- Basic vert.x & Kotlin project
- HTTP verticle
- API using OpenAPI3 Router Factory
- Co-routines based verticle example
- Bus service verticle
- Auto-generated proxy for bus services  !!MAKE SURE YOU USE JDK8 NOT JDK9!!

## Todo

- Security
- Testing
- Logging
- Monitoring
- Scaling
- Deployment configuration


## Issues

- Can't make vertx codegen to save the generated java file with kapt...


## Some useful command to remember

** How to set up gradle wrapper

    gradle wrapper

** How to run this example with auto-recompile

    ./gradlew run

** How to build a "fat jar"

    ./gradlew shadowJar

** How to run the fat jar

    java -jar build/libs/simple-project-fat.jar,


** How to kill all running vertx instances when gradle barf and doesn't release them

    pkill -9 -f io.vertx.core.Launcher


** Update Kotlin runtime

 - change kotlin version in build.gradle