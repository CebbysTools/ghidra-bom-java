package lv.cebbys.tools.ghidra.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Maven plugin goal that indexes Ghidra JAR files and validates the Ghidra version.
 *
 * This plugin requires two mandatory properties:
 * - ghidra.version: The version of Ghidra to use (must be 12.0)
 * - ghidra-home.path: The path to the Ghidra installation directory
 */
@Mojo(name = "index-jars", defaultPhase = LifecyclePhase.VALIDATE)
public class GhidraIndexMojo extends AbstractMojo {

    /**
     * The Ghidra version to use. Must be 12.0.
     */
    @Parameter(property = "ghidra.version", required = true)
    private String ghidraVersion;

    /**
     * The path to the Ghidra installation directory.
     */
    @Parameter(property = "ghidra-home.path", required = true)
    private String ghidraHomePath;

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Skip the plugin execution.
     */
    @Parameter(property = "ghidra.index.skip", defaultValue = "false")
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping Ghidra JAR indexing");
            return;
        }

        // Validate required properties
        validateProperties();

        // Validate Ghidra version
        validateGhidraVersion();

        // Index JAR files
        indexGhidraJars();
    }

    private void validateProperties() throws MojoExecutionException {
        if (ghidraVersion == null || ghidraVersion.trim().isEmpty()) {
            throw new MojoExecutionException(
                "Property 'ghidra.version' is required but not defined. " +
                "Please define it in your pom.xml or pass it as -Dghidra.version=12.0"
            );
        }

        if (ghidraHomePath == null || ghidraHomePath.trim().isEmpty()) {
            throw new MojoExecutionException(
                "Property 'ghidra-home.path' is required but not defined. " +
                "Please define it in your pom.xml or pass it as -Dghidra-home.path=/path/to/ghidra"
            );
        }

        File ghidraHome = new File(ghidraHomePath);
        if (!ghidraHome.exists() || !ghidraHome.isDirectory()) {
            throw new MojoExecutionException(
                "Ghidra home path does not exist or is not a directory: " + ghidraHomePath
            );
        }
    }

    private void validateGhidraVersion() throws MojoFailureException {
        getLog().info("Validating Ghidra version: " + ghidraVersion);

        if (!"12.0".equals(ghidraVersion)) {
            throw new MojoFailureException(
                String.format(
                    "========================================%n" +
                    "ERROR: Invalid Ghidra version!%n" +
                    "========================================%n%n" +
                    "Expected version: 12.0%n" +
                    "Provided version: %s%n%n" +
                    "This plugin currently only supports Ghidra version 12.0.%n" +
                    "Please update the 'ghidra.version' property to 12.0.%n" +
                    "========================================%n",
                    ghidraVersion
                )
            );
        }

        getLog().info("Ghidra version validation passed: " + ghidraVersion);
    }

    private void indexGhidraJars() throws MojoExecutionException {
        getLog().info("Indexing Ghidra JAR files from: " + ghidraHomePath);

        Path ghidraPath = Paths.get(ghidraHomePath, "Ghidra");

        if (!Files.exists(ghidraPath)) {
            throw new MojoExecutionException(
                "Ghidra directory not found: " + ghidraPath +
                ". Expected to find a 'Ghidra' subdirectory in: " + ghidraHomePath
            );
        }

        List<JarInfo> jarFiles = new ArrayList<>();

        try {
            // Walk through the Ghidra directory tree and find all JAR files
            try (Stream<Path> paths = Files.walk(ghidraPath)) {
                paths.filter(Files::isRegularFile)
                     .filter(path -> path.toString().endsWith(".jar"))
                     .forEach(jarPath -> {
                         JarInfo jarInfo = createJarInfo(jarPath, ghidraPath);
                         jarFiles.add(jarInfo);
                         getLog().debug("Found JAR: " + jarInfo);
                     });
            }

            if (jarFiles.isEmpty()) {
                getLog().warn("No JAR files found in: " + ghidraPath);
            } else {
                getLog().info("Successfully indexed " + jarFiles.size() + " JAR files");

                // Log summary by category
                logJarSummary(jarFiles);
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Error indexing Ghidra JAR files", e);
        }

        // Store jar info in project properties for potential use by other plugins
        project.getProperties().setProperty("ghidra.jars.indexed", String.valueOf(jarFiles.size()));
    }

    private JarInfo createJarInfo(Path jarPath, Path basePath) {
        Path relativePath = basePath.relativize(jarPath);
        String category = determineCategory(relativePath);

        return new JarInfo(
            jarPath.getFileName().toString(),
            jarPath.toString(),
            relativePath.toString(),
            category
        );
    }

    private String determineCategory(Path relativePath) {
        String pathStr = relativePath.toString();

        if (pathStr.contains("Framework")) {
            return "Framework";
        } else if (pathStr.contains("Features")) {
            return "Features";
        } else if (pathStr.contains("Processors")) {
            return "Processors";
        } else if (pathStr.contains("Configurations")) {
            return "Configurations";
        } else if (pathStr.contains("Extensions")) {
            return "Extensions";
        } else {
            return "Other";
        }
    }

    private void logJarSummary(List<JarInfo> jarFiles) {
        getLog().info("========================================");
        getLog().info("Ghidra JAR Index Summary");
        getLog().info("========================================");

        jarFiles.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                JarInfo::getCategory,
                java.util.stream.Collectors.counting()
            ))
            .forEach((category, count) ->
                getLog().info(String.format("  %-20s : %d JAR(s)", category, count))
            );

        getLog().info("========================================");
    }

    /**
     * Data class to hold JAR file information
     */
    private static class JarInfo {
        private final String fileName;
        private final String absolutePath;
        private final String relativePath;
        private final String category;

        public JarInfo(String fileName, String absolutePath, String relativePath, String category) {
            this.fileName = fileName;
            this.absolutePath = absolutePath;
            this.relativePath = relativePath;
            this.category = category;
        }

        public String getFileName() {
            return fileName;
        }

        public String getAbsolutePath() {
            return absolutePath;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return String.format("%s [%s] - %s", fileName, category, relativePath);
        }
    }
}
