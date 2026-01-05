# Ghidra Maven Plugin

A Maven plugin for indexing and managing Ghidra JAR dependencies.

## Features

- **Version Validation**: Ensures that only Ghidra version 12.0 is used
- **JAR Indexing**: Automatically indexes all JAR files in the Ghidra installation directory
- **Categorization**: Organizes JAR files by category (Framework, Features, Processors, etc.)
- **Build Integration**: Runs during the Maven `validate` phase by default

## Requirements

- Maven 3.6.0 or higher
- Java 17 or higher
- Ghidra 12.0 installation

## Configuration

The plugin requires two mandatory properties:

### Required Properties

| Property | Description | Example |
|----------|-------------|---------|
| `ghidra.version` | The Ghidra version (must be 12.0) | `12.0` |
| `ghidra-home.path` | Path to Ghidra installation directory | `/opt/ghidra` or `C:\Tools\Ghidra` |

### Usage

Add the plugin to your project's `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>lv.cebbys.tools</groupId>
            <artifactId>ghidra-maven-plugin</artifactId>
            <version>1.0.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>index-jars</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <ghidraVersion>${ghidra.version}</ghidraVersion>
                <ghidraHomePath>${ghidra-home.path}</ghidraHomePath>
            </configuration>
        </plugin>
    </plugins>
</build>

<properties>
    <ghidra.version>12.0</ghidra.version>
    <ghidra-home.path>/path/to/ghidra</ghidra-home.path>
</properties>
```

### Command Line Usage

You can also run the plugin directly from the command line:

```bash
mvn lv.cebbys.tools:ghidra-maven-plugin:1.0.0:index-jars \
    -Dghidra.version=12.0 \
    -Dghidra-home.path=/opt/ghidra
```

### Skipping Execution

To skip the plugin execution:

```bash
mvn clean install -Dghidra.index.skip=true
```

Or in your `pom.xml`:

```xml
<configuration>
    <skip>true</skip>
</configuration>
```

## Goals

### `index-jars`

Indexes all JAR files in the Ghidra installation directory.

**Phase**: `validate` (default)

**Parameters**:
- `ghidraVersion` (required): The Ghidra version (must be 12.0)
- `ghidraHomePath` (required): Path to the Ghidra installation
- `skip` (optional): Skip plugin execution (default: `false`)

## Output

The plugin provides detailed logging of the indexing process:

```
[INFO] Validating Ghidra version: 12.0
[INFO] Ghidra version validation passed: 12.0
[INFO] Indexing Ghidra JAR files from: /opt/ghidra
[INFO] Successfully indexed 156 JAR files
[INFO] ========================================
[INFO] Ghidra JAR Index Summary
[INFO] ========================================
[INFO]   Framework            : 23 JAR(s)
[INFO]   Features             : 87 JAR(s)
[INFO]   Processors           : 34 JAR(s)
[INFO]   Configurations       : 8 JAR(s)
[INFO]   Extensions           : 4 JAR(s)
[INFO] ========================================
```

## Error Handling

### Invalid Ghidra Version

If a version other than 12.0 is specified, the build will fail:

```
========================================
ERROR: Invalid Ghidra version!
========================================

Expected version: 12.0
Provided version: 11.4.2

This plugin currently only supports Ghidra version 12.0.
Please update the 'ghidra.version' property to 12.0.
========================================
```

### Missing Properties

If required properties are not defined, the plugin will throw a helpful error:

```
[ERROR] Property 'ghidra.version' is required but not defined.
Please define it in your pom.xml or pass it as -Dghidra.version=12.0
```

### Invalid Ghidra Path

If the Ghidra home path is invalid:

```
[ERROR] Ghidra home path does not exist or is not a directory: /invalid/path
```

## Integration with ghidra-bom

This plugin is designed to work seamlessly with the `ghidra-bom` module. The indexed JAR information can be used by the BOM to automatically configure dependencies.

## Development

### Building the Plugin

```bash
cd ghidra-maven-plugin
mvn clean install
```

### Testing

```bash
mvn test
```

## License

See the main project LICENSE file.
