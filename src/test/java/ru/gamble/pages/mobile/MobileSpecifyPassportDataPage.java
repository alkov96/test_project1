package ru.gamble.pages.mobile;

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
import ru.gamble.utility.Generators;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static ru.gamble.utility.Constants.*;

@PageEntry(title = "Укажите паспортные данные")
public class MobileSpecifyPassportDataPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileSpecifyPassportDataPage.class);

    @FindBy(xpath = "//div/h2[contains(.,'Укажите паспортные')]")
    private WebElement pageTitle;

    @ElementTitle("Серия и номер")
    @FindBy(id = "passport")
    private WebElement serialAndNumberInput;

    @ElementTitle("Дата выдачи")
    @FindBy(xpath = "//div[@class='datepicker__overlay']")
    private WebElement birthDateInput;

    @ElementTitle("Кем выдан")
    @FindBy(name = "passport_issuer")
    private WebElement issuedByInput;

    @ElementTitle("Код подразделения")
    @FindBy(id = "passport_issuer_code")
    private WebElement unitCodeInput;

    @ElementTitle("Мужской")
    @FindBy(xpath = "//input[@value='MALE']/following-sibling::label")
    private WebElement maleButton;

    @ElementTitle("Женский")
    @FindBy(xpath = "//input[@value='FEMALE']/following-sibling::label")
    private WebElement femaleButton;

    @ElementTitle("Место рождения")
    @FindBy(id = "birth_place")
    private WebElement birthPlaceInput;

    @ElementTitle("Регион")
    @FindBy(id = "region")
    private WebElement regionInput;

    @ElementTitle("Населенный пункт")
    @FindBy(id = "city")
    private WebElement cityInput;

    @ElementTitle("Улица")
    @FindBy(id = "street")
    private WebElement streetInput;

    @ElementTitle("Дом/владение")
    @FindBy(id = "house")
    private WebElement houseInput;

    @ElementTitle("Ok")
    @FindBy(xpath = "//a[contains(.,'Ok')]")
    private WebElement datePickerOkButton;

    @ElementTitle("Отмена")
    @FindBy(xpath = "//a[contains(.,'Отмена')]")
    private WebElement datePickerCancelButton;

    @ElementTitle("ОТПРАВИТЬ")
    @FindBy(xpath = "//button[contains(.,'Отправить')]")
    private WebElement sendButton;

    public MobileSpecifyPassportDataPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(birthDateInput));
    }

    @ActionTitle("заполняет паспортные данные с")
    public void fillsPassportDateWith(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String inputField, value, saveVariable, date = "", serial, number, whoIssued, unitCode, sex, placeOfBirth, house;
        String wrongAddressXpath = "//div[@class='message-error']/div[contains(.,'Неправильный адрес')]";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MMMM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map<String, String> aData : data) {
            inputField = aData.get(INPUT_FIELD);
            value = aData.get(VALUE);
            saveVariable = aData.get(SAVE_VALUE);

            if (inputField.contains(SERIAL)) {
                serial = (value.equals(RANDOM)) ? Generators.randomNumber(4) : value;
                fillField(serialAndNumberInput, serial);
                Stash.put(saveVariable, String.valueOf(serial));
                LOG.info(saveVariable + "<==[" + serial + "]");
            }
            if (inputField.contains(NUMBER)) {
                number = (value.equals(RANDOM)) ? Generators.randomNumber(6) : value;
                serialAndNumberInput.sendKeys(number);
                Stash.put(saveVariable, String.valueOf(number));
                LOG.info(saveVariable + "<==[" + number + "]");
            }

            if (inputField.contains(ISSUEDBY)) {
                whoIssued = (value.equals(RANDOM)) ? Generators.randomString(25) : value;
                fillField(issuedByInput, whoIssued);
                Stash.put(saveVariable, whoIssued);
                LOG.info(saveVariable + "<==[" + issuedByInput.getAttribute("value") + "]");

            }

            if (inputField.contains(UNITCODE)) {
                unitCode = (value.equals(RANDOM)) ? Generators.randomNumber(6) : value;
                fillField(unitCodeInput, unitCode);
                Stash.put(saveVariable, unitCodeInput.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + unitCodeInput.getAttribute("value") + "]");
            }

            if (inputField.contains(SEX)) {
                sex = (value.equals(RANDOM)) ? Generators.randomSex() : value;
                PageFactory.getWebDriver().findElement(By.xpath(("//*[text()='" + sex + "']"))).click();
                Stash.put(saveVariable, sex);
                LOG.info(saveVariable + "<==[" + sex + "]");
            }

            if (inputField.contains(PLACEOFBIRTH)) {
                placeOfBirth = (value.equals(RANDOM)) ? Generators.randomCity() : value;
                fillField(birthPlaceInput, placeOfBirth);
                Stash.put(saveVariable, placeOfBirth);
                LOG.info(saveVariable + "<==[" + birthPlaceInput.getAttribute("value") + "]");
            }

            if (inputField.contains(REGION) && value.equals(TRUE)) {
                fillAddressForMobile(regionInput, true);
                Stash.put(saveVariable, regionInput.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + regionInput.getAttribute("value") + "]");
            }

            if (inputField.contains(LOCALITY) && value.equals(TRUE)) {
                fillAddressForMobile(cityInput, true);
                Stash.put(saveVariable, cityInput.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + cityInput.getAttribute("value") + "]");
            }

            if (inputField.contains(STREET) && value.equals(TRUE)) {
                fillAddressForMobile(streetInput, true);
                Stash.put(saveVariable, streetInput.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + streetInput.getAttribute("value") + "]");
            }

            if (inputField.contains(HOUSE)) {
                house = (value.equals(RANDOM)) ? Generators.randomNumber(2) : value;
                fillField(houseInput, house);
                Stash.put(saveVariable, String.valueOf(house));
                LOG.info(saveVariable + "<==[" + house + "]");
            }
        }
    }

}
