<h1 align="center">VabrantPlayground</h1>

<p align="center">A place to test libGDX related code</p>

## Usage

Download the latest release from the release section. Run the jar from the command line with `java -jar`, using any
of the needed commands below.

| Command                     | Description                                                                                           |
|-----------------------------|-------------------------------------------------------------------------------------------------------|
| `--path`                    | Path to the directory                                                                                 |
| `-i`                        | Initialize the playground with the specified name                                                     |
| `-p`                        | Create new project. Templates can be specified when the project name is passed in. `project:template` |
| `-l`                        | Create launcher. (Needs a project)                                                                    |
| <nobr>`--standalone`</nobr> | A playground that will not be integrated into an existing libGDX project                              |

<nobr> e.g. `java -jar playground.jar --path pathToDirectory -i PlaygroundName -p ProjectName:templateName -l LauncherName` </nobr>

*Note: libGDX is the default template*

## Integrating with existing libGDX projects

### 1.) Turn assets folder into a project

Create a <b>build.gradle</b> file inside assets root directory and copy the code below
```groovy
group = "com.assets"
sourceSets.main.resources.srcDirs = ["."]
gradle.buildFinished{
    project.buildDir.deleteDir()
}
```

### 2.) Change group name of *build.gradle* in *core* project

```groovy 
group = "com.core"
```

### 3.) Add to settings.gradle of root project

```groovy
include assets
includeBuild '.'
includeBuild 'playground'
```

### Updating projects

*These steps are not necessary but will remove warnings and build.gradle of the assets project*

### <u><i>liftoff setup</i></u>

#### lwjgl3

*Note: Assets are pulled from the assets project*

* Remove line `sourceSets.main.resources.srcDirs...`
* Remove line `workingDir...` in the `run` closure
* Add dependency `project(:'assets')` 
* Add to `jar` closure 
```groovy
jar {
    ...
    
    dependencies {
        ...

        //Add this
        project(':assets') {
            exclude('build.gradle')
        }
    }
}
```

### <u><i>gdx-setup</i></u>

#### root build.gradle

```groovy 
project(":desktop") {
    ...
    
    dependencies {
        ...
        implementation project(":assets")
    }
}
```

#### lwjgl3

*Note: Assets are pulled from the assets project*

* Remove line `sourceSets.main.resources.srcDirs...`
* Remove line `project.ext.assetsDir...`
* Remove line `workingDir` in `run` closure
```
task run(dependsOn: classes, type: JavaExec) {
   ... 
   
   //Remove this
   workingDir = ...
}
```
* Remove line `workingDir` in `debug` closure (Follow the steps from run)
* Add to `dist` closure 
```groovy
task dist(type: Jar) {
    ...
    
    //Add this
    dependencies {
        project(':assets') {
            exclude('build.gradle')
        }
    }
}
```


