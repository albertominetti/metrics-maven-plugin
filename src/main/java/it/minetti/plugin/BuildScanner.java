package it.minetti.plugin;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.aop.TimedAspect;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class BuildScanner {

    private BuildScanner() {
        // only static methods
    }

    public static List<MethodInfo> retrieveAnnotatedMethods(Path outputDir) throws IOException {
        try (URLClassLoader classLoader = URLClassLoader.newInstance(
                new URL[]{outputDir.toUri().toURL()},
                BuildScanner.class.getClassLoader())) {
            ClassGraph classGraph = new ClassGraph().addClassLoader(classLoader)
                    .enableAllInfo().whitelistPackages("it.minetti");
            try (ScanResult sr = classGraph.scan()) {
                return sr.getClassesWithMethodAnnotation(Timed.class.getName()).stream()
                        .flatMap(c -> c.getMethodInfo().stream())
                        .filter(m -> m.hasAnnotation(Timed.class.getName()))
                        .collect(Collectors.toList());
            }
        }
    }

    public static String retrieveTimedAnnotationName(MethodInfo methodInfo) {
        AnnotationInfo timedAnnotationInfo = methodInfo.getAnnotationInfo(Timed.class.getName());
        if (timedAnnotationInfo == null) {
            throw new IllegalArgumentException("Method " + methodInfo + " must be annotated as @Timed");
        }

        String value = (String) timedAnnotationInfo.getParameterValues().getValue("value");

        return value.isEmpty() ? TimedAspect.DEFAULT_METRIC_NAME : value;
    }
}
