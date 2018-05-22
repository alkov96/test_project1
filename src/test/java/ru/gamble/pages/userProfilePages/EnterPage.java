package ru.gamble.pages.userProfilePages;

import cucumber.api.DataTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.gamble.pages.AbstractPage;
import ru.gamble.utility.Constants;
import ru.gamble.stepdefs.CommonStepDefs;
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

import static ru.gamble.utility.Constants.LOGIN;
import static ru.gamble.utility.Constants.PASSWORD;

@PageEntry(title = "Вход")
public class EnterPage extends AbstractPage {

    @FindBy(xpath = "//div[@class='modal__title' and contains(text(),'Вход')]")
    private WebElement pageTitle;

    @ElementTitle("Электронная почта")
    @FindBy(name = "login")
    private WebElement inputLogin;

    @ElementTitle("Пароль")
    @FindBy(name = "password")
    private WebElement inputPassword;

    @ElementTitle("Войти")
    @FindBy(xpath = "//button[@class='btn_important modal__submit-btn']")
    private WebElement enterButton;

    public EnterPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(pageTitle,10);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("логинится с")
    public void logIn(DataTable dataTable) throws DataException {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String login, password;

        if(data.get(LOGIN).equals(Constants.DEFAULT)){
            login = JsonLoader.getData().get("site1").get("login").getValue();
        } else if(data.get(LOGIN).equals("EMAIL")){
            login = Stash.getValue("EMAIL");
        } else {
            login = data.get(LOGIN);
        }

        if(data.get(PASSWORD).equals(Constants.DEFAULT)){
            password = JsonLoader.getData().get("site1").get("password").getValue();
        }
        else if(data.get(PASSWORD).equals("PASSWORD")) {
            password = Stash.getValue("PASSWORD");
        }else {
            password = data.get(PASSWORD);
        }

        fillField(inputLogin,login);
        fillField(inputPassword,password);
    }
}
