package i18n;

import java.util.ListResourceBundle;

public class MyClassBundle_de_DE extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return contents;
    }

    private Object[][] contents = {
            { "price"   , new Double(11.00) },
            { "currency", "USD" },
    };
}
