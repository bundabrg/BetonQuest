package pl.betoncraft.betonquest.version;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiVersionLoader extends ClassLoader {
    private final String base;
    private final List<String> versions;

    public MultiVersionLoader( ClassLoader parent, String base, String[] versions) {
        super(parent);
        this.base = base;
        this.versions = Arrays.asList(versions);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Only interested if its base package name matches us
        if (!name.startsWith(base)) {
            return super.loadClass(name, resolve);
        }

        if (name.equals("pl.betoncraft.betonquest.version.VersionPluginInterface") ||
                name.equals("pl.betoncraft.betonquest.BetonQuestPlugin")
//                name.startsWith("pl.betoncraft.betonquest.bstats")
        ) {
//            System.err.println("CLASSIGNORE: Trying to load: " + name);
            return super.loadClass(name, resolve);
        }

//        System.err.println("CLASSUSE: Trying to load: " + name);

        List<String> names = new ArrayList<>();
        String definedName = name;

        // Check if version is specified and remove if needed
        String [] nameParts = name.substring(base.length() + 1).split("\\.");
//        System.err.println("Does versions (" + String.join(",", versions) + ") contain " + nameParts[0]);
        if (versions.contains(nameParts[0])) {
//            System.err.println("Yup");
            definedName = base + name.substring(base.length() + 1 + nameParts[0].length());
        }

        // Add each version
        for (String version : versions) {
            names.add(base + "." + version + definedName.substring(base.length()));
        }

        // Add base defined name as well
        names.add(definedName);

        // See if this class is already loaded
        Class <?> c = findLoadedClass(definedName);
        if (c != null) {
            return c;
        }

        return loadAndDefineClass(names, definedName, resolve);
    }

    public Class<?> loadAndDefineClass(List<String> names, String definedName, boolean resolve) throws ClassNotFoundException {
        Class <?> c;

        // Define package if needed
        String packageName = getPackageName(definedName);
//        System.err.println("Current: " + packageName + " - " + getPackageName(packageName));
        if (packageName != null && getPackage(packageName) == null) {
            System.err.println("Defining Package: " + packageName);
            definePackage(packageName, null, null, null, null, null, null, null);
        }

        for (String name: names) {
            String filename = name.replaceAll("\\.", "/") + ".class";
            System.err.println("Trying: " + filename);

            try (InputStream in = getParent().getResourceAsStream(filename)) {
                // Read all the bytes
                byte[] bytes = rewritePackageName(IOUtils.toByteArray(in));
                System.err.println("Defining: " + name + " to " + definedName);
                c = defineClass(definedName, bytes, 0, bytes.length);

                if (resolve) {
                    resolveClass(c);
                }

                return c;
            } catch (IOException | NullPointerException ignored) {
            }
        }

        throw new ClassNotFoundException("Unable to load class: " + names.get(0));
    }

    public byte[] rewritePackageName(byte[] bytecode) throws IOException {
        ClassReader classReader = new ClassReader(bytecode);
        ClassWriter classWriter = new ClassWriter(classReader, 0);

        classReader.accept(
                new ClassRemapper(classWriter, new Remapper() {
                    @Override
                    public String map(String typeName) {
                        if (typeName.startsWith("pl/betoncraft/betonquest/v1_8_R3")) {
                            String newName = "pl/betoncraft/betonquest" + typeName.substring(32);
//                System.err.println("Remapper mapped: " + typeName + " to " + newName);
                            return newName;
                        }

                        return super.map(typeName);
                    }
                }),
                0);


        return classWriter.toByteArray();
    }

    private String getPackageName(String className) {
        int i = className.lastIndexOf('.');
        if (i > 0) {
            return className.substring(0, i);
        } else {
            // No package name, e.g. LsomeClass;
            return null;
        }
    }

}