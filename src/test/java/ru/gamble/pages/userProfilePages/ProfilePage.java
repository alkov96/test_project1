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



    public ProfilePage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        workWithPreloader();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(tabWraper));
    }


    @ActionTitle("меняет пароль в Личном кабинете")
    public void changePassword(String currentPass, String newPass) throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        CommonStepDefs.workWithPreloader();
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
        WebDriver driver = PageFactory.getDriver();
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
}
