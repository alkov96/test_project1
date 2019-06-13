package ru.gamble.pages.registrationPages;

import cucumber.api.DataTable;
import org.junit.Assert;
import org.openqa.selenium.By;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static ru.gamble.utility.Constants.*;

@PageEntry(title = "Учетная запись")
public class UserAccountPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//*[text()='Учетная запись']")
    private WebElement pageTitle;

    @ElementTitle("Имя")
    @FindBy(id = "first_name")
    private WebElement inputName;

    @ElementTitle("Эл.почта")
    @FindBy(id = "email")
    private WebElement inputEmail;

    @ElementTitle("Телефон")
    @FindBy(id = "phone")
    private WebElement cellFoneInput;

    @ElementTitle("Код")
    @FindBy(id = "phoneConfirmationCode")
    private WebElement cellFoneConformationInput;

    @ElementTitle("Пароль")
    @FindBy(id = "password")
    private WebElement passwordInput;

    @ElementTitle("Подтверждение пароля")
    @FindBy(id = "passwordConfirm")
    private WebElement confirmPasswordInput;

    @ElementTitle("Чекбокс оферты")
    @FindBy(className = "checkbox-style__imitate")
    private WebElement confirmOfertCheckbox;

    @ElementTitle("Отправить")
    @FindBy(xpath = "//*[@ng-disabled='regForm.$invalid']")
    private WebElement sendButton;

    @ElementTitle("Ок")
    @FindBy(xpath = "//div[@class='modal__btn-row']/button[text()='Ок']")
    private WebElement okButton;

    public UserAccountPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("заполняет форму с")
    public void fillsForm(DataTable dataTable) throws DataException {
        List<Map<String,String>> table = dataTable.asMaps(String.class,String.class);
        String inputField, date = "", value, saveVariable;

        for (Map<String, String> aTable : table) {

            inputField = aTable.get(INPUT_FIELD);
            value = aTable.get(VALUE);
            saveVariable = aTable.get(SAVE_VALUE);

            if (value.matches("[_A-Z]*")){
                value=Stash.getValue(value);
            }
            if (inputField.contains(DATEOFBIRTH)) {
                date = value;
                enterDate(date,DATEOFBIRTH);
                Stash.put(saveVariable, date);
                LOG.info(saveVariable + "<==[" + date + "]");
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

            if (inputField.contains(EMAIL)) {
                String email = Stash.getValue(("EMAIL"));
                fillField(inputEmail, email);
                LOG.info(saveVariable + "<==[" + inputEmail.getAttribute("value") + "]");
            }
            if (inputField.contains(PASSWORD)) {
                String password = (value.equals(Constants.DEFAULT)) ? JsonLoader.getData().get(STARTING_URL).get("PASSWORD").getValue() : value;
                LOG.info("Вводим пароль::" + password);
                fillField(passwordInput, password);
                Stash.put(saveVariable, password);
            }

            if (inputField.contains(NUMBERPHONE)) {
                super.enterSellphone(value, cellFoneInput, cellFoneConformationInput);
                StringBuilder builder = new StringBuilder();
                builder.append("7").append(cellFoneInput.getAttribute("value").trim().replaceAll("\n", "").replaceAll("-", "").replaceAll(" ", ""));
                Stash.put(saveVariable, String.valueOf(builder.toString()));
                LOG.info(saveVariable + "<==[" + builder.toString() + "]");
            }
        }
    }










    @ActionTitle("заполняет форму TESTINT с")
    public void fillsFormTestInt(DataTable dataTable) throws DataException {
        List<Map<String,String>> table = dataTable.asMaps(String.class,String.class);
        String inputField, value, saveVariable, date = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MMMM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map<String, String> aTable : table) {

            inputField = aTable.get(INPUT_FIELD);
            value = aTable.get(VALUE);
            saveVariable = aTable.get(SAVE_VALUE);

            if (value.matches("[_A-Z]*")){
                value=Stash.getValue(value);
            }

            if (inputField.contains(DATEOFBIRTH)) {
                try {
                    date = outputFormat.format(inputFormat.parse(enterDate(value,DATEOFBIRTH)));
                } catch (ParseException e) {
                    e.getMessage();
                }
                Stash.put(saveVariable, date);
                LOG.info(saveVariable + "<==[" + date + "]");
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
            if (inputField.contains(EMAIL)) {
                String email = Stash.getValue(("EMAIL"));
                fillField(inputEmail, email);
                LOG.info(saveVariable + "<==[" + inputEmail.getAttribute("value") + "]");
            }
            if (inputField.contains(PASSWORD)) {
                String password = (value.equals(Constants.DEFAULT)) ? JsonLoader.getData().get(STARTING_URL).get("PASSWORD").getValue() : value;
                LOG.info("Вводим пароль::" + password);
                fillField(passwordInput, password);
//                LOG.info("Подтверждаем::" + password);
//                fillField(confirmPasswordInput, password);
                Stash.put(saveVariable, password);
            }

            if (inputField.contains(NUMBERPHONE)) {
                super.enterSellphoneForOrtax(value, cellFoneInput, cellFoneConformationInput);
                StringBuilder builder = new StringBuilder();
                builder.append("7").append(cellFoneInput.getAttribute("value").trim().replaceAll("\n", "").replaceAll("-", "").replaceAll(" ", ""));
                Stash.put(saveVariable, String.valueOf(builder.toString()));
                LOG.info(saveVariable + "<==[" + builder.toString() + "]");
            }
        }
    }



    /**
     * Метод ввода поле email
     *
     * @param key вводимое значение
     */
    @ActionTitle("вводит email")
    public void enterEmail(String key){
                LOG.info("Вводим e-mail");
                fillField(inputEmail, Stash.getValue(key));
    }


    /**
     * Метод ввода в поля пароля и подтверждения
     *
     * @param text вводимое значение
     */
    @ActionTitle("вводит пароль")
    public void enterPassword(String text){
        String password;
        if(text.contains(PASSWORD)){
            password = Stash.getValue(PASSWORD);
        }else {
            password = text;
        }
        fillField(passwordInput, password);
        fillField(confirmPasswordInput, password);
    }

    @ActionTitle("проверяет, кто кнопка 'Отправить' не активна")
    public void sendDataWithoutOferta(){
        By by = By.xpath("//*[@ng-disabled='regForm.$invalid']");
        new WebDriverWait(driver, 10).withMessage("Несмотря на то, что признак оферты не был отмечен, мы смогли продолжить регистрацию.").until(ExpectedConditions.attributeContains(by, "disabled","true"));

    }
}
