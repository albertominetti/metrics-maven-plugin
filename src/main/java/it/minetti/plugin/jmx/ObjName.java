package it.minetti.plugin.jmx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

public class ObjName {
    @JacksonXmlProperty(isAttribute = true)
    public String name;

    @JacksonXmlProperty(localName = "AttributeLong")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<AttributeLong> attributeLongList = new ArrayList<>();

    public static class AttributeLong {
        @JacksonXmlProperty(isAttribute = true)
        public String name;

        public AttributeLong(String name) {
            this.name = name;
        }
    }

    @JacksonXmlRootElement(localName = "root")
    public static class ObjectNamesRoot {

        @JacksonXmlProperty(localName = "ObjNames")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<ObjName> objNames;

        public ObjectNamesRoot(List<ObjName> objNames) {
            this.objNames = objNames;
        }
    }
}
