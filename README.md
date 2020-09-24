![StaroWebD](https://github.com/starohub/starowebd/raw/master/resources/images/starowebd-64.png)

# Staro Micro Web Server

### Documents

[API for JavaScript](http://webd.starohub.com/api.html)

[API for module building](http://webd.starohub.com/xapi.html)

### Release

[Release 0.1.5](https://github.com/starohub/starowebd/releases/tag/0.1.5)

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

        <!-- https://github.com/starohub/starojsb/releases/tag/0.1.11 -->
        <dependency>
            <groupId>com.starohub.jsb</groupId>
            <artifactId>starojsb</artifactId>
            <version>0.1.11</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/starojsb.jar</systemPath>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.velocity/velocity-engine-core -->
        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity-engine-core</artifactId>
            <version>2.1</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/velocity-engine-core-2.1.jar</systemPath>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.8.1</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/commons-lang3-3.8.1.jar</systemPath>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-api -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.26</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/slf4j-api-1.7.26.jar</systemPath>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-simple -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.26</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/slf4j-simple-1.7.26.jar</systemPath>
        </dependency>

        <!-- https://github.com/starohub/starowebd/releases/tag/0.1.5 -->
        <dependency>
            <groupId>com.starohub.webd</groupId>
            <artifactId>starowebd</artifactId>
            <version>0.1.5</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/starowebd.jar</systemPath>
        </dependency>
        
        <dependency>
            <groupId>com.starohub.trial.blueprint</groupId>
            <artifactId>starotrial-blueprint</artifactId>
            <version>0.0.1</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/starotrial.blueprint.jar</systemPath>
        </dependency>
        
        <dependency>
            <groupId>com.starohub.trial.artwork</groupId>
            <artifactId>starotrial-artwork</artifactId>
            <version>0.0.1</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/starotrial.artwork.jar</systemPath>
        </dependency>
        
        <dependency>
            <groupId>com.starohub.trial.dataset</groupId>
            <artifactId>starotrial-dataset</artifactId>
            <version>0.0.1</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/starotrial.dataset.jar</systemPath>
        </dependency>
        
        <dependency>
            <groupId>com.starohub.trial.kernel</groupId>
            <artifactId>starotrial-kernel</artifactId>
            <version>0.0.1</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/starotrial.kernel.jar</systemPath>
        </dependency>
        
```

### Usage

```
String configFile = "";
String blueprintClass = "com.starohub.trial.blueprint.BluePrint";
String blueprintLicense = "/bpt/com.starohub.trial.blueprint/license.lic";
String[] args = new String[] {configFile, blueprintClass, blueprintLicense};
com.starohub.webd.WebDLoader.main(args);
```
