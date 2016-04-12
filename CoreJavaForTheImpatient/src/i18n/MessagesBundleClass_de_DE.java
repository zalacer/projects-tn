package i18n;

import java.text.MessageFormat;
import java.util.ListResourceBundle;

public class MessagesBundleClass_de_DE extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return contents;
    }
    
    MessageFormat primeMform = new MessageFormat(
        "Bei {0} {1}, fanden wir {2} {3}");
    
    String[] primeChoices = 
        {"keine Primzahl","eine Primzahl","{2, number} Primzahlen"};

    private Object[][] contents = {
        { "primeMform", primeMform },
        { "primeChoices", primeChoices },
    };
}

