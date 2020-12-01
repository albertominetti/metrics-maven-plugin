# Sample for maven plugin and testing

### Goal

The purpose of this project is create a simple maven plugin and test it. Actual purpose is to test it, in an automated way.

### Implementation

The maven plugin, after the compilation of the sources, reads the compiled classes and searches for all micrometer annotation `@Timed`, then it produces an xml file under `target/generated-resources` that describes the classes and methods with the annotation.

### Testing

Testing has been done using the Takari lifecycle, it allows to have a full integration test with different maven version and using a demo project included in the `test/projects` folder. The test looks for the presence of error logs when building the demo project, then looks for the present of the expected logs and check whether the generated file corresponds to the expected one.

### Credits and useful links

* Micrometer https://micrometer.io/docs/concepts
* Takari testing plugin https://github.com/takari/takari-plugin-testing-project
