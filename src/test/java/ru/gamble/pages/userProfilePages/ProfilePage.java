package ru.gamble.pages.userProfilePages;

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
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

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


    public ProfilePage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        workWithPreloader();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(tabWraper));
    }


    @ActionTitle("меняет пароль в Личном кабинете")
    public void changePassword() throws InterruptedException {
        String currentPass = "1qwert2ASDF";
        String newPass = "NewPassword123";
        WebDriver driver = PageFactory.getDriver();
        CommonStepDefs.workWithPreloader();
        driver.findElement(By.cssSelector("span.user-profile__link")).click();
        driver.findElement(By.xpath("//input[@type='password']")).clear();
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(currentPass);
        driver.findElement(By.name("profile_new_pass")).clear();
        driver.findElement(By.name("profile_new_pass")).sendKeys(newPass);
        LOG.info("Форма заполнена, жмём Сохранить");
        driver.findElement(By.className("user-profile__group-line")).click(); //кликаем в пустоту
        driver.findElement(By.cssSelector("div.user-profile__text > button:nth-child(1)")).click(); // Сохранить

        Thread.sleep(2000);

        LOG.info("Проверяем уведомление об успешной смене пароля");
        if (driver.findElement(By.cssSelector("div.modal__body.modal__body_up-pass")).isDisplayed()) {
            LOG.info("Уведомление присутствует, закрываем его");
        }
            else {
                LOG.warn("Уведомление о смене пароля не было отображено");
        }

        driver.findElement(By.xpath("//div[@class='modal__body modal__body_up-pass']/span[@class='modal__closeBtn closeBtn']")).click(); //закрываем окошко поздравления
        driver.findElement(By.cssSelector("span.user-profile__link")).click();
        driver.findElement(By.xpath("//input[@type='password']")).clear();
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(newPass);
        driver.findElement(By.name("profile_new_pass")).clear();
        driver.findElement(By.name("profile_new_pass")).sendKeys(currentPass);
        LOG.info("Форма заполнена, жмём Сохранить");
        driver.findElement(By.className("user-profile__group-line")).click(); //кликаем в пустоту
        driver.findElement(By.cssSelector("div.user-profile__text > button:nth-child(1)")).click(); // Сохранить

        Thread.sleep(2000);

        LOG.info("Проверяем уведомление об успешной смене пароля");
        if (driver.findElement(By.cssSelector("div.modal__body.modal__body_up-pass")).isDisplayed()) {
            LOG.info("Уведомление присутствует, закрываем его");
        }
        else {
            LOG.warn("Уведомление о смене пароля не было отображено");
        }
        driver.findElement(By.xpath("//div[@class='modal__body modal__body_up-pass']/span[@class='modal__closeBtn closeBtn']")).click();
        Thread.sleep(500);
    }
}
