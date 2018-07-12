package ru.gamble.pages.userProfilePages;

import cucumber.api.DataTable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.sbtqa.tag.qautils.properties.Props;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.Map;

import static ru.gamble.utility.Constants.LOGIN;
import static ru.gamble.utility.Constants.PASSWORD;
import static ru.gamble.utility.Constants.STARTING_URL;

@PageEntry(title = "Вход")
public class EnterPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(EnterPage.class);

    @FindBy(xpath = "//div[@class='modal__title' and contains(text(),'Вход')]")
    private WebElement pageTitle;

    @ElementTitle("Электронная почта")
    @FindBy(name = "login")
    private WebElement inputLogin;

    @ElementTitle("Пароль")
    @FindBy(name = "password")
    private WebElement inputPassword;

    @ElementTitle("Войти")
    @FindBy(xpath = "//button[contains(.,'Войти')]")
    private WebElement enterButton;

    public EnterPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        for(int j = 0; j < 10; j++) {
            try {
                new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(inputLogin));
                if (enterButton.isDisplayed()) {
                    break;
                }
            } catch (Exception e){
                driver.navigate().refresh();
                try {
                    new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("log-in")));
                    driver.findElement(By.id("log-in")).click();
                }catch (Exception e1) {
                    new AutotestError("Ошибка! На Главной странице не нашлась кнопка 'ВХОД'.");
                }
            }
            if(j >= 9){
                throw new AutotestError("Ошибка! Не нашли элемент после " + j + " попыток перезагрузки страницы");
            }
        }
    }

    @ActionTitle("логинится с")
    public void logIn(DataTable dataTable) throws DataException {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String login, password;
        if(data.get(LOGIN).equals(Constants.DEFAULT)){
            login = JsonLoader.getData().get(STARTING_URL).get("login").getValue();
        } else if(data.get(LOGIN).equals("EMAIL")){
            login = Stash.getValue("EMAIL");
        } else {
            login = data.get(LOGIN);
        }

        if(data.get(PASSWORD).equals(Constants.DEFAULT)){
            password = JsonLoader.getData().get(STARTING_URL).get("password").getValue();
        }
        else if(data.get(PASSWORD).equals("PASSWORD")) {
            password = Stash.getValue("PASSWORD");
        }else {
            password = data.get(PASSWORD);
        }

        LOG.info("Водим в поле::" + login);
        fillField(inputLogin,login);
        LOG.info("Водим в поле::" + password);
        fillField(inputPassword,password);
        LOG.info("Ожидаем пока кнопка 'Войти' будет видимой");
        new WebDriverWait(PageFactory.getWebDriver(),10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(.,'Войти')]")));
    }
}
