package i18n;

import java.text.MessageFormat;
import java.util.ListResourceBundle;

public class MessagesBundleClass_da_DK extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return contents;
    }
    
    MessageFormat primeMform = new MessageFormat(
        "{0} den {1}, fandt vi {2} {3}");
    
    String[] primeChoices = {"ingen primtal","et primtal","{2, number} primtal"};

    private Object[][] contents = {
        { "primeMform", primeMform },
        { "primeChoices", primeChoices },
    };
}

