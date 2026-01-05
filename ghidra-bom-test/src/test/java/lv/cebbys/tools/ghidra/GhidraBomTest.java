package lv.cebbys.tools.ghidra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify Ghidra BOM imports work correctly.
 * This test verifies that Ghidra API classes can be loaded and accessed.
 */
public class GhidraBomTest {

    @Test
    public void testGhidraFrameworkUtilityClassesAvailable() throws ClassNotFoundException {
        // Test that Framework-Utility classes are available
        Class<?> applicationClass = Class.forName("ghidra.framework.Application");
        assertNotNull(applicationClass, "ghidra.framework.Application should be available");
    }

    @Test
    public void testGhidraFrameworkGenericClassesAvailable() throws ClassNotFoundException {
        // Test that Framework-Generic classes are available
        Class<?> genericClass = Class.forName("generic.jar.ResourceFile");
        assertNotNull(genericClass, "generic.jar.ResourceFile should be available");
    }

    @Test
    public void testGhidraSoftwareModelingClassesAvailable() throws ClassNotFoundException {
        // Test that Framework-SoftwareModeling classes are available
        Class<?> programClass = Class.forName("ghidra.program.model.listing.Program");
        assertNotNull(programClass, "ghidra.program.model.listing.Program should be available");

        Class<?> addressClass = Class.forName("ghidra.program.model.address.Address");
        assertNotNull(addressClass, "ghidra.program.model.address.Address should be available");
    }

    @Test
    public void testGhidraFeaturesBaseClassesAvailable() throws ClassNotFoundException {
        // Test that Features-Base classes are available
        Class<?> codeUnitClass = Class.forName("ghidra.app.util.cparser.C.CParser");
        assertNotNull(codeUnitClass, "ghidra.app.util.cparser.C.CParser should be available");
    }
}
