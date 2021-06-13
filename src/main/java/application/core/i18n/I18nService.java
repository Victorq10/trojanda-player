package application.core.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class I18nService {
    public static final I18nService i18nService = new I18nService();
    private static Locale ukLocale;
    private static Locale zhCNLocale;

    {
        ukLocale = new Locale("uk");
        zhCNLocale = new Locale("zh", "CN");
    }

    private Locale defaultLocale = ukLocale;
    private Locale currentLocale = ukLocale;


    //private ResourceBundle bundle;
    private I18nService() {
        //bundle = ResourceBundle.getBundle("/messages/messages", ukLocal);
    }

    public void setCurrentLocale(Locale currentLocal) {
        this.currentLocale = currentLocal;
    }

    public List<Locale> getLocals() {
        return Arrays.asList(ukLocale, zhCNLocale);
    }

    public String getMessage(final String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages.messages", currentLocale);
        if (bundle.containsKey(key)) {
            String message = bundle.getString(key);
            return message;
        } else if (!currentLocale.equals(defaultLocale)) {
            ResourceBundle defaultBundle = ResourceBundle.getBundle("messages.messages", defaultLocale);
            if (defaultBundle.containsKey(key)) {
                String message = defaultBundle.getString(key);
                return message;
            }
        }
        return "~Empty~";
    }
}
