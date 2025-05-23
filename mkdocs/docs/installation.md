## Unirest 4
Unirest 4 is build on modern Java standards, and as such requires at least Java 11.

Unirest 4's dependencies are fully modular, and have been moved to new Maven coordinates to avoid conflicts with the previous versions.
You can use a maven bom to manage the modules:

## Install With [Maven](https://mvnrepository.com/artifact/com.konghq/unirest-java)[:](https://repo.maven.apache.org/maven2/com/konghq/unirest-java/)
```xml
<dependencyManagement>
    <dependencies>
        <!-- https://mvnrepository.com/artifact/com.konghq/unirest-java-bom -->
        <dependency>
            <groupId>com.konghq</groupId>
            <artifactId>unirest-java-bom</artifactId>
            <version>4.4.5</version>
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
    <artifactId>unirest-modules-gson</artifactId>
</dependency>

<!-- OR maybe you like Jackson better? -->
<dependency>
    <groupId>com.konghq</groupId>
    <artifactId>unirest-modules-jackson</artifactId>
</dependency>
</dependencies>
```

#### 🚨 Attention JSON users 🚨
Under Unirest 4, core no longer comes with ANY transient dependencies, and because Java itself lacks a JSON parser you MUST declare a JSON implementation if you wish to do object mappings or use Json objects.

### Upgrading from Previous Versions
See the [Upgrade Guide](https://github.com/Kong/unirest-java/blob/master/UPGRADE_GUIDE.md)

### ChangeLog 
See the [Change Log](https://github.com/Kong/unirest-java/blob/master/CHANGELOG.md) for recent changes.


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