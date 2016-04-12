package utils;

import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LoggingHTMLFormatter extends java.util.logging.Formatter {
  public String format(LogRecord record) {
    return ("<tr><td>" + (new Date(record.getMillis())).toString() + 
        "</td><td>" + record.getMessage() + "</td></tr>\n");
  }

  public String getHead(Handler h) {
    return ("<html>\n  <body>\n" + 
        "<Table border>\n<tr><td>Time</td><td>Log Message</td></tr>\n");
  }

  public String getTail(Handler h) {
    return ("</table>\n</body>\n</html>");
  }
}