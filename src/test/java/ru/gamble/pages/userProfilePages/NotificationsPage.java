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
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;

import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

/**
 * @author p.sivak.
 * @since 21.05.2018.
 */
@PageEntry(title = "Настройка уведомлений")
public class NotificationsPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationsPage.class);

    @FindBy(xpath = "//div[@class='user-profile__tab-wraper ng-scope']")
    private WebElement tabWraper;

    public NotificationsPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(driver, this);
        workWithPreloader();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(tabWraper));
    }
}
