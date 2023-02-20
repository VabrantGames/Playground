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
<nobr> e.g. 'java -jar playground.jar --path /users/path/etc... -i MyFirstPlayground -p Squares:libgdx -l lwjgl3' </nobr>

*Note: libGDX is the default template*

## Integrating with existing libGDX projects

### Add to settings.gradle of root project
```java
includeBuild 'playground'
```

### Add to build.gradle of core project

```java 
apply plugin: 'maven-publish'

group = "com.playground.integrated"

publishing {
  publications {
    mavenJava(MavenPublication) {
      from components.java
    }
  }
  repositories {
    maven {
      url = rootProject.file('playground/publications')
    }
  }
}

tasks.named('build') { dependsOn 'publish' }
```

This will publish to the core project in a local directory instead of the default `.m2`. All projects will look in this
local directory to find the core project.

After building the core projects, you can run the playground projects with code from core.

