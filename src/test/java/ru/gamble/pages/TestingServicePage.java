package ru.gamble.pages;

import cucumber.api.java.en.Then;
import cucumber.api.java.ru.Когда;
import net.minidev.json.JSONValue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.userProfilePages.PopUPLCPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.gamble.utility.JsonLoader;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static ru.gamble.utility.Constants.DEFAULT;
import static ru.gamble.utility.Constants.STARTING_URL;

@PageEntry(title = "Тестовый сервис", url = "http://88.198.200.81:27000/testservice/")
public class TestingServicePage extends AbstractPage{

    private static final Logger LOG = LoggerFactory.getLogger(TestingServicePage.class);


    @FindBy(xpath = "//div[@class='logo' and contains(text(),'Тестовый сервис')]")
    private WebElement pageTitle;

    @ElementTitle("Выйти")
    @FindBy(id = "log-out-button")
    private WebElement exitButton;

    @ElementTitle("Идентификатор мерчанта")
    @FindBy(id = "merchantId")
    private WebElement identMerchantInput;

    @ElementTitle("Код/логин/ид/имя пользователя")
    @FindBy(id = "customerCode")
    private WebElement customerCodeInput;


    @ElementTitle("Способ идентификации")
    @FindBy(id = "method")
    private WebElement methodSelect;

    @ElementTitle("Полная идентификация")
    @FindBy(xpath = "//input[contains(@value,'Полная идентификация')]")
    private WebElement fullIdentButton;


    public TestingServicePage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
        LOG.info("Сейчас мы на странице [" + driver.getCurrentUrl() + "]");
    }


    @ActionTitle("ищет последнее отправленное SMS по номеру и запоминает в")
    public void userSearchesForLastSentSMSByNumberAndRemembersIn(String keyPhoneNumber, String keySMS) {
        WebDriver driver = PageFactory.getDriver();
        String currentPage = driver.getCurrentUrl();
        String sms, number = "";
        try {
            number = keyPhoneNumber.equals(DEFAULT) ? JsonLoader.getData().get(STARTING_URL).get("PHONE").getValue() : keyPhoneNumber ;
        } catch (DataException e) {
            e.printStackTrace();
        }

        String xpath = "//p[contains(.,'Последние отправленные смс:')]/following-sibling::ul/li[contains(.,'" + number + "')]";
        StringBuilder tmp = new StringBuilder();

            sms = PageFactory.getDriver().findElement(By.xpath(xpath)).getText();

            Pattern pat = Pattern.compile("(?<= код\\s)[\\d]{4}");
            Matcher matcher = pat.matcher(sms);
            while (matcher.find()) {
                tmp.append(matcher.group());
            }
            if(tmp.toString().isEmpty()){
                throw new AutotestError("Ошибка! Не нашли строку с номером телефона [" + number + "]");
            }else {
                LOG.info("Сохраняем в памяти key[" + keySMS + "] valie[" + tmp.toString() + "]");
                Stash.put(keySMS, tmp.toString());
            }

    }

    @ActionTitle("вводит в поле")
    public void inputInField(String inputField, String text){
        String inputText = (text.matches("^[A-Z_]+$")) ? Stash.getValue(text) : text;
        try {
            fillField(inputField, inputText);
        } catch (PageException e) {
            e.getMessage();
        }
    }


}
