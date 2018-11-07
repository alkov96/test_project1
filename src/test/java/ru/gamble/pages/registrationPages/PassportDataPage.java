package ru.gamble.pages.registrationPages;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.gamble.utility.Constants.*;

@PageEntry(title = "Паспортные данные")
public class PassportDataPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(PassportDataPage.class);

    @FindBy(xpath = "//*[text()='Паспортные данные']")
    private WebElement pageTitle;

    @ElementTitle("Серия")
    @FindBy(id = "passpserial")
    private WebElement passpSerialInput;

    @ElementTitle("Номер")
    @FindBy(id = "passpserialnum")
    private WebElement passpNumberInput;

    @ElementTitle("Кем выдан")
    @FindBy(id = "passport_issuer")
    private WebElement issuedByInput;

    @ElementTitle("Код подразделения")
    @FindBy(id = "passport_issuer_code")
    private WebElement unitCodeInput;


    @ElementTitle("Место рождения")
    @FindBy(id = "birth_place")
    private WebElement placeOfBirthInput;

    @ElementTitle("Регион")
    @FindBy(id = "region")
    private WebElement regionInput;

    @ElementTitle("Нас. пункт")
    @FindBy(xpath = "//td[contains(.,'Нас. пункт')]//following-sibling::td//input[1]")
    private WebElement cityInput;

    @ElementTitle("Улица")
    @FindBy(id = "street")
    private WebElement streetInput;

    @ElementTitle("Дом/владение")
    @FindBy(id = "house")
    private WebElement houseInput;

    @ElementTitle("Строение")
    @FindBy(id = "building")
    private WebElement buildingInput;

    @ElementTitle("Корпус")
    @FindBy(id = "housing")
    private WebElement housingInput;

    @ElementTitle("Блок")
    @FindBy(id = "housing")
    private WebElement blockInput;

    @ElementTitle("Квартира")
    @FindBy(id = "apartment")
    private WebElement flatInput;

    @ElementTitle("Отправить")
    @FindBy(id = "next_step")
    private WebElement sendButton;


    public PassportDataPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(fieldYear));
    }


    @ActionTitle("заполняет паспорт с")
    public void fillsPassportDate(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String inputField, value, saveVariable, date = "", serial, number, whoIssued, unitCode, sex, placeOfBirth, house, building, block, flat;
        String wrongAddressXpath = "//div[@class='message-error']/div[contains(.,'Неправильный адрес')]";

        for (Map<String, String> aData : data) {
            inputField = aData.get(INPUT_FIELD);
            value = aData.get(VALUE);
            saveVariable = aData.get(SAVE_VALUE);

            if (inputField.contains(DATEISSUE)) {
                date = Stash.getValue(value);
                enterDate(date);
                Stash.put(saveVariable, date);
                LOG.info(saveVariable + "<==[" + date + "]");
            }
            if (inputField.contains(SERIAL)) {
                serial = (value.equals(RANDOM)) ? Generators.randomNumber(4) : value;
                fillField(passpSerialInput, serial);
                Stash.put(saveVariable, String.valueOf(serial));
                LOG.info(saveVariable + "<==[" + serial + "]");
            }
            if (inputField.contains(NUMBER)) {
                number = (value.equals(RANDOM)) ? Generators.randomNumber(6) : value;
                fillField(passpNumberInput, number);
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
                fillField(placeOfBirthInput, placeOfBirth);
                Stash.put(saveVariable, placeOfBirth);
                LOG.info(saveVariable + "<==[" + placeOfBirthInput.getAttribute("value") + "]");
            }


            if (inputField.contains(REGION) && value.equals(TRUE)) {
                fillAddress(regionInput, true);
                Stash.put(saveVariable, regionInput.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + regionInput.getAttribute("value") + "]");
            }

            if (inputField.contains(LOCALITY) && value.equals(TRUE)) {
                fillAddress(cityInput, true);
                Stash.put(saveVariable, cityInput.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + cityInput.getAttribute("value") + "]");
            }

            if (inputField.contains(STREET) && value.equals(TRUE)) {
                fillAddress(streetInput, true);
                Stash.put(saveVariable, streetInput.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + streetInput.getAttribute("value") + "]");
            }

            if (inputField.contains(HOUSE)) {
                house = (value.equals(RANDOM)) ? Generators.randomNumber(2) : value;
                fillField(houseInput, house);
                Stash.put(saveVariable, String.valueOf(house));
                LOG.info(saveVariable + "<==[" + house + "]");
            }

            if (inputField.contains(FLAT)) {
                flat = (value.equals(RANDOM)) ? Generators.randomNumber(2) : value;
                fillField(flatInput, flat);
                Stash.put(saveVariable, String.valueOf(flat));
                LOG.info(saveVariable + "<==[" + flat + "]");
            }
        }
        try {
            LOG.info("Ждём 2 сек. из-за TEST_INT");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info("Нажимаем кнопку 'Отправить'");
            sendButton.click();
    }

}
