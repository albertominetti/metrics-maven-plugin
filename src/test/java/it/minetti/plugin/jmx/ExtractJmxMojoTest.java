package it.minetti.plugin.jmx;

import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.apache.commons.io.FileUtils.contentEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(MavenJUnitTestRunner.class)
@MavenVersions({"3.6.0"})
public class ExtractJmxMojoTest {

    @Rule
    public final TestResources resources = new TestResources();

    public final MavenRuntime mavenRuntime;

    public ExtractJmxMojoTest(MavenRuntime.MavenRuntimeBuilder builder) throws Exception {
        this.mavenRuntime = builder.build();
    }

    @Test
    public void extractJmx() throws Exception {
        File baseDir = resources.getBasedir("demo");
        MavenExecutionResult result = mavenRuntime
                .forProject(baseDir)
                .execute("clean", "compile", "metrics:extract-jmx");

        result.assertErrorFreeLog();
        result.assertLogText("JMX descriptor file written").assertLogText("with 2 ObjName");

        String expectedFileName = "demo.jmx-agent-config.xml";
        File xmlDescriptor = new File(result.getBasedir(), "target/generated-resources/metrics/" + expectedFileName);

        assertThat(xmlDescriptor.exists(), is(true));
        assertThat(contentEquals(xmlDescriptor, new File("src/test/resources/" + expectedFileName)), is(true));
    }
}