package ch14.compiling.scripting;

//By Cay Horstmann and available in http://horstmann.com/javaimpatient/javaimpatient.zip 

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class StringSource extends SimpleJavaFileObject {
    private String code;
 
    public StringSource(String name, String code) {
        super(URI.create("string:///" + name.replace('.','/') + ".java"),
              Kind.SOURCE);
        this.code = code;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}