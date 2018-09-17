package ru.gamble.pages.mobile;

import cucumber.api.DataTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.utility.Constants;
import ru.gamble.utility.JsonLoader;
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
    @FindBy(name = "password")
    private WebElement inputPassword;

    @ElementTitle("Войти")
    @FindBy(xpath = "//button[@type='submit']")
    private WebElement buttonEnter;

    @ElementTitle("Регистрация")
    @FindBy(xpath = "//a[contains(.,'Регистрация')]")
    private WebElement registrationLink;

    public MobileEnterWindow() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(inputEmail,5, 10);
    }

    @ActionTitle("залогинивается с мобильными")
    public void loginWithMobile(DataTable dataTable){
        WebDriver driver = PageFactory.getWebDriver();
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        String email = "", password = "";
       // EMAIL = "E-mail";
        if(data.get(EMAIL).equals(Constants.DEFAULT)) {
            try {
                email = JsonLoader.getData().get(STARTING_URL).get("USER").getValue();
            } catch (DataException e) {
                e.getMessage();
            }
        } else {
            email = data.get(EMAIL);
        }

        if(data.get(PASSWORD).equals(Constants.DEFAULT)){
            try {
                password = JsonLoader.getData().get(STARTING_URL).get("password").getValue();
            } catch (DataException e) {
                e.getMessage();
            }
        } else {
            password = data.get(PASSWORD);
        }

        fillField(inputEmail,email);
        LOG.info("В поле e-mail ввели::" + inputEmail.getAttribute("value"));
        fillField(inputPassword,password);
        LOG.info("В поле password ввели::" + inputPassword.getAttribute("value"));

    }


}
