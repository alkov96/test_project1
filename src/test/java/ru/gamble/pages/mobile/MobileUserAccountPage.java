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
import ru.gamble.utility.Generators;
import ru.gamble.utility.JsonLoader;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.Map;

import static ru.gamble.utility.Constants.*;

@PageEntry(title = "Учетная запись мобильная")
public class MobileUserAccountPage extends AbstractPage {

    private static final Logger LOG = LoggerFactory.getLogger(MobileUserAccountPage.class);

    @FindBy(xpath = "//div/h2[contains(.,'Учетная запись')]")
    private WebElement pageTitle;

    @ElementTitle("Дата рождения")
    @FindBy(xpath = "//div[contains(@class,'datepicker__main')]")
    private WebElement birthDateInput;

    @ElementTitle("Фамилия")
    @FindBy(id = "surname")
    private WebElement surnameInput;

    @ElementTitle("Имя")
    @FindBy(id = "first_name")
    private WebElement firstNameInput;

    @ElementTitle("Отчество")
    @FindBy(id = "patronymic")
    private WebElement patronymicInput;

    @ElementTitle("Эл. почта")
    @FindBy(id = "email")
    private WebElement emailInput;

    @ElementTitle("Мобильный телефон")
    @FindBy(id = "phone")
    private WebElement phoneInput;

    @FindBy(id = "phoneConfirmationCode")
    private WebElement phoneConfirmationCodeInput;

    @ElementTitle("Укажите пароль")
    @FindBy(name = "password")
    private WebElement passwordInput;

    @ElementTitle("Регистрируясь...")
    @FindBy(xpath = "//label[@for='rulesAccepted']")
    private WebElement rulesAcceptedCheckbox;

    @ElementTitle("Я соглашаюсь...")
    @FindBy(xpath = "//label[@for='accept_subscription']")
    private WebElement acceptSubscriptionCheckbox;

    @ElementTitle("ПРОДОЛЖИТЬ")
    @FindBy(xpath = "//button[contains(.,'Продолжить')]")
    private WebElement submitButton;


    public MobileUserAccountPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("заполняет мобильную форму с")
    public void fillsMobileForm(DataTable dataTable) throws DataException {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String inputField, value, saveVariable, date = "";

        for (Map<String, String> aTable : table) {

            inputField = aTable.get(INPUT_FIELD);
            value = aTable.get(VALUE);
            saveVariable = aTable.get(SAVE_VALUE);

            if (inputField.contains(LASTNAME)) {
                if (value.contains(RANDOM)) {
                    fillField(surnameInput, Generators.randomString(25));
                } else {
                    fillField(surnameInput, value);
                }
                Stash.put(saveVariable, surnameInput.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + surnameInput.getAttribute("value") + "]");
            }
            if (inputField.contains(NAME)) {
                if (value.contains(RANDOM)) {
                    fillField(firstNameInput, Generators.randomString(25));
                } else {
                    fillField(firstNameInput, value);
                }
                Stash.put(saveVariable, firstNameInput.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + firstNameInput.getAttribute("value") + "]");
            }
            if (inputField.contains(PATERNALNAME)) {
                if (value.contains(RANDOM)) {
                    fillField(patronymicInput, Generators.randomString(25));
                } else {
                    fillField(patronymicInput, value);
                }
                Stash.put(saveVariable, patronymicInput.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + patronymicInput.getAttribute("value") + "]");
            }
            if (inputField.contains("Эл. почта")) {
                String email = Stash.getValue(("EMAIL"));
                fillField(emailInput, email);
                LOG.info(saveVariable + "<==[" + emailInput.getAttribute("value") + "]");
            }
            if (inputField.contains("Мобильный телефон")) {
                super.enterSellphone(value,phoneInput,phoneConfirmationCodeInput);
                StringBuilder builder = new StringBuilder();
                builder.append("7").append(phoneInput.getAttribute("value").trim().replaceAll("\n", "").replaceAll("-", "").replaceAll(" ", ""));
                Stash.put(saveVariable, String.valueOf(builder.toString()));
                LOG.info(saveVariable + "<==[" + builder.toString() + "]");
            }
            if (inputField.contains(PASSWORD)) {
                String password = (value.equals(Constants.DEFAULT)) ? JsonLoader.getData().get(STARTING_URL).get("PASSWORD").getValue() : value;
                LOG.info("Вводим пароль::" + password);
                fillField(passwordInput, password);
                Stash.put(saveVariable, password);
            }
        }
    }

}
