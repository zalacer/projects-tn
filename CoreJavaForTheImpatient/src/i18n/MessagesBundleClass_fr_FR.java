package i18n;

import java.text.MessageFormat;
import java.util.ListResourceBundle;

public class MessagesBundleClass_fr_FR extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return contents;
    }
    
    MessageFormat primeMform = new MessageFormat(             
        "À {0} le {1}, nous avons trouvé {2} {3}");
    
    String[] primeChoices = 
        {"aucun nombre premier","un nombre premier","{2, number} nombres premiers"};

    private Object[][] contents = {
        { "primeMform", primeMform },
        { "primeChoices", primeChoices },
    };
}

