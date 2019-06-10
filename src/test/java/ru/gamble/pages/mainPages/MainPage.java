package ru.gamble.pages.mainPages;


import cucumber.api.DataTable;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.gamble.utility.Constants;
import ru.gamble.utility.JsonLoader;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.*;
import static ru.gamble.utility.Constants.PASSWORD;

@PageEntry(title = "Главная страница админки неавторизованная")
public class MainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MainPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(id = "textfield-1011-inputEl")
    private WebElement inputLogin;

    @ElementTitle("Пароль")
    @FindBy(id = "textfield-1012-inputEl")
    private WebElement inputPassword;

    @FindBy(id = "button-1014")
    private WebElement enterButton;

    @ElementTitle("Регистрация")
    @FindBy(id = "register")
    private WebElement registrationButton;



    public MainPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(By.id("textfield-1011-inputEl"),3, 5);
        workWithPreloader();
    }


    @ActionTitle("осуществляет переход на страницу, проверяет, что открылась нужная страница")
    public void widgetsOnMain(){
        List<WebElement> attr = driver.findElements(By.xpath("//div[@class='benef__item']/a"));
        for (WebElement element : attr) {
            String link = element.getAttribute("href");
            CommonStepDefs.goLink(element, link);
            LOG.info("Ссылка " + link + " открылась");
        }
    }

    @ActionTitle("логинится с")
    public void logIn(DataTable dataTable) throws DataException {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        String login, password;
        switch (data.get(LOGIN)) {
            case Constants.DEFAULT:

                login = JsonLoader.getData().get(STARTING_URL).get("USER_ADMIN").getValue();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--no-sandbox");
                break;
            default:
                login = data.get(LOGIN);
        }

        switch (data.get(PASSWORD)) {
            case Constants.DEFAULT:
                password = JsonLoader.getData().get(STARTING_URL).get("PASSWORD_ADMIN").getValue();
                break;

            default:
                password = data.get(PASSWORD);
                break;
        }

        LOG.info("Водим в поле::" + login);
        fillField(inputLogin, login);

        LOG.info("Водим в поле::" + password);
        fillField(inputPassword, password);
        LOG.info("Нажимаем кнопку вход");
        enterButton.click();

    }

}
