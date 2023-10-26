# Unirest for Java 

[![Actions Status](https://github.com/kong/unirest-java/workflows/Verify/badge.svg)](https://github.com/kong/unirest-java/actions)
[![MIT License](https://img.shields.io/apm/l/atomic-design-ui.svg?)](https://github.com/tterb/atomic-design-ui/blob/master/LICENSEs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.konghq/unirest-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.kong/unirest-java)
[![Javadocs](http://www.javadoc.io/badge/com.konghq/unirest-java.svg)](http://www.javadoc.io/doc/com.konghq/unirest-java)
[![Twitter Follow](https://img.shields.io/twitter/follow/UnirestJava.svg?style=social)](https://twitter.com/UnirestJava) 

## Unirest 4
Unirest 4 is build on modern Java standards, and as such requires at least Java 11.

Unirest 4's dependencies are fully modular, and have been moved to new Maven coordinates to avoid conflicts with the previous versions.
You can use a maven bom to manage the modules:

### Install With Maven

```xml
<dependencyManagement>
  <dependencies>
      <!-- https://mvnrepository.com/artifact/com.konghq/unirest-java-bom --> 
      <dependency>
          <groupId>com.konghq</groupId>
          <artifactId>unirest-java-bom</artifactId>
          <version>4.1.1</version>
          <type>pom</type>
          <scope>import</scope>
      </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
    <!-- https://mvnrepository.com/artifact/com.konghq/unirest-java-core -->
    <dependency>
        <groupId>com.konghq</groupId>
        <artifactId>unirest-java-core</artifactId>
    </dependency>
    
    <!-- pick a JSON module if you want to parse JSON include one of these: -->
    <!-- Google GSON -->
    <dependency>
        <groupId>com.konghq</groupId>
        <artifactId>unirest-object-mappers-gson</artifactId>
    </dependency>

    <!-- OR maybe you like Jackson better? -->
    <dependency>
        <groupId>com.konghq</groupId>
        <artifactId>unirest-objectmapper-jackson</artifactId>
    </dependency>
</dependencies>
```

#### 🚨 Attention JSON users 🚨
Under Unirest 4, core no longer comes with ANY transient dependencies, and because Java itself lacks a JSON parser you MUST declare a JSON implementation if you wish to do object mappings or use Json objects.


## Upgrading from Previous Versions 
See the [Upgrade Guide](UPGRADE_GUIDE.md)

## ChangeLog 
See the [Change Log](CHANGELOG.md) for recent changes.

## Documentation
Our [Documentation](http://kong.github.io/unirest-java/) 

## Unirest 3
### Maven
```xml
<!-- Pull in as a traditional dependency -->
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-java</artifactId>
    <version>3.14.1</version>
</dependency>
```
