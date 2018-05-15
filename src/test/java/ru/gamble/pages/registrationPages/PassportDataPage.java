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
import ru.gamble.utility.Generators;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.Map;

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
    @FindBy(id = "city")
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
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 30).until(ExpectedConditions.visibilityOf(pageTitle));
    }


    @ActionTitle("заполняет паспорт с")
    public void fillsPassportDate(DataTable dataTable){
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String serial, number, whoIssued, unitCode, sex, placeOfBirth, house, building, block, flat;

        serial = (data.get(SERIAL).equals(RANDOM))? Generators.randomNumber(4): data.get(SERIAL);
        LOG.info("Заполняем серию паспорта::" + serial);
        fillField(passpSerialInput,serial);

        number = (data.get(NUMBER).equals(RANDOM))? Generators.randomNumber(6): data.get(NUMBER);
        LOG.info("Заполняем номер паспорта::" + number);
        fillField(passpNumberInput,number);

        enterDate(data.get(DATEISSUE));

        whoIssued = (data.get(ISSUEDBY).equals(RANDOM))? Generators.randomString(25) : data.get(ISSUEDBY);
        LOG.info("Заполняем кем выдан::" + whoIssued);
        fillField(issuedByInput,whoIssued);

        unitCode = (data.get(UNITCODE).equals(RANDOM))? Generators.randomNumber(6) : data.get(ISSUEDBY);
        LOG.info("Заполняем код подразделения::" + unitCode);
        fillField(unitCodeInput,unitCode);

        sex = (data.get(SEX).equals(RANDOM))? Generators.randomSex() : data.get(SEX);
        LOG.info("Выбираем пол::" + sex);
        PageFactory.getWebDriver().findElement(By.xpath(("//*[text()='" + sex + "']"))).click();

        placeOfBirth = (data.get(PLACEOFBIRTH).equals(RANDOM))? Generators.randomCity() : data.get(PLACEOFBIRTH);
        LOG.info("Заполняем место рождения::" + placeOfBirth);
        fillField(placeOfBirthInput,placeOfBirth);

        if(data.get(REGION).equals(TRUE)){
            LOG.info("Регион");
            fillAddress(regionInput, true);
        }

        if(data.get(LOCALITY).equals(TRUE)){
            LOG.info("Населённый пункт");
            fillAddress(cityInput, true);
        }

        if(data.get(STREET).equals(TRUE)){
            LOG.info("Улица");
            fillAddress(streetInput, true);
        }

        house = (data.get(HOUSE).equals(RANDOM))? Generators.randomNumber(3) : data.get(HOUSE);
        LOG.info("Заполняем Дом/владение::" + house);
        fillField(houseInput,house);

        building = (data.get(BUILDING).equals(RANDOM))? Generators.randomNumber(2) : data.get(BUILDING);
        LOG.info("Заполняем Строение::" + building);
        fillField(buildingInput,building);

        block = (data.get(BLOCK).equals(RANDOM))? Generators.randomNumber(3) : data.get(BLOCK);
        LOG.info("Заполняем Корпус::" + block);
        fillField(blockInput,block);

        flat = (data.get(FLAT).equals(RANDOM))? Generators.randomNumber(3) : data.get(FLAT);
        LOG.info("Заполняем Квартира::" + flat);
        fillField(flatInput,flat);
    }

}
