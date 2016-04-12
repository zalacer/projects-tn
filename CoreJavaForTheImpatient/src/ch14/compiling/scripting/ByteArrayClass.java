package ch14.compiling.scripting;

// By Cay Horstmann and available in http://horstmann.com/javaimpatient/javaimpatient.zip 

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class ByteArrayClass extends SimpleJavaFileObject {
    private ByteArrayOutputStream out;

    public ByteArrayClass(String name) {
        super(URI.create("bytes:///" + name.replace('.','/') + ".class"),
            Kind.CLASS);
    }
    
    public byte[] getCode() {
        return out.toByteArray();
    }
    
    @Override
    public OutputStream openOutputStream() throws IOException {
        // TODO Auto-generated method stub
        out = new ByteArrayOutputStream();
        return out;
    }        
}

