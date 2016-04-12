package ch09.io;

import java.awt.datatransfer.DataFlavor;
import java.net.MalformedURLException;
import java.net.URL;

import javax.activation.ActivationDataFlavor;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.management.loading.MLet;
import javax.management.loading.PrivateMLet;

// 16. Which classes in the standard Java library implement Externalizable? 
// Which of them use writeReplace/readResolve?

// The classes are ActivationDataFlavor, DataFlavor, MimeType, MLet and PrivateMLet.
// None of them implement writeReplace or readResolve since those methods always require 
// custom implementation. However they all have implementations of writeExternal and
// readExternal, because that's what Externalizable is for. In the case of PrivateMLet 
// these methods are inherited from MLet and each of the other classes has its own 
// implementations.

public class Ch0916Externalizable {

  public static void main(String[] args) {

    ActivationDataFlavor adf = 
        new ActivationDataFlavor(String.class, "text/xml", "XML String");
    // implements writeExternal, readExternal
    System.out.println(adf);

    DataFlavor df = new DataFlavor(Object.class, 
        "X-test/test; class=<java.lang.Object>; foo=bar");
    // implements writeExternal, readExternal
    System.out.println(df);

    MimeType mt = null;
    // implements writeExternal, readExternal
    try {
      mt = new MimeType("text/plain");
    } catch (MimeTypeParseException e) {
      e.printStackTrace();
    }
    System.out.println(mt);

    URL u1 = null;
    URL u2 = null;
    URL u3 = null;
    try {
      u1 = new URL("http://url1.com/");
      u2 = new URL("http://url2.com/");
      u3 = new URL("http://url2.com/");
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    URL[] u = new URL[]{u1,u2,u3};

    MLet ml = new MLet(u);
    // implements writeExternal, readExternal
    System.out.println(ml);

    boolean delegateToCLR = true;
    PrivateMLet pml = new PrivateMLet(u, delegateToCLR);
    // implements writeExternal, readExternal
    System.out.println(pml); 

  }

}

// output
//javax.activation.ActivationDataFlavor[mimetype=text/xml;representationclass=java.io.InputStream;charset=UTF-8]
//java.awt.datatransfer.DataFlavor[mimetype=application/x-java-serialized-object;representationclass=java.lang.Object]
//text/plain
//javax.management.loading.MLet@7852e922
//javax.management.loading.PrivateMLet@70dea4e

