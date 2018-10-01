package ru.gamble.pages.mobile;

import cucumber.api.DataTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.utility.Constants;
import ru.gamble.utility.JsonLoader;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.Map;

import static ru.gamble.utility.Constants.*;

@PageEntry(title = "Мобильный вход")
public class MobileEnterWindow extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileEnterWindow.class);

    @ElementTitle("Электронная почта")
    @FindBy(id = "login_name")
    private WebElement inputEmail;

    @ElementTitle("Пароль")
    @FindBy(xpath = "//input[@placeholder='Пароль']")
    private WebElement inputPassword;

    @ElementTitle("ВОЙТИ")
    @FindBy(xpath = "//button[@type='submit' and contains(.,'Войти')]")
    private WebElement buttonEnter;

    @ElementTitle("Регистрация")
    @FindBy(xpath = "//a[contains(.,'Регистрация')]")
    private WebElement registrationLink;

    public MobileEnterWindow() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        waitingForPreloaderToDisappear(30);
        tryingLoadPage(inputEmail,5, 10);
    }

    @ActionTitle("залогинивается с мобильными")
    public void loginWithMobile(DataTable dataTable){
        WebDriver driver = PageFactory.getWebDriver();
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        String email = "", password = "";
        if(data.get(EMAIL).equals(Constants.DEFAULT)) {
            try {
                email = JsonLoader.getData().get(STARTING_URL).get("USER").getValue();
            } catch (DataException e) {
                e.getMessage();
            }
        } else if(data.get(EMAIL).matches("^[A-Z_]+$")){
            email = Stash.getValue(data.get(EMAIL));
        }else { email = data.get(EMAIL);}

        if(data.get(PASSWORD).equals(Constants.DEFAULT)){
            try {
                password = JsonLoader.getData().get(STARTING_URL).get("PASSWORD").getValue();
            } catch (DataException e) {
                e.getMessage();
            }
        }else if(data.get(PASSWORD).matches("^[A-Z_]+$")){
            password = Stash.getValue(data.get(PASSWORD));
        }else { password = data.get(PASSWORD);}

        fillField(inputEmail,email);
        LOG.info("В поле e-mail ввели [" + inputEmail.getAttribute("value") + "]");

        new WebDriverWait(driver,5).until(ExpectedConditions.visibilityOf(inputPassword));
        fillField(inputPassword,password);
        LOG.info("В поле password ввели [" + inputPassword.getAttribute("value") + "]");
    }
}
