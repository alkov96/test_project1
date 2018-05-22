package ru.gamble.pages;

import org.apache.commons.logging.Log;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.livePages.DayEventsPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openqa.selenium.By.xpath;
import static ru.gamble.stepdefs.CommonStepDefs.*;

/**
 * @author p.sivak.
 * @since 11.05.2018.
 */
@PageEntry(title = "Купон")
public class CouponPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(CouponPage.class);

    @FindBy(xpath = "//div[@class='list-bet-block-top__title']")
    private WebElement coupon;

    @ElementTitle("Очистить всё")
    @FindBy(xpath = "//span[@class='coupon-clear-all__text ng-binding']")
    private WebElement clearCoupon;

    @ElementTitle("экспресс-бонус ссылка")
    @FindBy(xpath = "//div[@class='coupon-bonus-info coupon-bonus-info-link']")
    private WebElement expressBonusLink;

    @ElementTitle("текст экспресс бонуса")
    @FindBy(xpath = "//div[@class='coupon-bonus-info']")
    private WebElement expressBonusText;

    @ElementTitle("бонус к возможному выйгрышу")
    @FindBy(xpath = "//p[@class='betting-result-info__total-bonus']")
    private WebElement bonus;

    @ElementTitle("тип ставки")
    @FindBy(xpath = "//span[@class='bs-type-switcher__title-text ng-binding']")
    private WebElement betType;

    @ElementTitle("бонусы")
    @FindBy(xpath = "//div[@id='bonusmoney']")
    private WebElement bonusSwitcher;

    @ElementTitle("фрибет")
    @FindBy(xpath = "//div[@class='coupon-clear-all__inner']")
    private WebElement freebet;

    @ElementTitle("параметры в купоне")
    @FindBy(xpath = "//div[@class='list-bet-block-top']//div[@class='bs-type-switcher__wrapper']//i")
    private WebElement button;

    @ElementTitle("заключить пари")
    @FindBy(id="place-bet-button")
    private WebElement coupon_bet_button;

    @ElementTitle("поле суммы Экспресс-ставки")
    @FindBy(id="express-bet-input")
    private WebElement coupon_field;


    public CouponPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(coupon,10);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(coupon));
    }

    @ActionTitle("убирает события из купона, пока их не станет")
    public void removeEventsFromCoupon(String param) {
        int count = Integer.parseInt(param);
        while (PageFactory.getWebDriver().findElements(By.xpath("//ul[@class='coupon-bet-list ng-scope']")).size() > count) {
            PageFactory.getWebDriver().findElement(By.xpath("//span[@ng-click='removeBet(bet)']")).click();
        }
    }

    @ActionTitle("проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе")
    public void checkBonusFalse() {
        checkExpressBonus(false);
    }

    @ActionTitle("проверяет корректность ссылки О бонусах к экспрессу и текста о бонусе")
    public void checkBonusTrue() {
        checkExpressBonus(true);
    }

    @ActionTitle("заполняет сумму для ставки")
    public void fillSumm(String param1, String param2) {
        if (param1.equals("Экспресс")) {
            PageFactory.getWebDriver().findElement(By.xpath("//input[@id='express-bet-input']")).clear();
            PageFactory.getWebDriver().findElement(By.xpath("//input[@id='express-bet-input']")).sendKeys(param2);
        }
    }

    @ActionTitle("проверяет наличие бонуса к возможному выйгрышу")
    public void checkBonusPresent() {
        assertThat(true, equalTo(checkBonus()));
    }

    @ActionTitle("проверяет отсутствие бонуса к возможному выйгрышу")
    public void checkBonusNotPresent() {
        assertThat(false, equalTo(checkBonus()));
    }

//    @ActionTitle("выбирает тип ставки")
//    public void checkBonusNotPresent(String type) {
//        if (type.equals("Ординар")){
//            PageFactory.getWebDriver().findElement(By.xpath("//li[contains(text(), '\n" +
//                    "                            Ординар\n" +
//                    "                        ')]")).click();
//        }
//        if (type.equals("Экспресс")){
//            PageFactory.getWebDriver().findElement(By.xpath("//li[contains(text(), '\n" +
//                    "                            Экспресс\n" +
//                    "                        ')]")).click();
//        }
//        if (type.equals("Система")){
//            PageFactory.getWebDriver().findElement(By.xpath("//li[contains(text(), '\n" +
//                    "                            система\n" +
//                    "                        ')]")).click();
//        }
//    }

    public boolean checkBonus() {
        try {
            bonus.isDisplayed();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void checkExpressBonus(boolean except) {
        if (!except) {
            assertThat(false, equalTo(expressBonusLink.isDisplayed()));
            assertThat(false, equalTo(expressBonusText.isDisplayed()));
        } else {
            assertThat("о бонусах к экспрессу", equalTo(expressBonusLink.findElement(By.xpath("a[@href='/rules/express-bonus']")).getText())); // проверка корректности ссылки
            assertThat(PageFactory.getWebDriver().findElements(By.xpath("//ul[contains(@class, 'coupon-bet-list')]")).size() + 1 + "% к выигрышу за еще одно событие\nс коэффициентом от 1.25", equalTo(expressBonusText.getText())); // проверка корректности текста
        }
    }



    @ActionTitle("проверяет, добавилось ли событие в купон")
    public void checkListOfCoupon() {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> сouponList = driver.findElements(By.xpath("//div[@class='coupon-bet-list__wrap']/ul"));
        if (сouponList.isEmpty()) {
            Assertions.fail("События не добавлиись в купон.");
        } else LOG.info("Событие " + сouponList.size());
    }

    @ActionTitle("проверяет, совпадают ли события в купоне с ожидаемыми")
    public void bannerAndTeams() {
        WebDriver driver = PageFactory.getDriver();
        String сouponGame = driver.findElement(By.xpath("//div[@class='coupon-bet-list__wrap']/ul[1]/li[1]/span[contains(@class,'bet-event-title')]")).getText();//cuponGame - наше добавленные события в купоне.
        String team1 = Stash.getValue("team1key");
        String team2 = Stash.getValue("team2key");
        if (CommonStepDefs.stringParse(team1 + team2).equals(CommonStepDefs.stringParse(сouponGame))) {
            LOG.info("Названия команд в купоне совпадают с ожидаемыми: " + team1 + team2 + "=" +сouponGame);
        } else Assertions.fail("Названия команд в купоне не совпадают с ожидаемыми: " + team1+team2 + сouponGame);
    }

    @ActionTitle("проверяет, совпадает ли исход в купоне с ожидаемым")
    public void checkIshod() {
        WebDriver driver = PageFactory.getDriver();
        String ishod = driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[@class='pick ng-binding']")).getText();
        String ishodName = Stash.getValue("ishodKey");//ожидаемое название исхода
        if (CommonStepDefs.stringParse(ishod).equals(CommonStepDefs.stringParse(ishodName))) {
            LOG.info("Выбранных исход в купоне совпадает с ожидаемым: " + ishod+ "-"+ ishodName);
        } else Assertions.fail("Выбранный исход в купоне не совпадает с ожидаемым: " + ishod +" - "+ ishodName);

    }

    @ActionTitle("сравнивает коэфиценты")
    public void compareCoef() {
        WebDriver driver = PageFactory.getDriver();
        float coef = Stash.getValue("coefKey");
        float coefCoupon = Float.valueOf(driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[contains(@class,'coupon-betprice')]")).getText());//Кэфицент в купоне
        String oldString = driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[contains(@class,'coupon-betprice_old')]")).getAttribute("class");//oldString - просто переменная, в которую сохраним класс, где лежит старый коэф.
        float coefOld;
        coefOld = oldString.contains("ng-hide") ? 0.0f : Float.valueOf(driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[contains(@class,'coupon-betprice_old')]")).getText());//Краткая запись цикла. ? - часть синтаксиса. Здесь показываем чему равен старый коэфицент. если скрыт, то 0.0.
        if (coef != coefCoupon && coef != coefOld) {
            Assertions.fail("Коэфицент в купоне не совпадает с коэфицентом в событии: " + coefCoupon + coef);
        } else LOG.info("Коэфицент в купоне совпадает с коэфицентом в событии: " + coefCoupon +" - " + coef);

    }
    @ActionTitle("устанавливает условие для принятия коэфицентов как 'Никогда'")
    public void neverAccept() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        button.click();
        driver.findElement(xpath("//label[@class='betslip-settings__option']")).click();
        LOG.info("Установили условие 'Никогда'");
        button.click();
    }

    @ActionTitle("проверяет, что после изменения условий на 'Никогда' в купоне появляется кнопка 'Принять' и информационное сообщение")
    public void buttonAndMessageIsDisplayed() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        List<WebElement> oldCoef =  driver.findElements(xpath("//li[@class='coupon-bet-list__item_result']/div[@class='coupon-bet-list__item-column']/span[@class='coupon-betprice_old ng-binding']"));
        Thread.sleep(500);
        if (oldCoef.size()>0 && !driver.findElement(xpath("//div[@class='bet-notification__error-text bet-notification__suggestion-wrapper']")).isDisplayed()) {
            Assertions.fail("Коэф изменился, однако сообщение не отображается.");
        }
        LOG.info("Изменился коэф и появилось сообщение о принятии коэфиценита");
        Thread.sleep(1000);
        if (!driver.findElement(xpath("//div[@class='bet-notification__error-text bet-notification__suggestion-wrapper']")).isDisplayed()
                || !driver.findElement(xpath("//div[@class='coupon-confirm__btn']")).isDisplayed()) {
            Assertions.fail("При изменении условий ставки не появилось сообщение или кнопка о принятии изменений.");
        }
        LOG.info("Появилось сообщение о принятии коэфицента и кнопка");

        LOG.info("Проверка на принятие условия 'Никогда' в купоне прошла успешно.");

    }

    /**
     * функция проверяет, как изменился коэф для конкретной ставки
     * @param param порядковый номер ставки в купоне
     * @return возращает 0, если коэф не изменился, >0, если увеличился, и <0, если уменьшился
     */
    public float compareCoef(int param) {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> allBets = driver.findElements(By.xpath("//ul[contains(@class,'coupon-bet-list')]/li[2]/div[2]"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        for (int i = 2; i >0; i--){
            List<WebElement> oldCoef =  driver.findElements(xpath("//li[@class='coupon-bet-list__item_result']/div[@class='coupon-bet-list__item-column']/span[@class='coupon-betprice_old ng-binding']"));
            if (!oldCoef.isEmpty()) break;
        }
        float coefCoupon = Float.valueOf(allBets.get(param).findElement(By.xpath("span[contains(@class,'coupon-betprice')]")).getText());
        String oldString = allBets.get(param).findElement(By.xpath("span[contains(@class,'coupon-betprice_old')]")).getAttribute("class");
        float coefOld;
        coefOld = oldString.contains("ng-hide") ? coefCoupon : Float.valueOf(allBets.get(param).findElement(By.xpath("span[contains(@class,'coupon-betprice_old')]")).getText());
        LOG.info("Старый коэф: " + coefOld);
        LOG.info("Текущий коэф: " + coefCoupon);
        float sum = coefCoupon - coefOld;
        return sum;
    }

    @ActionTitle("проверяет изменения коэфицентов в купоне при условии 'Повышенные коэфиценты', удаляет из купона все события, кроме событий, у которых повысился коэфицент")
    public void compareChangeCoef () throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        button.click();
        driver.findElement(xpath("//div[@class='betslip-settings ng-scope active']//label[2]")).click();
        button.click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<WebElement> allBets = driver.findElements(By.xpath("//ul[contains(@class,'coupon-bet-list')]/li[2]/div[2]"));
        for (int i = allBets.size()-1; i >=0; i--) {
            float s = compareCoef(i);
            LOG.info("для " + i + " события результат = " + s);
               if (s<=0){
                   driver.findElement(xpath("//ul[contains(@class,'coupon-bet-list') and position()=" + (i+1) + "]//i[contains(@class,'icon-cross-close')]")).click();
                    allBets = driver.findElements(By.xpath("//ul[contains(@class,'coupon-bet-list')]/li[2]/div[2]"));
               }
        }

        if (allBets.size()==0){
            LOG.info("Подходящей ставки не нашлось, поэтому заново добавляем события в купон, сравниваем коэфиценты и удаляем ненужные события");
            DayEventsPage.addEventsToCouponF();
            compareChangeCoef();
            //  compareCoef(i);
        }
        LOG.info("Количество событий в купоне: " + driver.findElements(By.xpath("//ul[contains(@class,'coupon-bet-list')]/li[2]/div[2]")).size());
        if (driver.findElement(xpath("//div[@class='bet-notification__error-text bet-notification__suggestion-wrapper']")).isDisplayed()
                || driver.findElement(xpath("//div[@class='coupon-confirm__btn']")).isDisplayed()) {
            Assertions.fail("Отображается кнопка и сообщение, хотя не должны - в купоне одни лишь события с повышенным коэфицентом");
        }
        LOG.info("Не появилась кнопка 'Принять', в купоне события с повышенным коэфицентом");

    }

    @ActionTitle("выбирает тип ставки")
    public void selectTypeBet(String type){
        WebDriver driver = PageFactory.getDriver();
        type = type.toLowerCase();
        if (driver.findElements(By.xpath("//div[@class='bs-type-switcher open']")).isEmpty()){//если переключатель типа ставки не открыт - открываем
        LOG.info("Жмём на переключатель типов ставок");
        driver.findElement(By.xpath("//div[contains(@class,'bs-type-switcher__title')]")).click();
        }
        WebElement selectType = driver.findElement(By.xpath("//li[contains(translate(text(),'ЯЧСМИТЬБЮФЫВАПРОЛДЖЭЙЦУКЕНГШЩЗХЪ', 'ячсмитьбюфывапролджэйцукенгшщзхъ'),'" + type + "')]"));
                //driver.findElement(By.xpath("//li[contains(@class,'open-type-switcher__item') and contains(lower-case(text()),'"+type+"')]"));
        LOG.info("Переключаем тип ставки на '" + selectType.getText() + "'");
        selectType.click();
    }

    @ActionTitle("вводит сумму ставки Экспресс")
    public void inputExpress(String sumBet){
        WebDriver driver = PageFactory.getDriver();
        coupon_field.sendKeys(String.valueOf(sumBet));
        LOG.info("Вводим сумму ставки : " + sumBet);
        float sum = Float.valueOf(sumBet.trim());
        sum = (float)Stash.getValue("balanceKey") - sum;
        Stash.put("balanceKey",sum);
    }

    @ActionTitle("заключает пари")
    public void doBet(){
        WebDriver driver = PageFactory.getDriver();
        LOG.info("Жмём Заключить пари");
        coupon_bet_button.click();
        CommonStepDefs.workWithPreloader();
        if (!driver.findElement(By.cssSelector("div.bet-accepted-noification")).isDisplayed()) {
            LOG.warn("Сообщение об успешной ставке не найдено");
        }
    }

    /**
     * проверка что баланс изменился успешно. По параметру отепределяется проверка рублей или бонусов
     * @param param если параметр = "бонусов", то проверка бонусов, иначе - првоерка рублевого баланса
     */
    @ActionTitle("проверяет изменение баланса")
    public void balanceIsOK(String param){
        WebDriver driver = PageFactory.getDriver();
        float afterBalance;
        By balance=param.equals("бонусов")?By.id("bonus-balance"):By.id("topPanelWalletBalance");//определяем баланс рублей или бонусов будм првоерть
        int count = 30;
        while (count >0){
            afterBalance = Float.valueOf(driver.findElement(balance).getText());
            afterBalance = new BigDecimal(afterBalance).setScale(2, RoundingMode.UP).floatValue();
            float balanceExpected = Stash.getValue("balanceKey");
            balanceExpected = new BigDecimal(balanceExpected).setScale(2, RoundingMode.UP).floatValue();
            if ((balanceExpected<afterBalance-0.05) || (balanceExpected<afterBalance+0.05)) {
                LOG.info("Баланс соответствует ожидаемому: " +afterBalance);
                break;
            }
            count--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count==0){
                LOG.error("Баланс не  соответствует ожидаемому. Баланс сейчас:" + afterBalance + ", ожидалось : " + balanceExpected);
                assert false;
            }
        }
    }
}



