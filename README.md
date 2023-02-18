<h1 align="center">VabrantPlayground</h1>

<p align="center">A place to test libGDX related code</p>

## Usage

Download the latest release from the release section. Run the jar from the command line with `java -jar`, using any
of the needed commands below.

| Command               | Description                                                                                    |
|-----------------------|------------------------------------------------------------------------------------------------|
| `--path`              | Path to the directory                                                                          |
| `-in`                 | Initialize the playground                                                                      |
| `-p`                  | Create new project. Templates can be specified when the project name is passed in. `project:template` |
| `-l`                  | Create launcher. (Needs a project)                                                             |
| (TODO) `--standalone` | A playground that will not be integrated into an existing libGDX project                       |

e.g. `java -jar playground.jar --path pathToDirectory -in PlaygroundName -p ProjectName:templateName -l lwjgl3 -p NextProject`

*Note: libGDX is the default template*

## Integrating with existing libGDX projects

*Note: For integrated projects*

### Add to settings.gradle of libGDX project
```java
includeBuild 'playground'
```

### Add to build.gradle of core project

