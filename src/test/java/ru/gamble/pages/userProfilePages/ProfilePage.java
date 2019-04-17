package ru.gamble.pages.userProfilePages;

import cucumber.api.DataTable;
import cucumber.api.java.mn.Харин;
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
import ru.gamble.stepdefs.CommonStepDefs;
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
import java.util.stream.Collectors;

import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

/**
 * @author p.sivak.
 * @since 21.05.2018.
 */
@PageEntry(title = "Профиль")
public class ProfilePage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(ProfilePage.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//div[@class='user-profile__tab-wraper']")
    private WebElement tabWraper;

    @FindBy(xpath = "//i[contains(@class,'ico-eye')]")
    private WebElement PDeye;

    @ElementTitle("адрес почты")
    @FindBy(xpath = "//div[@class='user-profile__group']/div[4]//span[@class='user-profile__link']")
    protected WebElement email;

    @ElementTitle("изменить пароль")
    @FindBy(xpath = "//div[@class='user-profile__group']/div[4]//span[@class='user-profile__link']")
    protected WebElement changePassword;

    @ElementTitle("Старый пароль")
    @FindBy(xpath = "//input[@ng-model='password.oldPassword']")
    protected WebElement oldPassword;

    @ElementTitle("Новый пароль")
    @FindBy(xpath = "//input[@ng-model='password.newPassword']")
    protected WebElement newPassword;

    @ElementTitle("Сохранить пароль")
    @FindBy(xpath = "//button[@ng-click='updatePassword()']")
    protected WebElement savePassword;

    @ElementTitle("Изменить email")
    @FindBy(xpath = "//div[normalize-space(text())='Электронная почта']/following-sibling::div/span[contains(@class,'user-profile__link')]")
    protected WebElement editEmail;

    @ElementTitle("Изменить phone")
    @FindBy(xpath = "//div[normalize-space(text())='Телефон']/following-sibling::div/span[contains(@class,'user-profile__link')]")
    protected WebElement editPhone;



    public ProfilePage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        workWithPreloader();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(tabWraper));
    }


    @ActionTitle("меняет пароль в Личном кабинете")
    public void changePassword(String currentPass, String newPass) throws InterruptedException {
        CommonStepDefs.workWithPreloader();
        if (currentPass.matches("[A-Z]*")){
            currentPass = Stash.getValue(currentPass).toString();
        }
        if (newPass.matches("[A-Z]*")){
            newPass = Stash.getValue(newPass).toString();
        }

        changePassword.click();
        oldPassword.clear();
        oldPassword.sendKeys(currentPass);
        newPassword.clear();
        newPassword.sendKeys(newPass);
        LOG.info("Форма заполнена, жмём Сохранить");
        driver.findElement(By.className("user-profile__group-line")).click(); //кликаем в пустоту
        savePassword.click(); // Сохранить

        Thread.sleep(2000);

        LOG.info("Проверяем уведомление об успешной смене пароля");
        if (driver.findElement(By.cssSelector("div.modal__body.modal__body_up-pass")).isDisplayed()) {
            LOG.info("Уведомление присутствует, закрываем его");
        }
            else {
                LOG.warn("Уведомление о смене пароля не было отображено");
        }

        LOG.info("Текущий пароль - " + newPass);

//        driver.findElement(By.xpath("//div[@class='modal__body modal__body_up-pass']/span[@class='modal__closeBtn closeBtn']")).click(); //закрываем окошко поздравления
//        driver.findElement(By.cssSelector("span.user-profile__link")).click();
//        driver.findElement(By.xpath("//input[@type='password']")).clear();
//        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(newPass);
//        driver.findElement(By.name("profile_new_pass")).clear();
//        driver.findElement(By.name("profile_new_pass")).sendKeys(currentPass);
//        LOG.info("Форма заполнена, жмём Сохранить");
//        driver.findElement(By.className("user-profile__group-line")).click(); //кликаем в пустоту
//        driver.findElement(By.cssSelector("div.user-profile__text > button:nth-child(1)")).click(); // Сохранить
//
//        Thread.sleep(2000);
//
//        LOG.info("Проверяем уведомление об успешной смене пароля");
//        if (driver.findElement(By.cssSelector("div.modal__body.modal__body_up-pass")).isDisplayed()) {
//            LOG.info("Уведомление присутствует, закрываем его");
//        }
//        else {
//            LOG.warn("Уведомление о смене пароля не было отображено");
//        }
        driver.findElement(By.xpath("//div[@class='modal__body modal__body_up-pass']/span[@class='modal__closeBtn closeBtn']")).click();
        Thread.sleep(500);
    }


    @ActionTitle("сравнивает значения в ЛК с тем, с которыми пользователь регистрировался")
    public void checkPDinPrivite(DataTable dataTable){
        Map<String, String> table = dataTable.asMap(String.class, String.class);
        LOG.info("Раскрываем список Персональных данных если он закрыт");
        if (PDeye.getAttribute("class").contains("hide")){
            PDeye.findElement(By.xpath("./following-sibling::span")).click();
        }

        LOG.info("Формируем список тех данных, что отображаются в ЛК пользователя");
        SimpleDateFormat formatDateInLK = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formatDateInMemory = new SimpleDateFormat("yyyy-MM-dd");
        List<String> linesInLK = driver.findElements(By.xpath("//div[@class='user-profile__label']")).stream().map(el->el.getAttribute("innerText")).collect(Collectors.toList());
        String xpathForValueLine = "//div[@class='user-profile__label' and text()='%s']/following-sibling::*[contains(@class,'user-profile__text')]";
        for (String line:linesInLK){
            String key = table.get(line);
            if (key==null){
                continue;
            }
            String valueInMemory = Stash.getValue(key).toString().trim();
            String valueLine = driver.findElement(By.xpath(String.format(xpathForValueLine,line))).getAttribute("innerText").replaceAll("Изменить","").trim();

            if (line.contains("Дата")){
                valueLine = CommonStepDefs.newFormatDate(formatDateInLK,formatDateInMemory,valueLine);
            }

            if (line.contains("Телефон")){
                valueLine = valueLine.replaceAll("[-]*[ ]*[+]*","");
//                valueInMemory = valueInMemory + "Изменить";
            }

//            if (line.contains("почта")) {
////                valueInMemory = valueInMemory + " Изменить";
//            }

            Assert.assertTrue("Значение в ЛИЧНОМ КАБИНЕТЕ пользователя не совпадает с тем, с которым регистрировались. " +
                            line + " в ЛК: " + valueLine + ", а регистрирвоали: " + valueInMemory,
                    valueInMemory.toLowerCase().equals(valueLine.toLowerCase()));
            LOG.info(line + " совпадает с ожидаемым значением");
        }
    }

    @ActionTitle("подтверждает пароль")
    public void acceptPassword(String keyPassword){
        WebDriverWait wait = new WebDriverWait(driver,10);
        wait
                .withMessage("После нажатия на кнопку 'изменить' не появилось предложение подтвердить пароль")
                .until(ExpectedConditions.numberOfElementsToBe(By.id("auth_password"),1));
        String password = Stash.getValue(keyPassword);
        LOG.info("Заполняем поле 'пароль' значением " + password);
        driver.findElement(By.id("auth_password")).clear();
        driver.findElement(By.id("auth_password")).sendKeys(password);
        wait
                .withMessage("Поле 'пароль' заполнено, но кнопка 'ОК' не стала активной")
                .until(ExpectedConditions.not(ExpectedConditions.attributeContains(By.id("log-in-button"),"disabled","disabled")));
        LOG.info("Нажимаем на кнопку 'ОК'");
        driver.findElement(By.id("log-in-button")).click();
    }

    @ActionTitle("проверяет что поле содержит значение")
    public void checkValueInField(String field, String value){
        if (value.matches("[A-Z]*")){
            value = Stash.getValue(value).toString().replace("+","");
        }
        String actualvalue = driver.findElement(By.xpath("//*[@class='user-profile__label' and normalize-space(text())='" + field + "']/following-sibling::*[contains(@class,'user-profile__text')]")).getAttribute("innerText");
        Assert.assertTrue("Значение поля " + field + ":'" + actualvalue + "' не соответствует ожидаемому '" + value + "'",
                actualvalue.replace("Изменить","").replaceAll("[ +-]*","").trim().equals(value));

    }

    @ActionTitle("проверка чекбокса оферты в разделе 'Настройка уведомлений'")
    public void checkBoxOferta(){
        WebDriverWait wait = new WebDriverWait(driver,10);
        driver.findElement(By.xpath("//*[@href='/private/user/notifications']")).click();
        By by_email = By.id("not_email");
        By by_sms = By.id("not_sms");
        wait.withMessage("Не отмечен чекбокс емэйла").until(ExpectedConditions.attributeContains(by_email,"class","ng-valid-required"));
        wait.withMessage("Не отмечен чекбокс смс").until(ExpectedConditions.attributeContains(by_sms,"class","ng-valid-required"));

    }
}
