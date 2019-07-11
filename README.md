<h1 height="256"><img align="left" width="41" height="41" src=".github/logo48.png">&nbsp;Elyssif Client</h1>

**E**lyssif **L**et's **Y**ou **S**ecurely **S**end **I**mportant **F**iles

Elyssif is a user-friendly app that allows you to send sensitive or important files via the platform of your choice without worrying about your data being stolen or sold, thanks to strong asymmetric encryption.

## Installing

### Prerequisites
- [Git](https://git-scm.com)
- [Java SDK 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
- [Maven 2](https://maven.apache.org/)
- [JavaFX Scene Builder](https://gluonhq.com/products/scene-builder/) (Optional)

Build the project and run unit testing using maven: `mvn install`.  

### Maven profiles

- `local`: Used for development. Build: `mvn install -Plocal`
- `production`: Used for deployment. Build: `mvn install -Pproduction`
- `testing`: Used for unit-testing, not available for building.


### Eclipse

The repository includes an Eclipse project that can be easily imported.  

1. File -> Import -> Maven -> Existing Maven Projects
2. Select the project's root.
3. Select all and click "Finish"

### JavaFX Scene Builder

Check that your Scene Builder version is **at least 10.0.0**.  
Before opening any FXML file in the project, you need to import JFoenix to Scene Builder:  

1. Click the little cog next to the search bar in the top left corner of the window.
2. Click "JAR/FXML Manager"
3. Click "Add Library/FXML from file system"
4. Add the jfoenix jar which should be located into your maven local repository (`.m2/repository/com/jfoenix/jfoenix/9.0.8/jfoenix-9.0.8.jar`). Your maven local repository is located into your home directory by default.
5. Open the jfoenix jar using an archive manager.
6. Extract `jfoenix-design.css` and `jfoenix-fonts.css` from `com/jfoenix/assets/css`. (The location where you extract those files doesn't matter)

When opening a FXML view from the project in Scene Builder, you'll notice that it doesn't render properly. You need to add the needed resources for preview.

1. Add CSS resources: "Preview -> Scene Style Sheets -> Add a style sheet..."
2. Select both previously extracted css files and `src/main/resources/common/view/css/application.css` from the project.
3. Add language bundle: "Preview -> Internationalization -> Set resource..."
4. Select `src/main/resources/common/bundles/lang_en.properties`

*Note: You have to do this for every FXML file individually.*

## Usage

TODO
