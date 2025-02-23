# Eternal Breaker

Eternal Breaker is a JavaFX-based game. This guide provides the steps to set up, build, and run the game on your system.

## Prerequisites

- Java 21 or higher

## Setup

> NOTE: If you encounter HiDPI issues on GNU/Linux using Wayland, you may need to set the environment variable `_JAVA_OPTIONS='-Dglass.gtk.uiScale=150%'` before running the game.

### 1. Clone the Repository

Clone the repository and navigate to the project folder:

```bash
git clone https://github.com/Rukkhadevata123/EternalBreakers.git
cd EternalBreakers
```

### 2. Install Dependencies (Optional)

You may need to install the game dependencies using the following commands:

#### For GNU/Linux

```bash
./mvnw clean install -s ./.mvn/conf/settings.xml
```

#### For Windows

```powershell
.\mvnw.cmd clean install -s .\.mvn\conf\settings.xml
```

## Running the Game

To run the game, use the following commands:

#### For GNU/Linux

```bash
./mvnw clean javafx:run -s ./.mvn/conf/settings.xml
```

#### For Windows

```powershell
.\mvnw.cmd clean javafx:run -s .\.mvn\conf\settings.xml
```

## Building the Game (Optional)

To build the game package, use the following commands:

#### For GNU/Linux

```bash
./mvnw package -s ./.mvn/conf/settings.xml
```

#### For Windows

```powershell
.\mvnw.cmd package -s .\.mvn\conf\settings.xml
```

### Download the JavaFX SDK

Visit the [JavaFX SDK download page](https://gluonhq.com/products/javafx/) to get the appropriate version of the JavaFX SDK.

For example, if you download the SDK to `~/Downloads/openjfx-17.0.13_linux-x64_bin-sdk.zip`, you can extract it as follows:

```bash
unzip ~/Downloads/openjfx-17.0.13_linux-x64_bin-sdk.zip -d target
```

### Running the Game with the JAR File

After building the game, you can run it with the following command:

#### For GNU/Linux

```bash
java --module-path target/javafx-sdk-17.0.13/lib/ --add-modules javafx.controls,javafx.fxml -jar target/eternal_breaker-1.0-RELEASE-jar-with-dependencies.jar
```

#### For Windows

If you've downloaded the JavaFX SDK to `D:\javas\javafx-sdk-17.0.13`, use this command to run the game:

```powershell
java -jar --module-path=D:\javas\javafx-sdk-17.0.13\lib --add-modules javafx.controls,javafx.fxml -jar .\target\eternal_breaker-1.0-RELEASE-jar-with-dependencies.jar
```

