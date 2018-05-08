package ru.gamble.pages;

import cucumber.api.DataTable;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.utility.Generators;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.Map;
import java.util.Random;
import java.util.Set;

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
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("заполняет форму с")
    public void fillsForm(DataTable dataTable){
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        enterDate(data.get(DATEOFBIRTH));

        if(data.get(FIO).contains(RANDOM)){
            LOG.info("Вводим случайные ФИО");
            fillField(inputSurname,Generators.randomString(25));
            fillField(inputName,Generators.randomString(25));
            fillField(inputPatronymic,Generators.randomString(25));
        }else{
            String[] tmp = data.get(FIO).split("\\s");
            LOG.info("Вводим ФИО");
            fillField(inputSurname,tmp[0]);
            fillField(inputName,tmp[1]);
            fillField(inputPatronymic,tmp[2]);
        }

        LOG.info("Вводим e-mail");
        fillField(inputEmail, Stash.getValue(data.get(EMAIL)));
        LOG.info("Вводим пароль");
        String password = data.get(PASSWORD);
        fillField(passwordInput, password);
        LOG.info("Вводим подтверждения пароля");
        fillField(confirmPasswordInput, password);

        enterSellphone(data.get(NUMBERPHONE));
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
                LOG.info("Вводим случайный номер телефона");
                phone = "0" + Generators.randomNumber(10);
                fillField(cellFoneInput,phone);
            }else {
                LOG.info("Вводим номер телефона");
                fillField(cellFoneInput,value);
            }

            LOG.info("Attempt : " + count);
            if (count > 5) {
                throw new AutotestError("Использовано 5 попыток ввода номера телефона");
            }
            ++count;

        } while (!driver.findElements(By.xpath("//div[contains(@class,'inpErrTextError')]")).isEmpty());

        LOG.info("Копируем смс-код для подтверждения телефона");

//        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "t");
//        driver.get("http://stage-bet-site1.tsed.orglot.office/test_registration/");
//        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "w");

        String currentHandle = driver.getWindowHandle();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("registration_window = window.open('http://stage-bet-site1.tsed.orglot.office/test_registration/')");

        Set<String> windows = driver.getWindowHandles();
        windows.remove(currentHandle);
        String newWindow = windows.toArray()[0].toString();
        driver.switchTo().window(newWindow);

        String code = driver.findElement(By.xpath("//li[1]")).getText().split(" - ")[1];

        driver.switchTo().window(currentHandle);
        js.executeScript("registration_window.close()");

        LOG.info("Вводим полученный SMS-код : " + code);

        fillField(cellFoneConformationInput,code);
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
