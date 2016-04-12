package utils;

// http://stackoverflow.com/questions/470430/java-util-logging-logger-doesnt-respect-java-util-logging-level

import java.util.logging.*;

public class ConsoleLoggerSetup {

	public static void setupConsoleLogger(Level level) {
		// get the top Logger:
		Logger topLogger = java.util.logging.Logger.getLogger("");

		// Handler for console (reuse it if it already exists)
		Handler consoleHandler = null;
		// see if there is already a console handler
		for (Handler handler : topLogger.getHandlers()) {
			if (handler instanceof ConsoleHandler) {
				// found the console handler
				//System.out.println("found the console handler");
				consoleHandler = handler;
				break;
			}
		}

		if (consoleHandler == null) {
			// there was no console handler found, create a new one
			consoleHandler = new ConsoleHandler();
			//System.out.println("created new ConsoleHandler");
			topLogger.addHandler(consoleHandler);
		}
		// set the console handler to level:
		consoleHandler.setLevel(level);
	}
	
	// this is not effective in overriding default ConsoleLogger
	public static void newConsoleLogger(Level level) {
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(level);
	}

}
