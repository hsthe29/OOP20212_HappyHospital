import java.net.URI

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.12"
}

group = "game"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "12"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainModule.set("game")
    mainClass.set("Main")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation ("com.google.code.gson:gson:2.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
