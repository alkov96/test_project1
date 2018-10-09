package ru.gamble.pages.administrationPages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

/**
 * @author p.sivak.
 * @since 18.05.2018.
 */
@PageEntry(title = "Логин в админку")
public class LoginPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(LoginPage.class);

    @FindBy(xpath = "//div[@class='x-panel x-panel-default-framed x-box-item']")
    private WebElement authForm;

    @ElementTitle("Войти")
    @FindBy(xpath = "//span[@id='button-1014-btnIconEl']")
    private WebElement enterBottom;

    @ElementTitle("Логин")
    @FindBy(xpath = "//input[@id='textfield-1011-inputEl']")
    private WebElement loginField;

    @ElementTitle("Пароль")
    @FindBy(xpath = "//input[@id='textfield-1012-inputEl']")
    private WebElement passField;

    @ActionTitle("логинится в админку")
    public void loginToAdm(){
        loginField.clear();
        loginField.sendKeys("promo_test_superadmin");
        passField.clear();
        passField.sendKeys("promo_test_superadmin");
        enterBottom.click();
    }

    public LoginPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(driver, this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(authForm));
    }
}
