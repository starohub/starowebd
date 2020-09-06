![StaroWebD](https://github.com/starohub/starowebd/raw/master/resources/images/starowebd-64.png)

# Staro Micro Web Server

### Release

[Release 0.1.4](https://github.com/starohub/starowebd/releases/tag/0.1.4)

### Maven

```

        <!-- https://github.com/starohub/staroplaties/releases/tag/0.0.2 -->
        <dependency>
            <groupId>com.starohub.platies</groupId>
            <artifactId>staroplaties</artifactId>
            <version>0.0.2</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/staroplaties.jar</systemPath>
        </dependency>

        <!-- https://github.com/starohub/starodiscus/releases/tag/0.0.3 -->
        <dependency>
            <groupId>com.starohub.discus</groupId>
            <artifactId>starodiscus</artifactId>
            <version>0.0.3</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/starodiscus.jar</systemPath>
        </dependency>

        <!-- https://github.com/starohub/starojsb/releases/tag/0.1.9 -->
        <dependency>
            <groupId>com.starohub.jsb</groupId>
            <artifactId>starojsb</artifactId>
            <version>0.1.9</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/starojsb.jar</systemPath>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.velocity/velocity -->
        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity</artifactId>
            <version>1.7</version>
        </dependency>

        <!-- https://github.com/starohub/starowebd/releases/tag/0.1.4 -->
        <dependency>
            <groupId>com.starohub.webd</groupId>
            <artifactId>starowebd</artifactId>
            <version>0.1.4</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/starowebd.jar</systemPath>
        </dependency>

```

### Usage

```
String configFile = "";
String[] args = new String[] {configFile};
com.starohub.webd.WebDLoader.main(args);
```
