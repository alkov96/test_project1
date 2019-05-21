package ru.gamble.pages.registrationPages;

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

@PageEntry(title = "Личные данные")

public class PersonalDataPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(PassportDataPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//*[text()='Личные данные']")
    private WebElement pageTitle;

    @ElementTitle("Фамилия")
    @FindBy(id = "surName")
    private WebElement inputSurname;

    @ElementTitle("Имя")
    @FindBy(id = "firstName")
    private WebElement inputName;

    @ElementTitle("Отчество")
    @FindBy(id = "patronymic")
    private WebElement inputPatronymic;

    @ElementTitle("Сохранить")
    @FindBy(id = "next_step")
    private WebElement buttonContinue;



    public PersonalDataPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(fieldYear));
    }

    @ActionTitle("заполняет личные данные с")
    public void fillsPersonalData(DataTable dataTable) throws DataException {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String inputField, date = "", value, saveVariable;

        for (Map<String, String> aTable : table) {

            inputField = aTable.get(INPUT_FIELD);
            value = aTable.get(VALUE);
            saveVariable = aTable.get(SAVE_VALUE);

            if (value.matches("[A-Z]*")) {
                value = Stash.getValue(value);
            }

            if (inputField.contains(DATEOFBIRTH)) {
                date = Stash.getValue(value);
                enterDate(date);

                Stash.put(saveVariable, date);
                LOG.info(saveVariable + "<==[" + date + "]");
            }
            if (inputField.contains(LASTNAME)) {
                if (value.contains(RANDOM)) {
                    fillField(inputSurname, Generators.randomString(25));
                } else {
                    fillField(inputSurname, value);
                }
                Stash.put(saveVariable, inputSurname.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + inputSurname.getAttribute("value") + "]");
            }
            if (inputField.contains(NAME)) {
                if (value.contains(RANDOM)) {
                    fillField(inputName, Generators.randomString(25));
                } else {
                    fillField(inputName, value);
                }
                Stash.put(saveVariable, inputName.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + inputName.getAttribute("value") + "]");
            }
            if (inputField.contains(PATERNALNAME)) {
                if (value.contains(RANDOM)) {
                    fillField(inputPatronymic, Generators.randomString(25));
                } else {
                    fillField(inputPatronymic, value);
                }
                Stash.put(saveVariable, inputPatronymic.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + inputPatronymic.getAttribute("value") + "]");
            }
        }
    }
}

