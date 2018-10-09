package ru.gamble.pages.tsupis;

import cucumber.api.DataTable;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
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

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.gamble.utility.Constants.*;

@PageEntry(title = "Первый ЦУПИС")
public class TSUPISMainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(TSUPISMainPage.class);

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
        PageFactory.initElements(driver, this);
        LOG.info("Перешли на страницу [" + driver.getCurrentUrl() + "]");
        new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOf(inputPhone));
    }

    @ActionTitle("логинится в ЦУПИС с")
    public void loginInTSUPIS(DataTable dataTable){
        WebDriver driver = PageFactory.getWebDriver();
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String phone = "", password = "";

        try {
            if(data.get(PHONE).equals(DEFAULT)){
                Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
                String browserName = caps.getBrowserName();
                if (browserName.contains("chrome")){
                    phone = JsonLoader.getData().get(STARTING_URL).get("PHONE").getValue();
                }
                else {
                    phone = JsonLoader.getData().get(STARTING_URL).get("PHONE_FIREFOX").getValue();
                }

            } else {
                phone = data.get(PHONE);
            }
            if(data.get(PASSWORD).equals(DEFAULT)){
                password = JsonLoader.getData().get(STARTING_URL).get("PASSWORD").getValue();
                }else {
                password = data.get(PASSWORD);
            }
        } catch (DataException e) {
            e.printStackTrace();
        }

        fillField(inputPhone, phone);
        String actual = inputPhone.getAttribute("value").replaceAll("\\D","");
        assertThat(actual).as("ОШИБКА! Вводимый номер [" + phone + "] не соответсвует [" + actual + "]").contains(phone);
        LOG.info("В поле [Телефон] ввели [" + actual  +"]");

        fillField(inputPassword, password);
        LOG.info("В поле [Пароль] ввели [" + password  +"]");
    }
}
