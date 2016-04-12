package utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtils {
    
    public static String getStackTrace(Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        String[] lines = writer.toString().split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append(lines[i]).append("\n");
        }
        
        return sb.toString();    
    }
    
    public static String shortenedStackTrace(Throwable t, int maxLines) {
    // http://stackoverflow.com/questions/21706722/fetch-only-first-n-lines-of-a-stack-trace    
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        String[] lines = writer.toString().split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(lines.length, maxLines); i++) {
            sb.append(lines[i]).append("\n");
        }
        
        return sb.toString();    
    }

}
