package ru.gamble.pages.userProfilePages;

import cucumber.api.DataTable;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
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
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.Map;
import java.util.stream.Collectors;

import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.*;

@PageEntry(title = "Вход")
public class EnterPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(EnterPage.class);
    static WebDriver driver = PageFactory.getDriver();

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
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);

        for(int j = 0; j < 10; j++) {
            try {
                new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(enterButton));
                    break;
            } catch (Exception e){
                driver.navigate().refresh();
                try {
                    new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("log-in")));
                    driver.findElement(By.id("log-in")).click();
                }catch (Exception e1) {
                    throw new AutotestError("Ошибка! На Главной странице не нашлась кнопка 'ВХОД'.");
                }
            }
            if(j >= 9){
                throw new AutotestError("Ошибка! Не нашли элемент после " + j + " попыток перезагрузки страницы");
            }
        }
    }

    private void chooseMethodAutorization(String method){
        LOG.info("Будем логиниться через method. Для этого выбираем соответсвующую вкладку на попапе авторизации");
        driver.findElement(By.xpath("//div[@class='modal__tabs']/div[contains(.,'" + method + "')]")).click();
        new WebDriverWait(driver,10)
                .withMessage("Попытались переключиться на авторизацию по email, но не получилось")
                .until(ExpectedConditions.attributeContains(By.xpath("//div[@class='modal__tabs']/div[contains(.,'" + method + "')]"),"class","active"));
        LOG.info("Выбрали авторизацию через method");
    }

    /**
     * Авторизация на сайте. в зависимости от того, чму равен епрвый параметр (телефон или нет) выбирается аворизация по теелфону или по email
     * @param dataTable - данные с которыми логинимся. телеон+пароль или логин+пароль
     * @throws DataException
     */
    @ActionTitle("логинится с")
    public void logIn(DataTable dataTable) throws DataException {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        if (data.keySet().toArray()[0].toString().toLowerCase().equals("телефон")){
            chooseMethodAutorization("телефон");
        }
        else {
            chooseMethodAutorization("email");
        }

        chooseMethodAutorization("email");
        String login, password;
        switch (data.get(LOGIN)) {
            case Constants.DEFAULT:
                Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
                String browserName = caps.getBrowserName();
                if (browserName.contains("chrome")) {
                    login = JsonLoader.getData().get(STARTING_URL).get("USER_CHROME").getValue();
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--no-sandbox");
                } else {
                    login = JsonLoader.getData().get(STARTING_URL).get("USER_FIREFOX").getValue();
                }
                break;
            case "EMAIL":
                login = Stash.getValue("EMAIL");
                break;
            default:
                login = data.get(LOGIN).matches("[A-Z_]*")?Stash.getValue(data.get(LOGIN)):data.get(LOGIN);
                break;
        }

        switch (data.get(PASSWORD)) {
            case Constants.DEFAULT:
                password = JsonLoader.getData().get(STARTING_URL).get("PASSWORD").getValue();
                break;
            case "PASSWORD":
                password = Stash.getValue("PASSWORD");
                break;
            default:
                password = data.get(PASSWORD);
                break;
        }
        // КОСТЫЛЬНЫЙ МЕТОД, ТАК-КАК ПОЛЕ ВВОДА ЛОГИНА ДОСТУПНО В СЛУЧАЙНЫЕ ВРЕМЕННЫЕ ПЕРИОДЫ *************************
         int count = 0;
         int count1 = 0;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        do{
             do {
                 LOG.info("Водим в поле::" + login);
                 fillField(inputLogin, login);
                 LOG.info("Попытка ввести::" + login + "[" + count1 + "]");
                 LOG.info("Реально в поле ввелось::" + inputLogin.getAttribute("value"));
                 count1++;
             }while (!(inputLogin.getAttribute("value").equals(login)) && count1 < 3);

            LOG.info("Водим в поле::" + password);
            fillField(inputPassword, password);

            LOG.info("Ожидаем пока кнопка 'Войти' будет видимой");
            new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(.,'Войти')]")));
            LOG.info("Нажимаем на кнопку 'Войти'");
            driver.findElement(By.xpath("//button[contains(.,'Войти')]")).click();
            LOG.info("Попытка ввеcти::" + count);
            count++;

        }while ((!driver.findElements(By.xpath("//div[contains(.,'Ошибка в адресе электронной почты или пароле')]")).stream().filter(WebElement::isDisplayed).collect(Collectors.toList()).isEmpty()) && count < 3);
        workWithPreloader();

        closePopUpWindowGoToTSUPISIfOpened();
      //***********************
    }
}
