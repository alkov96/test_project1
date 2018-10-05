package ru.gamble.pages.userProfilePages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

/**
 * @author p.sivak.
 * @since 22.05.2018.
 */
@PageEntry(title = "верхнее меню")
public class TopMenu extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(TopMenu.class);

    @FindBy(xpath = "//div[@class='user-profile__tabs clearfix']")
    private WebElement topMenu;

    @ElementTitle("Профиль")
    @FindBy(xpath = "//div[contains(@class,'user-profile__tabs')]/a[contains(@href, 'details')]")
    private WebElement profileBotton;

    @ElementTitle("Бонусы в личном кабинете")
    @FindBy(xpath = "//div[contains(@class,'user-profile__tabs')]/a[contains(@href, 'bonus')]")
    private WebElement bonusBotton;

    @ElementTitle("История операций")
    @FindBy(xpath = "//div[contains(@class,'user-profile__tabs')]/a[contains(@href,'history')]")
    private WebElement operationHistoryButton;

    @ElementTitle("Сообщения")
    @FindBy(xpath = "//div[contains(@class,'user-profile__tabs')]/a[contains(@href,'message')]")
    private WebElement messagesButton;

    @ElementTitle("Настройка уведомлений")
    @FindBy(xpath = "//div[contains(@class,'user-profile__tabs')]/a[contains(@href,'notifications')]")
    private WebElement notificationsButton;

    public TopMenu() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(driver, this);
        workWithPreloader();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(topMenu));
    }

}
