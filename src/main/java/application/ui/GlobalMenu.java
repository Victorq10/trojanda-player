package application.ui;

import application.services.impl.DefaultI18nService;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class GlobalMenu extends ContextMenu {
    public static GlobalMenu INSTANCE = new GlobalMenu();
    
    private DefaultI18nService i18nService = DefaultI18nService.INSTANCE;

    private GlobalMenu() {
        MenuItem settingMenuItem = new MenuItem(i18nService.getMessage("menu.settings"));
        MenuItem updateMenuItem = new MenuItem(i18nService.getMessage("menu.update"));
        MenuItem feedbackMenuItem = new MenuItem(i18nService.getMessage("menu.feedback"));
        MenuItem aboutMenuItem = new MenuItem(i18nService.getMessage("menu.about"));
        MenuItem companyMenuItem = new MenuItem(i18nService.getMessage("menu.company"));
        
        getItems().add(settingMenuItem);
        getItems().add(updateMenuItem);
        getItems().add(companyMenuItem);
        getItems().add(feedbackMenuItem);
        getItems().add(aboutMenuItem);
    }
}
