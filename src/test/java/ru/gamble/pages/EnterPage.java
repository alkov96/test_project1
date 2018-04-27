package ru.gamble.pages;

import cucumber.api.DataTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.gamble.pages.utility.BeforeTest;
import ru.gamble.pages.utility.Constants;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.Map;

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
    @FindBy(id = "log-in-button")
    private WebElement enterButton;

    public EnterPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("логинится с")
    public void logIn(DataTable dataTable) throws DataException {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String login, password;
        if(data.get(Constants.LOGIN).equals(Constants.DEFAULT)){ login = BeforeTest.getData().get("site1").get("login").getValue(); }
        else {login = data.get(Constants.LOGIN);}
        if(data.get(Constants.PASSWORD).equals(Constants.DEFAULT)){ password = BeforeTest.getData().get("site1").get("password").getValue(); }
        else {password = data.get(Constants.PASSWORD);}

        fillField(inputLogin,login);
        fillField(inputPassword,password);

        CommonStepDefs.pressButton("Войти");
    }
}
