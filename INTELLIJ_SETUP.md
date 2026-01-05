# IntelliJ IDEA Setup for Ghidra Libraries

After running the `ghidra-maven-plugin`, follow these steps to make Ghidra JARs visible in IntelliJ IDEA:

## Automatic Library Generation

The plugin automatically creates library definitions in `.idea/libraries/` with names like:
- `Ghidra_Framework_11_4_2.xml`
- `Ghidra_Features_11_4_2.xml`
- `Ghidra_Processors_11_4_2.xml`
- etc.

## Steps to Add Libraries to Your Module

### Option 1: Via Project Structure (Recommended)

1. Open **File → Project Structure** (or press `Ctrl+Alt+Shift+S`)
2. Navigate to **Modules** → Select your module (e.g., `ghidra-bom-test`)
3. Click the **Dependencies** tab
4. Click the **+** button → **Library** → **Project Library**
5. Select the Ghidra libraries you want to add:
   - `Ghidra_Framework_11.4.2`
   - `Ghidra_Features_11.4.2`
   - `Ghidra_Processors_11.4.2`
   - etc.
6. Click **OK** and **Apply**

### Option 2: Reload Maven Project

1. Run the plugin: `mvn initialize` or `mvn clean install`
2. In IntelliJ, go to **Maven** tool window (View → Tool Windows → Maven)
3. Click the **Reload All Maven Projects** button (circular arrow icon)
4. **File → Invalidate Caches** → Select **Invalidate and Restart**

### Option 3: Manual `.iml` File Edit

If you have `.iml` files, you can manually add library references:

```xml
<orderEntry type="library" name="Ghidra_Framework_11.4.2" level="project" />
<orderEntry type="library" name="Ghidra_Features_11.4.2" level="project" />
```

## Verification

Once added, you should see the libraries in:
1. **Project** view → **External Libraries**
2. Ghidra classes will have autocomplete support
3. You can navigate to Ghidra source code with Ctrl+Click

## Troubleshooting

### Libraries Not Showing Up

- **Verify plugin ran**: Check for `.idea/libraries/Ghidra_*.xml` files
- **Check Maven output**: Look for "Created library: Ghidra_Framework_11.4.2 with X JARs"
- **Restart IntelliJ**: Sometimes a full restart is needed
- **Reimport Maven project**: Right-click pom.xml → Maven → Reimport

### Classes Still Not Available

-Run the plugin manually:
  ```bash
  mvn lv.cebbys.tools:ghidra-maven-plugin:1.0.0:index-jars \
      -Dghidra-home.path="C:\Tools\Ghidra\Ghidra 11.4.2"
  ```

### Wrong Ghidra Version

- The plugin auto-detects version from your installation
- Override with: `-Dghidra.version=11.4.2`

## Plugin Configuration

In your `pom.xml`:

```xml
<plugin>
    <groupId>lv.cebbys.tools</groupId>
    <artifactId>ghidra-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <id>index-ghidra-jars</id>
            <phase>initialize</phase>
            <goals>
                <goal>index-jars</goal>
            </goals>
            <configuration>
                <ghidraHomePath>C:\Tools\Ghidra\Ghidra 11.4.2</ghidraHomePath>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Notes

- Libraries are grouped by Ghidra category (Framework, Features, Processors, etc.)
- Each library contains all JARs from that category
- The plugin runs during the `initialize` phase by default
- Library files are gitignored (`.idea` directory)
