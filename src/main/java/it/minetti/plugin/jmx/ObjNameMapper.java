package it.minetti.plugin.jmx;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.MethodInfo;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import it.minetti.plugin.jmx.ObjName.ObjectNamesRoot;

import java.util.List;
import java.util.stream.Collectors;

import static it.minetti.plugin.BuildScanner.retrieveTimedAnnotationName;

public class ObjNameMapper {
    private static final String TIMED = "metrics:type=timers,name=%s.class.%s.exception.*.method.%s";
    private static final String COUNTED = "metrics:type=counters,name=%s.class.%s.exception.*.method.%s";
    private static final NamingConvention defaultNamingConvention = NamingConvention.camelCase;


    private ObjNameMapper() {
        // only static methods
    }

    public static ObjectNamesRoot toObjNames(List<MethodInfo> methodInfoList) {
        List<ObjName> objNames = methodInfoList.stream().map(ObjNameMapper::toObjName).collect(Collectors.toList());

        return new ObjectNamesRoot(objNames);
    }


    public static ObjName toObjName(MethodInfo methodInfo) {

        ObjName objName = new ObjName();

        AnnotationInfo timedAnnotationInfo = methodInfo.getAnnotationInfo(Timed.class.getName());
        if (timedAnnotationInfo != null) {
            String value = retrieveTimedAnnotationName(methodInfo);
            objName.name = String.format(TIMED, defaultNamingConvention.name(value, Meter.Type.TIMER),
                    methodInfo.getClassInfo().getName(), methodInfo.getName());
            objName.attributeLongList.add(new ObjName.AttributeLong("999thPercentile"));
            objName.attributeLongList.add(new ObjName.AttributeLong("99thPercentile"));
            objName.attributeLongList.add(new ObjName.AttributeLong("50thPercentile"));
        }

        objName.attributeLongList.add(new ObjName.AttributeLong("Count"));
        return objName;
    }


}
