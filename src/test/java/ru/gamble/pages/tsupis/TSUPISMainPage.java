package ru.gamble.pages.tsupis;

import cucumber.api.DataTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.utility.JsonLoader;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import static ru.gamble.utility.Constants.*;

import java.util.Map;

@PageEntry(title = "Первый ЦУПИС")
public class TSUPISMainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(TSUPISMainPage.class);

    @ElementTitle("Первый ЦУПИС")
    @FindBy(xpath = "//div/a[contains(@href,'https://23bet.itasystems.ru/frontend/')]")
    private WebElement pageTitle;

    @ElementTitle("Телефон")
    @FindBy(id = "form_login_phone")
    private WebElement inputPhone;

    @ElementTitle("Пароль")
    @FindBy(id = "form_login_password")
    private WebElement inputPassword;

    @ElementTitle("Войти")
    @FindBy(id = "btn_authorization_enter")
    private WebElement buttonEnter;

    public TSUPISMainPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("логинится в ЦУПИС с")
    public void loginInTSUPIS(DataTable dataTable){
        WebDriver driver = PageFactory.getWebDriver();
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String phone = null, password = null;

        try {
            if(data.get(PHONE).equals(DEFAULT)){
                phone = JsonLoader.getData().get(STARTING_URL).get("phone").getValue();
            } else {
                phone = data.get(PHONE);
            }
            if(data.get(PASSWORD).equals(DEFAULT)){
                password = JsonLoader.getData().get(STARTING_URL).get("password").getValue();
                }else {
                password = data.get(PASSWORD);
            }
        } catch (DataException e) {
            e.printStackTrace();
        }
        LOG.info("Водим в поле::" + phone);
        fillField(inputPhone, phone);

        LOG.info("Водим в поле::" + password);
        fillField(inputPassword, password);
    }
}
