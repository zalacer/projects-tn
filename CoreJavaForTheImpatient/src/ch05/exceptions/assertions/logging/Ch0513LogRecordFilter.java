package ch05.exceptions.assertions.logging;

import static java.util.logging.Level.FINEST;
import static utils.ConsoleLoggerSetup.setupConsoleLogger;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

// 13. Implement and test a log record filter that filters out log records containing bad
// words such as sex, drugs, and C++.

public class Ch0513LogRecordFilter {
    
    private static final Logger log = Logger.getLogger("filteredLogger");

    public static void main(String[] args) {
        
        setupConsoleLogger(FINEST);
        // testing to show logging is operational
        log.setLevel(Level.ALL);
        log.info("doing stuff");
        log.warning("trouble sneezing");
        log.finer("finer");
        log.finest("finest");
        
        String regex = "(?i)(?!.*(sex|drug|rock|roll|c++)).*";
        Pattern pattern = Pattern.compile(regex);
        log.setFilter(x ->  pattern.matcher(x.getMessage()).matches()); // works
        // log.setFilter(x ->  x.getMessage().matches(regex)); // also works

        log.finest("this is logged");    // logged
        log.severe("seX");               // not logged
        log.warning("druGs");            // not logged
        log.config("rOck");              // not logged
        log.finer("Roll");               // not logged
        log.info("C++");                 // not logged
        log.fine("this is logged");      // logged
        
    }

}
