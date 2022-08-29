package application.core.view;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import static application.core.utils.I18nService.i18nService;

public class GlobalMenu extends ContextMenu {
    public static GlobalMenu globalMenu = new GlobalMenu();

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
