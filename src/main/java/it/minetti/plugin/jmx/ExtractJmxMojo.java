package it.minetti.plugin.jmx;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.classgraph.MethodInfo;
import it.minetti.plugin.jmx.ObjName.ObjectNamesRoot;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static it.minetti.plugin.BuildScanner.retrieveAnnotatedMethods;
import static it.minetti.plugin.jmx.ObjNameMapper.toObjNames;

@Mojo(name = "extract-jmx", requiresDependencyResolution = ResolutionScope.COMPILE)
public class ExtractJmxMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        List<MethodInfo> annotatedMethods = new ArrayList<>();

        Path outputDir = Paths.get(project.getBuild().getOutputDirectory());
        try {
            annotatedMethods.addAll(retrieveAnnotatedMethods(outputDir));
            getLog().debug("Found methods: " + annotatedMethods);
        } catch (IOException e) {
            getLog().error("Something went wrong parsing the compiled classes", e);
        }

        ObjectNamesRoot objectNamesXmlContent = toObjNames(annotatedMethods);


        try {
            Path jmxMetricDir = Paths.get(project.getBuild().getDirectory())
                    .resolve("generated-resources").resolve("metrics");
            getLog().debug("Preparing to write in " + jmxMetricDir + " ...");
            Files.createDirectories(jmxMetricDir);
            writeToXml(objectNamesXmlContent, jmxMetricDir);
            getLog().info("JMX descriptor file written in " + jmxMetricDir + " with " + objectNamesXmlContent.objNames.size() + " ObjName");
        } catch (IOException e) {
            getLog().error("Something went wrong writing the ObjNames", e);
        }

    }

    public void writeToXml(ObjectNamesRoot objNames, Path outputDir) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Path partialXmlFile = outputDir.resolve(project.getArtifactId() + ".jmx-agent-config.xml");
        try (Writer outputWriter = Files.newBufferedWriter(partialXmlFile, StandardCharsets.UTF_8)) {
            xmlMapper.writeValue(outputWriter, objNames);
        }
    }

}