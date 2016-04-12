package ch05.exceptions.assertions.logging;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import utils.LoggingHTMLFormatter;

// 14. Implement and test a log record formatter that produces an HTML file.

public class Ch0514LogFormatter {

    public static void main(String args[]) throws Exception {
        LogManager lm = LogManager.getLogManager();
        // log.html in this project the logging output file
        FileHandler html_handler = new FileHandler("log.html");
        Logger logger = Logger.getLogger("logger");
        lm.addLogger(logger);
        logger.setLevel(Level.ALL);
        html_handler.setFormatter(new LoggingHTMLFormatter());
        logger.addHandler(html_handler); 
        logger.warning("severe message");
        logger.info("warning message");
        logger.severe("info message"); 
        logger.fine("config message");
        logger.finest("fine message");
        logger.config("finer message");
        logger.config("finest message");
        html_handler.close();
    }
}