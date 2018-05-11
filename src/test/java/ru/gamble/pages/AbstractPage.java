package ru.gamble.pages;

import cucumber.api.DataTable;
import cucumber.api.java.en.When;
import cucumber.api.java.ru.Когда;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.gamble.stepdefs.CommonStepDefs;
import ru.gamble.utility.BeforeTest;
import ru.gamble.utility.YandexPostman;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.RANDOM;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;


public abstract class AbstractPage extends Page {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPage.class);

    @ElementTitle("Бургер")
    @FindBy(id = "service-list")
    private WebElement burgerBottom;

    @ActionTitle("сохраняет с")
    public void saveKeyValue(DataTable dataTable){ CommonStepDefs.saveValueToKey(dataTable); }

    @ActionTitle("нажимает кнопку")
    public static void pressButton(String param){
        CommonStepDefs.pressButton(param);
        workWithPreloader();
    }

    @ActionTitle("stop")
    public static void stop(){
        LOG.info("STOP");
    }

    @ActionTitle("закрываем браузер")
    public static void closeBrowser(){
        PageFactory.getWebDriver().close();
    }

 /*   @ActionTitle("Очищает купон")
    public void crearCoupon(){
        if (PageFactory.getWebDriver().findElement(By.xpath("//span[@class='coupon-clear-all__text ng-binding']")).isDisplayed()){ //очистка купона
            PageFactory.getWebDriver().findElement(By.xpath("//span[@class='coupon-clear-all__text ng-binding']")).click();
        }
    }*/


    /**
     * Генератор
     *
     * @param len - максимальная длина строки.
     * @return возвращает получившуюся строку
     */
    public String randomNumber(int len) {

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            result.append(new Random().nextInt(10));
        }
        return result.toString();
    }

    /**
     * Генератор e-mail
     *
     * @param key - ключ по которому сохраняем е-mail в памяти.
     */
    @ActionTitle("генерит email в")
    public void generateEmailAndSave(String key){
        String value = "testregistrator+" + System.currentTimeMillis() + "@yandex.ru";
        LOG.info("Сохраняем в память (ключ):" + key + ":(значение)::" + value);
        Stash.put(key,value);
    }

    /**
     * Метод получения письма и перехода по ссылке для завершения регистрации на сайте
     *
     * @param key - ключ по которому получаем е-mail из памяти.
     */
    @ActionTitle("завершает регистрацию перейдя по ссылке в")
    public void endRegistrationByEmailLink(String key){
        WebDriver driver = PageFactory.getWebDriver();
        String email = Stash.getValue(key);
        String link = "";
        String url = "";

        try {
            url = BeforeTest.getData().get("site1").get("mainurl").getValue();
            link = YandexPostman.getLinkForAuthentication(email);
        }catch (DataException de){
            LOG.error("Ошибка! Не смогли получить ссылку сайта");
        } catch (Exception e) {
            LOG.error("Ошибка! Не смогли получить ссылку для аутентификации.");
            e.printStackTrace();
        }

        LOG.info("Переходим по ссылке из e-mail");

        driver.get(url + "?action=verify&" + link);

        new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.modal__closeBtn.closeBtn")));

        LOG.info("Закрываем уведомление об успешном подтверждении почты");
        driver.findElement(By.cssSelector("a.modal__closeBtn.closeBtn")).click();
    }

    @ActionTitle("убирает события из купона, пока их не станет")
    public void removeEventsFromCoupon(String param){
        int count = Integer.parseInt(param);
        while (PageFactory.getWebDriver().findElements(By.xpath("//ul[@class='coupon-bet-list ng-scope']")).size()>count){
            PageFactory.getWebDriver().findElement(By.xpath("//span[@ng-click='removeBet(bet)']")).click();
        }
    }

    public void fillCouponFinal(int count, String ifForExperss, By findCoeffs) {
        if (ifForExperss == "correct") {
            List<WebElement> eventsInCoupon;
            List<WebElement> correctMarkets;
            waitForElementPresent(findCoeffs,1000);
            do {
                correctMarkets = getWebDriver().findElements(findCoeffs)
                        .stream().filter(e -> e.isDisplayed() && !e.getText().contains("-") && Double.parseDouble(e.getText()) >= 1.26)
                        .limit(count+20).collect(Collectors.toList());
            }  while (correctMarkets.size() < count);
            for (WebElement coefficient : correctMarkets) {
                clickElement(coefficient);
                eventsInCoupon = PageFactory.getWebDriver().findElements(By.xpath("//ul[@class='coupon-bet-list ng-scope']"));
                if (eventsInCoupon.size() == count) {
                        break;
                }
            }
        }
        if (ifForExperss == "incorrect") {
            List<WebElement> eventsInCoupon;
            List<WebElement> correctMarkets;
            waitForElementPresent(findCoeffs,1000);
            do {
                correctMarkets = getWebDriver().findElements(findCoeffs)
                        .stream().filter(e -> e.isDisplayed() && !e.getText().contains("-") && Double.parseDouble(e.getText()) < 1.25)
                        .limit(count+20).collect(Collectors.toList());
            }  while (correctMarkets.size() < count);
            for (WebElement coefficient : correctMarkets) {
                clickElement(coefficient);
                eventsInCoupon = PageFactory.getWebDriver().findElements(By.xpath("//ul[@class='coupon-bet-list ng-scope']"));
                if (eventsInCoupon.size() == count) {
                    break;
                }
            }
        }
        if (ifForExperss == "no") {

        }
    }

    @ActionTitle("очищает купон")
    public void clearCoupon(){
        WebElement clearAllBottom = PageFactory.getWebDriver().findElement(By.xpath("//span[@class='coupon-clear-all__text ng-binding']"));
        if (clearAllBottom.isDisplayed()){ //очистка купона
            PageFactory.getWebDriver().findElement(By.xpath("//span[@class='coupon-clear-all__text ng-binding']")).click();
        }
    }

    public static void clickElement ( final WebElement element ) {
        WebElement myDynamicElement = ( new WebDriverWait(PageFactory.getWebDriver(), 10))
                .until( ExpectedConditions.elementToBeClickable( element ) );
        myDynamicElement.click();
    }

    public void waitForElementPresent(final By by, int timeout){
        WebDriverWait wait = (WebDriverWait)new WebDriverWait(PageFactory.getWebDriver(),timeout)
                .ignoring(StaleElementReferenceException.class);
        wait.until(new ExpectedCondition<Boolean>(){
            @Override
            public Boolean apply(WebDriver webDriver) {
                WebElement element = webDriver.findElement(by);
                return element != null && element.isDisplayed();
            }
        });
    }
}

