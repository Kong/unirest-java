<h3>Java</h3>
~~~xml
<dependency>
    <groupId>com.sparkjava</groupId>
    <artifactId>spark-core</artifactId>
    <version>{{site.sparkversion}}</version>
</dependency>
~~~
<h3>Kotlin</h3>
~~~xml
<dependency>
    <groupId>com.sparkjava</groupId>
    <artifactId>spark-kotlin</artifactId>
    <version>1.0.0-alpha</version>
</dependency>
~~~

[Not familiar with Maven? Click here for more detailed instructions.](/tutorials/maven-setup)

### Other dependency managers:
<div class="smaller-code" markdown="1">
~~~java
Gradle : compile "com.sparkjava:spark-core:{{site.sparkversion}}" // add to build.gradle (for Java users)
Gradle : compile "com.sparkjava:spark-kotlin:1.0.0-alpha" // add to build.gradle (for Kotlin users)
   Ivy : <dependency org="com.sparkjava" name="spark-core" rev="{{site.sparkversion}}" conf="build" /> // ivy.xml
   SBT : libraryDependencies += "com.sparkjava" % "spark-core" % "{{site.sparkversion}}" // build.sbt
~~~
</div>