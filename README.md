
Graph Designer
=======

## What is Graph Designer? ##
Graph Designer is a WYSIWYG tool to prototype property graphes. It mainly target the Neo4j graph models with the support of following features:

 - Labels
 - Types relationships
 - Properties on labels and relationships

Graph Designer is a JavaFX application. It runs on the Desktop and should be compatible the major operating systems supporting Java 13+.

![Screenshot](screenshot.png)

## How to build and run the project ##

The project is built using Maven. You can chose to package the application or directly run it.

To run the project execute the following command:

    mvn install -DskipTests && mvn -pl app javafx:run

To package the application, run:

    mvn install -DskipTests && mvn -pl app javafx:run
    
Once the application is package with jlink it can be run using:

    app/target/graph-editor/bin/launcher

