package i18n;

import java.util.ListResourceBundle;

public class MyClassBundle_da extends ListResourceBundle {

  @Override
  protected Object[][] getContents() {
      return contents;
  }

  private Object[][] contents = {
          { "price"   , new Double(75.00) },
          { "currency", "DKK" },
  };

}
