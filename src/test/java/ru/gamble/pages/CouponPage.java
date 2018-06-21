package ru.gamble.pages;

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
import ru.gamble.pages.mainPages.MainPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;
import static ru.gamble.pages.AbstractPage.*;
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

    @ElementTitle("экспресс-бонус ссылка")
    @FindBy(xpath = "//div[@class='coupon-bonus-info coupon-bonus-info-link']")
    private WebElement expressBonusLink;

    @ElementTitle("текст экспресс бонуса")
    @FindBy(xpath = "//div[@class='coupon-bonus-info ng-binding']")
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

//    @ElementTitle("поле суммы общей ставки")
//    @FindBy(id="express-bet-input")
//    private WebElement coupon_field;

    @ElementTitle("поле суммы ставки типа Система")
    @FindBy(id="express-unitbet-input")
    private WebElement coupon_field_System_one;

    @ElementTitle("кнопка Заключить пари")
    @FindBy(id="place-bet-button")
    private WebElement buttonBet;

    @ElementTitle("переключатель ставки на бонусы")
    @FindBy(id="bonusmoney")
    private WebElement bonusBet;


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
        if (CommonStepDefs.stringParse(team1  + team2).equals(CommonStepDefs.stringParse(сouponGame))) {
            LOG.info("Названия команд в купоне совпадают с ожидаемыми: " + team1 + team2 + "=" + сouponGame);
        } else Assertions.fail("Названия команд в купоне не совпадают с ожидаемыми: " + team1 + team2 + сouponGame);
    }

    @ActionTitle("проверяет, совпадает ли исход в купоне с ожидаемым")
    public void checkIshod() {
        WebDriver driver = PageFactory.getDriver();
        String ishod = driver.findElement(By.xpath("//li[@class='coupon-bet-list__item_result']//span[@class='pick ng-binding']")).getText();
        String ishodName = Stash.getValue("ishodKey");//ожидаемое название исхода
        if (CommonStepDefs.stringParse(ishod).equals(CommonStepDefs.stringParse(ishodName))) {
            LOG.info("Выбранных исход в купоне совпадает с ожидаемым: " + ishod + "-" + ishodName);
        } else Assertions.fail("Выбранный исход в купоне не совпадает с ожидаемым: " + ishod + " - " + ishodName);

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
        List<WebElement> oldCoef = driver.findElements(xpath("//li[@class='coupon-bet-list__item_result']/div[@class='coupon-bet-list__item-column']/span[@class='coupon-betprice_old ng-binding']"));
        Thread.sleep(500);
        if (oldCoef.size() > 0 && !driver.findElement(xpath("//div[@class='bet-notification__error-text bet-notification__suggestion-wrapper']")).isDisplayed()) {
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
        for (int i = 2; i > 0; i--) {
            List<WebElement> oldCoef = driver.findElements(xpath("//li[@class='coupon-bet-list__item_result']/div[@class='coupon-bet-list__item-column']/span[@class='coupon-betprice_old ng-binding']"));
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
    public void selectTypeBet(String type) {
        WebDriver driver = PageFactory.getDriver();
        type = type.toLowerCase();
        if (driver.findElements(By.xpath("//div[@class='bs-type-switcher open']")).isEmpty()) {//если переключатель типа ставки не открыт - открываем
            LOG.info("Жмём на переключатель типов ставок");
            driver.findElement(By.xpath("//div[contains(@class,'bs-type-switcher__title')]")).click();
        }
        WebElement selectType = driver.findElement(By.xpath("//li[contains(translate(text(),'ЯЧСМИТЬБЮФЫВАПРОЛДЖЭЙЦУКЕНГШЩЗХЪ', 'ячсмитьбюфывапролджэйцукенгшщзхъ'),'" + type + "')]"));
        //driver.findElement(By.xpath("//li[contains(@class,'open-type-switcher__item') and contains(lower-case(text()),'"+type+"')]"));
        LOG.info("Переключаем тип ставки на '" + selectType.getText() + "'");
        selectType.click();
    }

    /**
     * для Системы в куопне по сути сразу несколько ставок. Если есть параметр one = то вводится сумма для одной ставки.
     * Если такого параметра нет - то сумма общей ставки. Для Экспресса всегда вводится общая ставка(потому что экпрес не разбивается на неколько пари)
     * @param sumBet
     */
    @ActionTitle("вводит сумму ставки")
    public void inputBet(String sumBet){
        inputBet(sumBet,"");
    }
    public void inputBet(String sumBet,String one){
        BigDecimal sum;
        WebDriver driver = PageFactory.getDriver();
       // boolean forOne = one.equals("для каждого разбиения") && !driver.findElement(By.xpath("//span[contains(@class,'bs-type-switcher__title-text')]")).getText().contains("Экспресс");//вводить размер ставки для каждого разбиения в Системе или нет
        boolean forOne = !one.equals("") && !driver.findElement(By.xpath("//span[contains(@class,'bs-type-switcher__title-text')]")).getText().contains("Экспресс");//вводить размер ставки для каждого разбиения в Системе или нет

        WebElement field = forOne?coupon_field_System_one:coupon_field;
        field.clear();
        field.sendKeys(String.valueOf(sumBet));
        LOG.info("Вводим сумму ставки : " + sumBet);

        String countBet = forOne?driver.findElement(By.xpath("//span[@class='eachway-zone__text ng-binding']")).getText().split(" ")[0]:"1";//количество ставок при выбранном виде систем(2/3,3/5,4/5...)
        sum = new BigDecimal(sumBet.trim()).setScale(2,RoundingMode.UP).multiply(new BigDecimal(countBet).setScale(0,RoundingMode.UP));

        Stash.put("sumKey",sum.toString());
    }


    @ActionTitle("заключает пари")
    public void doBet() throws AuthenticationException {
        WebDriver driver = PageFactory.getDriver();
        String xpathBet = "//div[@class='coupon-bet-list__wrap']//input[contains(@placeholder,'Ставка')]";
        String xpathMessage = "//div[contains(@class,'accepted-bet-message') and contains(.,'Ваша ставка принята.')]";

        LOG.info("Жмём Заключить пари");
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(coupon_bet_button));
        coupon_bet_button.click();

        waitingForPreloadertoDisappear(120);

        if (!driver.findElement(By.cssSelector("div.bet-accepted-noification")).isDisplayed()) {
            LOG.warn("Сообщение об успешной ставке не найдено");
        }

        LOG.info("Ожидаем исчезновения сообщения 'Ваша ставка принята!'");
        new WebDriverWait(driver, 20).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpathMessage)));

        LOG.info("Ожидаем исчезновения из купона принятых ставок");
         new WebDriverWait(driver, 10).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpathBet)));

        if (driver.findElements(By.xpath(xpathBet)).size() != 0) {
            throw new AuthenticationException("Ошибка! Принялись не все ставки.");
        }
    }

    /**
     * проверка что баланс изменился успешно. По параметру отепределяется проверка рублей или бонусов
     * @param param если параметр = "бонусов", то проверка бонусов, иначе - првоерка рублевого баланса
     */
    @ActionTitle("проверяет изменение баланса")
    public static void balanceIsOK(String param){
        WebDriver driver = PageFactory.getDriver();
        BigDecimal afterBalance, balanceExpected, sumBet;

        By balance=param.equals("бонусов")?By.id("bonus-balance"):By.id("topPanelWalletBalance");//определяем баланс рублей или бонусов будм првоерть
        String key = param.equals("бонусов")?"balanceBonusKey":"balanceKey";
        int count = 30;
        balanceExpected = new BigDecimal((String) Stash.getValue(key)).setScale(2, RoundingMode.UP);
        sumBet = new BigDecimal((String) Stash.getValue("sumKey")).setScale(2, RoundingMode.UP);

        while (count >0){
            afterBalance = new BigDecimal(driver.findElement(balance).getText()).setScale(2, RoundingMode.UP);

            if((balanceExpected.subtract(sumBet).subtract(afterBalance).abs()).compareTo(new BigDecimal(0.05).setScale(2,RoundingMode.UP))== -1){
                LOG.info("Баланс соответствует ожидаемому: " + afterBalance.toString());
                break;
            }
            LOG.info("тик-так");
            count--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count == 0){
                Assertions.fail("Баланс не соответствует ожидаемому. Баланс сейчас: " + afterBalance + ", ожидалось : " + balanceExpected.subtract(sumBet));
            }
        }
    }

    @ActionTitle("проверяет изменение количества экспрессов при переключении вида системы")
    public void checkCountExpress(){
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> listOfBetType = driver.findElements(By.xpath("//ul[@class='open-type-switcher__list']/li"));
        for (int typeSys = 3; typeSys < listOfBetType.size(); typeSys++) {
            String countExp = listOfBetType.get(typeSys).findElement(By.xpath("span")).getText().split(" ")[0].replace("(", "");
            listOfBetType.get(typeSys).click();
            String countBet = driver.findElement(By.xpath("//span[@class='eachway-zone__text ng-binding']")).getText().split(" ")[0];
            if (!countBet.equals(countExp)) {
                 Assertions.fail("Неправильное количество ставок. Вместо " + countExp + " стало " + countBet);
            } else {
                LOG.info("Ок. Количество ставок(экспрессов) равно " + countBet);
            }
            driver.findElement(By.cssSelector("div.bs-type-switcher__title")).click();
        }
    }

    /**
     * првоерка кнопка Заключить Пари в купоне активна или заблокирована. и Печать ошибок и предупреждений в купоне, если есть
     * @param status - ожидаемое состояние кнопки(активна, заблокирована)
     */
    @ActionTitle("проверяет что кнопка Заключить Пари")
    public void checkButtonBet(String status){
        WebDriver driver = PageFactory.getDriver();
        Boolean disabled = status.equals("активна")?true:false;
        if (buttonBet.isEnabled()!=disabled){
            Assertions.fail("Кнопка 'Заключить пари' в неправильном состоянии: не " + status);
        }
        LOG.info("Кнопка 'Заключить пари' " + status);

        List <WebElement> notifications = driver.findElements(xpath("//div[contains(@class,'bet-notification__suggestion_visible') and @trans]"));
        notifications.addAll(driver.findElements(xpath("//div[contains(@class,'bet-notification__warning_visible')]/span")));
        for (WebElement element:notifications){
            LOG.info(element.getText());
        }
    }

    @ActionTitle("вводит сумму одной ставки Ординар")
    public void inputBetOrdinar(String sum){
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> listBets = driver.findElements(By.xpath("//div[@class='coupon-bet-list__wrap']//input[contains(@placeholder,'Ставка')]"));
        LOG.info("Вводим для первого события сумму ставки: " + sum);
        WebElement input;
        input=listBets.size()==1?
                coupon_field:
                driver.findElements(By.xpath("//input[contains(@class,'input_coupon-ordinar')]")).get(0);
        input.clear();
        input.sendKeys(String.valueOf(sum));
        String sumBet = sum.trim();
        Stash.put("sumKey",sumBet);
    }

    @ActionTitle("нажимает кнопку ВНИЗ - дублирование ставки для всех пари")
    public void dublicateBet(){
        BigDecimal sum;
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> listBets = driver.findElements(By.xpath("//div[@class='coupon-bet-list__wrap']//input[contains(@placeholder,'Ставка')]"));
        String sumFirstBet = listBets.get(0).getAttribute("value");
        LOG.info("Сумма первой ставки = " + sumFirstBet);
        if (listBets.size()>1) {//если в купоне не одна ставка, то имеет смысл нажать кнопку "вниз" заполняющую сумму ставки для всех одинаковой
            LOG.info("Нажимаем на кнопку ВНИЗ чтобы скопировать размер ставки для второго события");
            driver.findElement(By.xpath("//ul[contains(@class,'coupon-bet-list')]//i[contains(@class,'icon-download-icon')]")).click();
            LOG.info("Проверяем что размер ставки продублировался");
            String valueBet = listBets.get(1).getAttribute("value");
            if (!valueBet.equals(sumFirstBet)) {
                Assertions.fail("Ставка не продублировалась для второго события " + valueBet);
            }
        }
        sum = new BigDecimal(sumFirstBet.trim()).setScale(2,RoundingMode.UP).multiply(new BigDecimal(listBets.size()));
        LOG.info("Всего ставок в купоне::" + listBets.size());

        Stash.put("sumKey",sum.toString());
    }

    @ActionTitle("переходит в настройки и меняет коэффицент в купоне")
    public void changePreferencesCoeff() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        LOG.info("переходит в настройки и меняет коэффицент");
        preferences.click();
        String previous;
        List<WebElement> list = driver.findElements(By.cssSelector("span.prefs__key"));
        WebElement coeff = driver.findElement(cssSelector("span.coupon-betprice"));
        previous = coeff.getText();
        LOG.info("Переключаемся на '" + list.get(2).getText() + "' формат отображения");
        list.get(2).click();
        LOG.info("Текущее значение коэффициента : " + coeff.getText());
        Thread.sleep(350);
        if (previous.equals(coeff.getText())) {
            LOG.error("Формат отображения коэффициентов не изменился");
            Assertions.fail("Формат отображения коэффициентов не изменился");
        }
        LOG.info("Смена форматов отображения коэффицентов прошла успешно");
        list.get(0).click();
    }

    @ActionTitle("выбирает ставку бонусами")
    public void onBonus(){
        WebDriver driver = PageFactory.getDriver();
        if (!driver.findElement(xpath("//div[@id='bonusmoney']/..")).getAttribute("class").contains("active")){
            bonusBet.click();
        }
        LOG.info("Купон перключен на ставку БОНУСАМИ");
    }
}



