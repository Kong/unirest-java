# Unirest for Java 

[![Actions Status](https://github.com/kong/unirest-java/workflows/Verify/badge.svg)](https://github.com/kong/unirest-java/actions)
[![MIT License](https://img.shields.io/apm/l/atomic-design-ui.svg?)](https://github.com/tterb/atomic-design-ui/blob/master/LICENSEs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.konghq/unirest-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.kong/unirest-java)
[![Javadocs](http://www.javadoc.io/badge/com.konghq/unirest-java.svg)](http://www.javadoc.io/doc/com.konghq/unirest-java)
[![Twitter Follow](https://img.shields.io/twitter/follow/UnirestJava.svg?style=social)](https://twitter.com/UnirestJava) 

## Install With [Maven](https://mvnrepository.com/artifact/com.konghq/unirest-java)[:](https://repo.maven.apache.org/maven2/com/konghq/unirest-java/)
### 🚨 Attention JSON users 🚨
Unirest now uses a modular json system. If you want to use JSON you MUST include in the pom the JSON implementation you wish to use. This can be either Jackson or Gson.

```xml
<!-- Pull in as a traditional dependency -->
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-java</artifactId>
    <version>4.0.0-RC3</version>
</dependency>

<!-- OR as a snazzy new standalone jar with shaded dependencies -->
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-java</artifactId>
    <version>4.0.0-RC3</version>
    <classifier>standalone</classifier>
</dependency>

<!-- ONE of the following, if BOTH are on the path Unirest will pick the first it finds -->
<!-- GSON -->
<dependency>
   <groupId>com.konghq</groupId>
   <artifactId>unirest-object-mappers-gson</artifactId>
   <version>4.0.0-RC3</version>
</dependency>

<!-- Jackson -->
<dependency>
   <groupId>com.konghq</groupId>
   <artifactId>unirest-object-mappers-jackson</artifactId>
   <version>$4.0.0-RC3</version>
</dependency>

```

## Upgrading from Previous Versions 
See the [Upgrade Guide](UPGRADE_GUIDE.md)

## ChangeLog 
See the [Change Log](CHANGELOG.md) for recent changes.

## Documentation
Our [Documentation](http://kong.github.io/unirest-java/) 
