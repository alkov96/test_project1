package ru.gamble.pages.userProfilePages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.Random;
import java.util.Set;

import static ru.gamble.utility.Constants.PASSWORD;
import static ru.gamble.utility.Constants.RANDOM;

@PageEntry(title = "Учетная запись")
public class UserAccountPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountPage.class);

    @FindBy(xpath = "//*[text()='Учетная запись']")
    private WebElement pageTitle;

    @ElementTitle("День")
    @FindBy(className = "inpD")
    protected WebElement fieldDay;

    @ElementTitle("Месяц")
    @FindBy(className = "inpM")
    protected WebElement fieldMonth;

    @ElementTitle("Год")
    @FindBy(className = "inpY")
    protected WebElement fieldYear;

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


    /**
     * Метод ввода даты рождения
     *
     * @param date  - строка либо с датой формата 'DD.MM.YYYY', либо 'random'
     *              случайной генерации даты
     */
    @ActionTitle("вводит дату рождения")
    public void inputBerthdayDate(String date){

        if(date.contains(RANDOM)){
            LOG.info("Вводим случайную дату рождения");
            selectMenu(fieldDay);
            selectMenu(fieldMonth);
            selectMenu(fieldYear);
        }else{
            String[] tmp = date.split(".");
            LOG.info("Вводим дату рождения");
            selectMenu(fieldDay,Integer.parseInt(tmp[0]));
            selectMenu(fieldMonth,Integer.parseInt(tmp[1]));
            selectMenu(fieldYear,Integer.parseInt(tmp[2]));
        }
    }


    /**
     * Открывает выпадающий список и выбирает оттуда пункт случайным образом
     *
     * @param element  - поле где ждем выпадающий список
     * @param max - максимальный пункт, который можно выбрать (включая этот max). Второго параметра может и не быть. тогда максимум - это длина всего списка
     * @return Возвращает номер пункта который был выбран
     */
    private int selectMenu(WebElement element, int max) {
        WebDriver driver = PageFactory.getWebDriver();
        int menuSize = element.findElements(By.xpath("custom-select/div[2]/div")).size();
        menuSize -= (max + 1);
        int select = 1 + (int) (Math.random() * menuSize);
        element.findElement(By.xpath("custom-select")).click();
        element.findElement(By.xpath("custom-select/div[2]/div[" + select + "]")).click();
        return select;
    }

    private int selectMenu(WebElement element) {
        return selectMenu(element, 0);
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
            fillField(inputSurname,randomString(25).toString());
            fillField(inputName,randomString(25).toString());
            fillField(inputPatronymic,randomString(25).toString());
        }else{
            String[] tmp = fio.split("\\s");
            LOG.info("Вводим ФИО");
            fillField(inputSurname,tmp[0]);
            fillField(inputName,tmp[1]);
            fillField(inputPatronymic,tmp[2]);
        }
    }

    /**
     * Случайная строка русских символов (и большие и маленькие буквы сразу)
     *
     * @param len - максимальная длина строки. но может быть и меньше
     * @return возвращает получившуюся строку
     */
    private StringBuilder randomString(int len) {

        StringBuilder result = new StringBuilder();
        int count = (int) (1 + Math.random() * len);
        for (int i = 0; i <= count; i++) {
            result.append((char) ('А' + new Random().nextInt(64)));
        }
        return result;
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
    @ActionTitle("вводит номер тел")
    public void enterSellphone(String value){
        WebDriver driver = PageFactory.getWebDriver();
        String phone;
        int count = 1;
        do {
            if(value.contains(RANDOM)) {
                LOG.info("Вводим случайный номер телефона");
                phone = "0" + randomNumber(10);
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

        //private final String password = "Parol123";
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
