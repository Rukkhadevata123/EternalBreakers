# Eternal Breaker

Eternal Breaker is a JavaFX-based game. This README will guide you through the steps to set up, build, and run the project.

## Prerequisites

- Java 21 or higher
- Maven 3.9.0 or higher

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/EternalBreakers.git
cd EternalBreakers
```

### 2. Configure Maven Mirror

To speed up Maven dependency downloads, you can configure a mirror. Open the Maven settings file:

```bash
sudo vim /usr/share/java/maven/conf/settings.xml
```

Add the following mirror configuration:

```xml
<mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

### 3. Install Dependencies(Optional)

Run the following command to install the project dependencies:

```bash
mvn clean install
```

## Run the Project

To run the projeact, run the following command:

```bash
mvn clean javafx:run
```

## Build the Project(Optional)

To build the project, run the following command:

```bash
mvn package
```

Then go to [this link](https://gluonhq.com/products/javafx/) to download the JavaFX SDK.

Suppose you have downloaded the JavaFX SDK to the `~/Downloads/openjfx-17.0.13_linux-x64_bin-sdk.zip` folder. Then you can run the following command:

```bash
unzip ~/Downloads/openjfx-17.0.13_linux-x64_bin-sdk.zip -d target
```

Finally, you can run the following command to run the project:

```bash
java --module-path target/javafx-sdk-17.0.13/lib/  --add-modules javafx.controls,javafx.fxml -jar target/eternal_breaker-1.0-RELEASE-jar-with-dependencies.jar
```