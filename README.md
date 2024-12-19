# Eternal Breaker

Eternal Breaker is a JavaFX-based game. This README will guide you through the steps to set up, build, and run the game.

## Prerequisites

- Java 21 or higher

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Rukkhadevata123/EternalBreakers.git
cd EternalBreakers
```

### 2. Install Dependencies(Optional)

Run the following command to install the game dependencies:

```bash
./mvnw clean install -s ./.mvn/conf/settings.xml
```

## Run the Game

To run the game, run the following command:

```bash
./mvnw clean javafx:run -s ./.mvn/conf/settings.xml
```

## Build the Game(Optional)

To build the game, run the following command:

```bash
./mvnw package -s ./.mvn/conf/settings.xml
```

Then go to [this link](https://gluonhq.com/products/javafx/) to download the JavaFX SDK.

Suppose you have downloaded the JavaFX SDK to the `~/Downloads/openjfx-17.0.13_linux-x64_bin-sdk.zip` folder. Then you can run the following command:

```bash
unzip ~/Downloads/openjfx-17.0.13_linux-x64_bin-sdk.zip -d target
```

Finally, you can run the following command to run the game:

The same Jar file can be downloaded from this repository's release section.

```bash
java --module-path target/javafx-sdk-17.0.13/lib/  --add-modules javafx.controls,javafx.fxml -jar target/eternal_breaker-1.0-RELEASE-jar-with-dependencies.jar
```
