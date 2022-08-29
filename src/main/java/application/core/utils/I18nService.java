package application.core.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class I18nService {
    private static Locale UK_LOCALE = new Locale("uk");
    private static Locale ZK_CN_LOCALE = new Locale("zh", "CN");
    private static List<Locale> LOCALES = Arrays.asList(UK_LOCALE, ZK_CN_LOCALE);

    public static final I18nService i18nService = new I18nService(UK_LOCALE, UK_LOCALE);

    private Locale defaultLocale;
    private Locale currentLocale;


    //private ResourceBundle bundle;
    private I18nService(Locale defaultLocale, Locale currentLocale) {
        this.defaultLocale = defaultLocale;
        this.currentLocale = currentLocale;
        //bundle = ResourceBundle.getBundle("/messages/messages", ukLocal);
    }

    public void setCurrentLocale(Locale currentLocal) {
        this.currentLocale = currentLocal;
    }

    public List<Locale> getLocals() {
        return LOCALES;
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
