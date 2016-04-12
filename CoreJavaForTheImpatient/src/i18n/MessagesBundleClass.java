package i18n;

import java.text.MessageFormat;
import java.util.ListResourceBundle;

public class MessagesBundleClass extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return contents;
    }
    
    MessageFormat primeMform = new MessageFormat(
        "At {0} on {1}, we found {2} {3}");
    
    String[] primeChoices = {"no prime number","one prime number","{2, number} prime numbers"};

    private Object[][] contents = {
        { "primeMform", primeMform },
        { "primeChoices", primeChoices },
    };
}

