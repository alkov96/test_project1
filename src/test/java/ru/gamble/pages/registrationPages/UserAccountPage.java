package ru.gamble.pages.registrationPages;

import cucumber.api.DataTable;
import org.openqa.selenium.*;
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
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.gamble.utility.Constants.*;

@PageEntry(title = "Учетная запись")
public class UserAccountPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountPage.class);

    @FindBy(xpath = "//*[text()='Учетная запись']")
    private WebElement pageTitle;

    @ElementTitle("Фамилия")
    @FindBy(id = "surname")
    private WebElement inputSurname;

    @ElementTitle("Имя")
    @FindBy(id = "first_name")
    private WebElement inputName;

    @ElementTitle("Отчество")
    @FindBy(id = "patronymic")
    private WebElement inputPatronymic;

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
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(fieldYear));
    }

    @ActionTitle("заполняет форму с")
    public void fillsForm(DataTable dataTable) throws DataException {
        List<Map<String,String>> table = dataTable.asMaps(String.class,String.class);
        String inputField, value, saveVariable, date = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MMMM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        for(int i = 0; i < table.size(); i++) {

            inputField = table.get(i).get(INPUT_FIELD);
            value = table.get(i).get(VALUE);
            saveVariable = table.get(i).get(SAVE_VALUE);

            if(inputField.contains(DATEOFBIRTH)){
                try {
                    date = outputFormat.format(inputFormat.parse(enterDate(value)));
                } catch (ParseException e) {
                    e.getMessage();
                }
                Stash.put(saveVariable,date);
                LOG.info(saveVariable + "<==[" + date + "]");
            }
            if(inputField.contains(LASTNAME)) {
                if(value.contains(RANDOM)){
                fillField(inputSurname,Generators.randomString(25));}
                else {fillField(inputSurname,value); }
                Stash.put(saveVariable,inputSurname.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + inputSurname.getAttribute("value") + "]");
            }
            if(inputField.contains(NAME)) {
                if (value.contains(RANDOM)) {
                    fillField(inputName, Generators.randomString(25));
                } else { fillField(inputName, value); }
                Stash.put(saveVariable, inputName.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + inputName.getAttribute("value") + "]");
            }
            if (inputField.contains(PATERNALNAME)) {
                if (value.contains(RANDOM)) {
                    fillField(inputPatronymic, Generators.randomString(25));
                } else { fillField(inputPatronymic, value); }
                Stash.put(saveVariable, inputPatronymic.getAttribute("value"));
                LOG.info(saveVariable + "<==[" + inputPatronymic.getAttribute("value") + "]");
            }
            if(inputField.contains(EMAIL)){
                String email = Stash.getValue(("EMAIL"));
                fillField(inputEmail, email);
                LOG.info(saveVariable + "<==[" + inputEmail.getAttribute("value") + "]");
            }
            if(inputField.contains(PASSWORD)){
                String password = (value.equals(Constants.DEFAULT)) ? JsonLoader.getData().get(STARTING_URL).get("PASSWORD").getValue() : value;
                LOG.info("Вводим пароль::" + password);
                fillField(passwordInput, password);
                LOG.info("Подтверждаем::" + password);
                fillField(confirmPasswordInput, password);
                Stash.put(saveVariable, password);
            }

            if(inputField.contains(NUMBERPHONE)) {
                enterSellphone(value);
                StringBuilder builder = new StringBuilder();
                builder.append("7").append(cellFoneInput.getAttribute("value").trim().replaceAll("\n","").replaceAll("-","").replaceAll(" ",""));
                Stash.put(saveVariable, String.valueOf(builder.toString()));
                LOG.info(saveVariable + "<==[" +  builder.toString() + "]");
            }
        }
    }

    /**
     * Метод ввода ФИО
     *
     * @param fio  - строка либо с ФИО формата 'Ф И О', либо 'random'
     *              случайной генерации даты
     */
    @ActionTitle("вводит фио")
    public void inputFIO(String fio){

        if(fio.contains(RANDOM)){
            LOG.info("Вводим случайные ФИО");
            fillField(inputSurname,Generators.randomString(25));
            fillField(inputName,Generators.randomString(25));
            fillField(inputPatronymic,Generators.randomString(25));
        }else{
            String[] tmp = fio.split("\\s");
            LOG.info("Вводим ФИО");
            fillField(inputSurname,tmp[0]);
            fillField(inputName,tmp[1]);
            fillField(inputPatronymic,tmp[2]);
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
     * Метод ввода поле номера телефона
     *
     * @param value вводимое значение
     */
    private void enterSellphone(String value){
        WebDriver driver = PageFactory.getWebDriver();
        String phone;
        int count = 1;
        do {
            if(value.contains(RANDOM)) {
                phone = "0" + Generators.randomNumber(9);
                LOG.info("Вводим случайный номер телефона::+7[" + phone + "]");
                fillField(cellFoneInput,phone);
            } else {
                phone = (value.matches("^[A-Z_]+$")) ? Stash.getValue(value) : value;
                LOG.info("Вводим номер телефона без первой 7-ки [" + phone.substring(1,11) + "]");
                fillField(cellFoneInput,phone.substring(1,11));
            }

            LOG.info("Попыток ввести номер::" + count);
            if (count > 5) {
                throw new AutotestError("Использовано 5 попыток ввода номера телефона");
            }
            ++count;

        } while (!driver.findElements(By.xpath("//div[contains(@class,'inpErrTextError')]")).isEmpty());


        LOG.info("Копируем смс-код для подтверждения телефона");
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(cellFoneConformationInput));

        String currentHandle = driver.getWindowHandle();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String registrationUrl = "";

        try {
            registrationUrl =  JsonLoader.getData().get(STARTING_URL).get("REGISTRATION_URL").getValue();
        } catch (DataException e) {
           LOG.error(e.getMessage());
        }

        js.executeScript("registration_window = window.open('" + registrationUrl + "')");

        Set<String> windows = driver.getWindowHandles();
        windows.remove(currentHandle);
        String newWindow = windows.toArray()[0].toString();

        driver.switchTo().window(newWindow);

        String xpath = "//li/a[contains(.,'" + phone + "')]";
        WebElement numberSring = null;
        int x = 0;

        LOG.info("Пытаемся найти код подтверждения телефона");
        for(int y = 0; y < 3; y++) {
            try {
                new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
                numberSring = driver.findElement(By.xpath(xpath));
            } catch (Exception e) {
                driver.navigate().refresh();
            }
            x++;
            if (numberSring != null){break;}
        }

        if(numberSring != null && !numberSring.getText().isEmpty()) {
            String code = numberSring.getText().split(" - ")[1];
            driver.switchTo().window(currentHandle);
            js.executeScript("registration_window.close()");

            LOG.info("Вводим SMS-код::" + code);
            fillField(cellFoneConformationInput,code);
        }else {
            throw new AutotestError("Ошибка! SMS-код не найден.[" + x + "] раз обновили страницу [" + driver.getCurrentUrl() + "] не найдя номер[" +  phone + "]");
        }
        Stash.put("PHONE_NUMBER",phone);
        LOG.info("Сохранили номер телефона в память::" + phone + "[PHONE_NUMBER]");

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
}
