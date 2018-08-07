package ru.gamble.pages.userProfilePages;

import cucumber.api.DataTable;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.pages.CouponPage;
import ru.gamble.pages.mainPages.AuthenticationMainPage;
import ru.gamble.stepdefs.CommonStepDefs;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;
import static ru.gamble.stepdefs.CommonStepDefs.waitEnabled;
import static ru.gamble.stepdefs.CommonStepDefs.waitToPreloader;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.LOGIN;
import static ru.gamble.utility.Constants.STARTING_URL;


@PageEntry(title = "Мини Личный Кабинет")
public class PopUPLCPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(PopUPLCPage.class);
    private final String passCUPIS = "Regfordepoit_0601";
    private final String phone = "70010020601"; //телефон пользователя

    @ElementTitle("адрес почты")
    @FindBy(id = "user-profile")
    private WebElement emailBotton;

    @ElementTitle("Выйти")
    @FindBy(id = "log-out-button")
    private WebElement exitButton;

    @ElementTitle("История операций")
    @FindBy(id = "history")
    private WebElement history;

    @ElementTitle("Личный кабинет")
    @FindBy(id = "user-profile")
    private WebElement profileButton;

    @ElementTitle("Вывод")
    @FindBy(id = "withdraw-button")
    private WebElement withdrawButton;

    @ElementTitle("Пополнение")
    @FindBy(id = "fulfill-button")
    private WebElement depositButton;

    @FindBy(id = "money_in_amount2")
    private WebElement withdraw_field;

    @FindBy(id = "money_in_amount1")
    private WebElement deposit_field;

    @FindBy(xpath = "//div[@class='payPartner cupis_card']")
    private WebElement visa_deposit;

    @FindBy(xpath = "//div[@class='payPartner cupis_wallet']")
    private WebElement cupis_deposit;

    //@FindBy(xpath = "//div[@class='modal__body modal__body_moneyInOutBox']/button")
    @FindBy(xpath = "//div[@class='money-in-out__messages']/following-sibling::button[@class='btn_important money-in-out__btn']")
    private WebElement deposit_on; //кнопка Пополнить средства

    @ElementTitle("кнопка ВЫВЕСТИ на попапе")
    @FindBy(xpath = "//div[@class='money-in-out__line']/button[contains(@class,'money-in-out__btn')]")
    private WebElement withdrawOk;

    @ElementTitle("поле пароля при входе в ЦУПИС")
    @FindBy(id="form_login_password")
    private WebElement passwordCUPIS;

    @ElementTitle("кнопка ВОЙТИ в личный кабинет ЦУПИС")
    @FindBy (id="btn_authorization_enter")
    private WebElement authorizCUPIS;


    public PopUPLCPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(exitButton));
    }

    @ActionTitle("вводит крупную сумму для вывода")
    public void bigSummWithdraw() {
        WebDriver driver = PageFactory.getDriver();
        withdraw_field.click();
        withdraw_field.clear();
        withdraw_field.sendKeys("9999999");
    }

    @ActionTitle("проверяет появление сообщения о недостаточном количестве средств на балансе")
    public void excessOfLimit() {
        WebDriver driver = PageFactory.getDriver();
        try{
            driver.findElement(By.xpath("//div[@class='money-in-out__messages']/div[contains(@class, 'money-in-out__message money-in-out__message-error')]")).isDisplayed();
            LOG.info("Предупреждение о превышении лимита отображается. Кнопка вывода средств неактивна");
        }catch (Exception e){
            LOG.error("Ошибка! Предупреждение о превышении лимита не найдено или кнопка вывода средств активна");
            Assertions.fail("Предупреждение о превышении лимита не найдено или кнопка вывода средств активна");
        }
    }

    @ActionTitle("вводит крупную сумму, превышающую текущее значение баланса на 1")
    public void summWithdraw1() {
        WebDriver driver = PageFactory.getDriver();
        AuthenticationMainPage.rememberBalnce("рубли");
        float balance1 = Float.parseFloat(Stash.getValue("balanceKey"));
        int balance;
        balance = (int) balance1;
        withdraw_field.click();
        withdraw_field.clear();
        withdraw_field.sendKeys(String.valueOf(balance + 1));
    }

    @ActionTitle("вводит сумму меньше минимальной и проверяем для каждого способа")
    public void summMin() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        withdraw_field.click();
        withdraw_field.clear();
        withdraw_field.sendKeys("1");

        LOG.info("Кликаем на каждый доступный способ вывода и проверяем, что появляет ошибка лимитов.");
        List<WebElement> minSumElements = driver.findElements(By.xpath("//table[@class='moneyInOutTable']//div[not(contains(@class,'not-available')) and @ng-repeat='type in withdrawalTypes']" +
                "//label[contains(@for,'withdraw-method')]/following-sibling::div/span[1]"));
        List<String> minSumStrings = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?u)[^0-9]");
        minSumElements.forEach(element -> minSumStrings.add(pattern.matcher(element.getText()).replaceAll("")));
        String errorMessage;
        String errorMessageSum;
        LOG.info("Кликаем на каждый доступный способ вывода и проверяем, что появляет ошибка лимитов.");
        for (int i = 0; i < minSumElements.size(); i++) {
            minSumElements.get(i).findElement(By.xpath("../../label/div")).click();//кликаем на способ вывода
            workWithPreloader();
            errorMessage = driver.findElement(By.xpath("//div[@class='money-in-out__messages']/div[contains(@class, 'money-in-out__message money-in-out__message-error')]")).getText();
            errorMessageSum = pattern.matcher(errorMessage).replaceAll("");
            Assert.assertEquals(minSumStrings.get(i), errorMessageSum);
        }
        LOG.info("При попытке вывода суммы меньше достустимой появляется ошибка лимитов");
        Stash.put("minSumElementsKey", minSumElements);
    }

    @ActionTitle("проверяет, что при нажатии на сумму-ссылку появляется НДФЛ и бонусы")
    public void summLink() throws Exception {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> minSumElements = Stash.getValue("minSumElementsKey");
        String ndfl;
        String linkBalance;
        String sumOnButton;
        float ndflFloat;
        float linkBalanceFloat;
        float sumOnButtonFloat;
        float sumOnButtonShouldBe;
        for (int i = 0; i < minSumElements.size(); i++) {//Если нажимаем на сумму на серой ссылке, то должны отображаться бонусы и НДФЛ.
            Thread.sleep(1000);
            driver.findElement(By.xpath("//div[@class='smallJsLink__wrapper']/span[1]")).click(); //кликаем на сумму на ссылке
            Thread.sleep(1000);
            waitToPreloader();
            minSumElements.get(i).findElement(By.xpath("../../label/div")).click();//кликаем на способ вывода
            waitToPreloader();
            workWithPreloader();
            try {
                ndfl = driver.findElement(By.xpath("//div[contains(@class,'money-in-out__line')]/p/span")).getText();
            }catch (Exception e){
                throw new AutotestError("Ошибка! Не найден НДФЛ");
            }
            sumOnButton = driver.findElement(By.xpath("//button[@type = 'submit']/span[1]/span[1]")).getText().replace(" ", "").replace(",", ".");//сумма на кнопке
            linkBalance = driver.findElement(By.xpath("//div[@class='smallJsLink__wrapper']/span[1]")).getText().replace(" ", "");//сумма баланса на ссылке;
            ndflFloat = Float.parseFloat(ndfl);
            linkBalanceFloat = Float.parseFloat(linkBalance);
            sumOnButtonFloat = Float.parseFloat(sumOnButton);
            sumOnButtonShouldBe = linkBalanceFloat - ndflFloat;
            if (!driver.findElement(By.xpath("//div[@class='modal__body modal__body_moneyInOutBox']//p[contains(@class,'money-in-out__bonus-money')]")).isDisplayed()) {
                LOG.error("Бонусы ндфл не отображаются.");
                Assertions.fail("Бонусы ндфл не отображаются.");
            } else {
                LOG.info("Бонусы отображаются.");
            }

            LOG.info("Првоерка на то ,что сумма на кнопке совпадает с суммой баланса минус ндфл");

            if (sumOnButtonFloat != sumOnButtonShouldBe) {
                Assertions.fail("Сумма на кнопке и сумма вводимой суммы минус ндфл не совпадают.");
            } else {
                LOG.info("Суммы совпадают: " + sumOnButtonFloat + sumOnButtonShouldBe);

            }
        }
    }


    /**
     * попытка найти ту сумму вывода, при которой будет насчитываться НДФЛ, но не максимум
     * будет начинать с минимума и прибавлять кажды раз 5 рублей
     * @param way
     * @throws Exception
     */
    @ActionTitle("вводит минимальную сумму вывода для способа")
    public void inputSum(String way) throws Exception {
        int i=0;
        WebDriver driver = PageFactory.getDriver();
        Pattern pattern = Pattern.compile("(?u)[^0-9]");
        List<WebElement> allWayWithdraw = driver.findElements(By.xpath("//table[@class='moneyInOutTable']//div[not(contains(@class,'not-available')) and @ng-repeat='type in withdrawalTypes']"));
        String min = allWayWithdraw.get(i).findElement(By.xpath("span//span[@ng-if='withdrawMethods[type].limit.min']")).getText();//минимум для первого способа вывода
        String max = allWayWithdraw.get(i).findElement(By.xpath("span//span[@ng-if='withdrawMethods[type].limit.max']")).getText();//минимум для первого способа вывода
        max = pattern.matcher(max).replaceAll("");//максимум для вбранного способа вывода
        min = pattern.matcher(min).replaceAll("");//минимум для вбранного способа вывода

        LOG.info("Для выбранного способа вывода " + allWayWithdraw.get(i).findElement(By.xpath("span//div[contains(@class,'payPartner')]")).getAttribute("class"));
        LOG.info("Минимальная сумма вывода = " + min);
        LOG.info("Максимальная сумма вывода = " + max);

        int sum = Integer.valueOf(min);
        int allowMax = Integer.valueOf(max);
        float balanceF = Float.parseFloat(Stash.getValue("balanceKey"));
        int balance = (int) balanceF;
        allowMax=Math.min(allowMax,balance); //сумму вывода можно увеличивать либо до ограничения на способе вывода, либо до достижения суммы баланса
        LOG.info("Максимальная сумма вывода, с учетом текущего баланса = " + allowMax);

        LOG.info("попробуем найти сумму вывода, при которой будет высчитываться НДФЛ");
        allWayWithdraw.get(i).click(); //выбор первого способа вывода
       // CommonStepDefs.waitToPreloader();
        List<WebElement> bonuses = new ArrayList<>();
        Pattern patternBonus = Pattern.compile("(?u)[^0-9.]");


        withdraw_field.clear();
        withdraw_field.sendKeys(String.valueOf(sum));
        new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(withdrawOk));
        //       Thread.sleep(2000);//вместо слипа.вот тут нужно ожидание что кнопка Вывести - активна.


        bonuses=driver.findElements(By.xpath("//p[@ng-if and contains(@class,'money-in-out__bonus-money')]"));//проверка появился ли НДФЛ при максимуме вывода
        if (bonuses.isEmpty()) {//если НДФЛ не появился, то будем выводить минимум. а если НДФл при максимуме есть - то будем искать минимальную сумму, при которой он есть
            LOG.info("НДФЛ не начисляется. Проверка вывода без начисления бонусов при сумме вывода = " + min);
            withdraw_field.clear();
            withdraw_field.sendKeys(String.valueOf(min));
            Stash.put("bonus", "0");
            Stash.put("withdrawRub", min);
            new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(withdrawOk));
        }
        else {

            while (sum < allowMax) {
                withdraw_field.clear();
                withdraw_field.sendKeys(String.valueOf(sum));

                new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(withdrawOk));
                //       Thread.sleep(2000);//вместо слипа.вот тут нужно ожидание что кнопка Вывести - активна.


                bonuses = driver.findElements(By.xpath("//p[@ng-if and contains(@class,'money-in-out__bonus-money')]"));//проверка появился ли НДФЛ
                if (!bonuses.isEmpty()) {//если НДФЛ появился, то такую сумму и будем выводить
                    String bonus = bonuses.get(0).getText();
                    bonus = patternBonus.matcher(bonus).replaceAll("");
                    String sumOnButton = driver.findElement(By.xpath("//button[@type = 'submit']/span[1]/span[1]")).getText().replace(" ", "").replace(",", ".");//сумма на кнопке
                    Stash.put("bonus", bonus);
                    Stash.put("withdrawRub", sumOnButton);
                    LOG.info("Сумма вывода, при которой высчитывается НДФЛ = " + sum + ", НДФЛ = " + bonus);
                    break;
                }
                sum += 100;   //если НДФЛ на такой сумме не высчитывается - увеличим сумму на 5 рублей
            }
        }

        String siteHandle = driver.getWindowHandle();
        withdrawOk.click();
        withdrawCUPIS(siteHandle);
        driver.switchTo().window(siteHandle);
        Thread.sleep(500);


//todo дальше совершение стаки и проверка баланса прямо отсюда
    }

    /**
     * проверка вывода - все действия в ЦУПИС. в качестве параметра принимается адрес url -сата. чтобы знатчь куда возвращаться
     */
    public void withdrawCUPIS(String currentHandle){
        WebDriver driver = PageFactory.getDriver();
        new WebDriverWait(driver,10).until(numberOfWindowsToBe(2));
        Set<String> allHandles= driver.getWindowHandles();
        allHandles.remove(currentHandle);
        String urlCUPIS = allHandles.toArray()[0].toString();
        driver.switchTo().window(urlCUPIS);
        LOG.info("Перешли в ЦУПИС. Авторизация с паролем = " + passCUPIS);
//        for (String handle:allHandles){ //для переключения на вкладку ЦУПИС, т.к. точного адреса ЦУПИС не знаю
//            driver.switchTo().window(handle);
//            if (!driver.getCurrentUrl().contains(urlSite)) break;
//        }

        new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOf(passwordCUPIS));
        passwordCUPIS.clear();
        passwordCUPIS.sendKeys(passCUPIS);
        authorizCUPIS.click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!driver.findElements(By.id("card_tq09e1wn_sms")).isEmpty()) { //для кошелька ЦУПИС не нужно вводить код. для карт - нужно. поэтому вот такой сценари:есть поле для кода - вводим код. нету - значит не надо
            LOG.info("Для подтверждения вывода нужно ввести код из смс");
            urlCUPIS = driver.getWindowHandle();
            CommonStepDefs.newWindow("http://88.198.200.81:27000/testservice/");
            String mySMS = driver.findElement(By.xpath("//li[contains(text(),'" + phone + "')]")).getText();
            String code = mySMS.substring(mySMS.length() - 5, mySMS.length() - 1);
            LOG.info("Код подтверждения:" + code);
            LOG.info("Возвращаемся обратно в ЦУПИС и подверждаем вывод");
            driver.switchTo().window(urlCUPIS);
            new WebDriverWait(driver, 10).until(ExpectedConditions.titleIs("Первый ЦУПИС"));
            driver.findElement(By.id("card_tq09e1wn_sms")).clear();
            driver.findElement(By.id("card_tq09e1wn_sms")).sendKeys(code);
            driver.findElement(By.id("card_tq09e1wn_submit")).click();
        }

        if (!driver.findElements(By.id("wallet_dg09y5ch_submit")).isEmpty()) {//при выводе на карту - сразу после подтвержедния кода происходит вывод. при выводе на кошелек ЦУПИС - отдельная кнопочка.
            driver.findElement(By.id("wallet_dg09y5ch_submit")).click();
            LOG.info("Подверждаем вывод в ЦУПИС");
        }
        new WebDriverWait(driver,10).until(titleIs("Первый ЦУПИС — Перевод обрабатывается"));
    }


    @ActionTitle("проверяет снятие правильной суммы, и бонусов, если они были начислены")
    public void balanceAfterWithdraw(){
        LOG.info("Проверка что правильно изменился баланс рублей");
        BigDecimal sum;
        sum = new BigDecimal((String) Stash.getValue("withdrawRub")).setScale(2,RoundingMode.UP);
        Stash.put("sumKey",sum.toString());
        CouponPage.balanceIsOK("рубли");
        sum = new BigDecimal((String) Stash.getValue("bonus")).setScale(2,RoundingMode.UP).negate();
        if(sum.compareTo(new BigDecimal(0)) == 1){
            LOG.info("Проверка что правильно изменился баланс бонусов");
            Stash.put("sumKey",sum.toString());
            CouponPage.balanceIsOK("бонусов");
        }
    }


    @ActionTitle("смотрит, какие способы пополнения доступны")
    public void lookSummList() {
        WebDriver driver = PageFactory.getDriver();
        LOG.info("Смотрим какие способы пополнения доступны");
        List<WebElement> depositWays = driver.findElements(By.xpath("//div[@class='modal modal_money-in ng-scope active']//table[@class='moneyInOutTable']//div[@class='ng-scope moneyChnl']/span/label"));//если способ недоступне, то у имени класса будет строчка not-available и в этот список элемент уже не попадет
        Stash.put("depositWaysKey", depositWays);
        if (depositWays.isEmpty()) {
            LOG.info("Нет доступнх способов пополнения");
            Assertions.fail("Нет доступнх способов пополнения");
        }
        Collections.reverse(depositWays);
    }

    @ActionTitle("проверяет, что на попапе пополнения есть кнопки-ссылки сумм")
    public void sposobSumm() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> summList = driver.findElements(By.xpath("//div[@class='modal modal_money-in ng-scope active']//table[@class='moneyInOutTable']//div[contains(@class,'smallJsLink__wrapper')]/span"));
        Stash.put("summListKey", summList);
        Thread.sleep(1000);
        if (summList.isEmpty()) {
            Assertions.fail("При откртии попапа пополнения нет кнопок выбора суммы");
            LOG.info("На попапе пополнения нет кнопок с суммами. Попробуем переоткрыть этот попап.");
            int y = driver.findElement(By.xpath("//div[@class='modal ng-scope active']//table[@class='moneyInOutTable']//div[contains(@class,'smallJsLink__wrapper')]/span[last()]")).getLocation().getY();
            Actions act = new Actions(driver);
            ((JavascriptExecutor) driver).executeScript("window.scroll(50," + y + ")");
            act.click().build().perform();
        }
    }

    @ActionTitle("проверяет смену допустимой макс.суммы при выборе пополнения с")
    public void checkMax(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        Map maxForWay = new HashMap<String, Integer>();

        for(Map.Entry entry: data.entrySet() ){
            maxForWay.put(entry.getKey(),Integer.parseInt(String.valueOf(entry.getValue())));
        }

        LOG.info("Кладём максимальные суммы в память по ключу::maxForWayKey");
        Stash.put("maxForWayKey", maxForWay);
        String way;
        WebElement maxSum;
        int max;
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> depositWays = Stash.getValue("depositWaysKey");
        for (WebElement sposob : depositWays) {
            LOG.info("Выбираем способ пополнения " + sposob.findElement(By.xpath("div")).getAttribute("class"));
            sposob.click();
            //waitToPreloader(); //ждем появления прелоадера. т.к. если его не будет - значит и способ пооплнения по сути не изменился - не отправлялась инфа в сварм и вообще
            way = sposob.findElement(By.xpath("preceding-sibling::input")).getAttribute("value").trim();
            maxSum = driver.findElement(By.xpath("//div[@class='modal modal_money-in ng-scope active']//table[@class='moneyInOutTable']//div[contains(@class,'smallJsLink__wrapper')]/span[last()]"));//берем последний элемент из списка кнопочек сумм.Этот элемент и есть последня возможная сумма пополнения
            max = (int) Integer.valueOf(maxSum.getText().replace(" ", ""));
            if (!maxForWay.containsKey(way)) {
                Assertions.fail("Выбранный способ пополнения не описан в Map<String,Integer> maxForWay " + way);
                continue;
            }
            if ((int) maxForWay.get(way) != max) {
                Assertions.fail("Для способа пополнения " + way + " максимальная сумма = " + max + ", а ожидалось = " + maxForWay.get(way));
            }
            Stash.put("wayKey", way);
        }
//        List<WebElement> depositWays = Stash.getValue("depositWaysKey");
//        for (WebElement sposob : depositWays) {
//            LOG.info("Выбираем способ пополнения " + sposob.findElement(By.xpath("div")).getAttribute("class"));
//            sposob.click();
//            waitToPreloader(); //ждем появления прелоадера. т.к. если его не будет - значит и способ пооплнения по сути не изменился - не отправлялась инфа в сварм и вообще
//            String way;
//            WebElement maxSum;
//            int max;
//            Map maxForWay = new HashMap<String, Integer>();
//            maxForWay.put("cupis_card", 550000);
//            maxForWay.put("cupis_wallet", 550000);
//            maxForWay.put("cupis_mts", 14999);
//            maxForWay.put("cupis_megafon", 15000);
//            maxForWay.put("cupis_tele2", 15000);
//            maxForWay.put("cupis_beeline", 5000);
//            maxForWay.put("cupis_stoloto", 550000);
//            Stash.put("maxForWayKey", maxForWay);
//            way = sposob.findElement(By.xpath("preceding-sibling::input")).getAttribute("value").trim();
//            maxSum = driver.findElement(By.xpath("//div[@class='modal modal_money-in ng-scope active']//table[@class='moneyInOutTable']//div[contains(@class,'smallJsLink__wrapper')]/span[last()]"));//берем последний элемент из списка кнопочек сумм.Этот элемент и есть последня возможная сумма пополнения
//            max = (int) Integer.valueOf(maxSum.getText().replace(" ", ""));
//            if (!maxForWay.containsKey(way)) {
//                Assertions.fail("Выбранный способ пополнения не описан в Map<String,Integer> maxForWay " + way);
//                continue;
//            }
//            if ((int) maxForWay.get(way) != max) {
//                Assertions.fail("Для способа пополнения " + way + " максимальная сумма = " + max + ", а ожидалось = " + maxForWay.get(way));
//            }
//            Stash.put("wayKey", way);
//        }
    }

    @ActionTitle("проверяет, что при выборе суммы с помощью кнопок эта сумма правильно отображается на кнопке и в поле ввода")
    public void checkLinkSumm() throws Exception {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> summList = Stash.getValue("summListKey");
        summList = driver.findElements(By.xpath("//div[@class='modal modal_money-in ng-scope active']//table[@class='moneyInOutTable']//div[contains(@class,'smallJsLink__wrapper')]/span"))
        .stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
        LOG.info("Проверка что при выборе суммы с помощью кнопок эта сумма правильно отображается на кнопке и в поле ввода");
        WebElement buttonOk;
        int sumOnButton, sumField;
        for (WebElement summ : summList) {
            buttonOk = driver.findElement(By.xpath("//div[@class='modal__body modal__body_moneyInOutBox']/button"));
            LOG.info("Вводим сумму с помощью кнопки " + summ.getText());
            summ.click();
            waitEnabled(buttonOk);
            sumOnButton = Integer.valueOf(buttonOk.findElement(By.xpath("//span[@ng-bind='totalPrice | number']")).getText().replace(" ", ""));
            if (sumOnButton != Float.valueOf(summ.getText().replace(" ", ""))) {
                Assertions.fail("При клике на кнопку с суммой на кнопке #Пополнить# указана непраивьная сумма");
            }
            sumField = Integer.valueOf(driver.findElement(By.id("money_in_amount1")).getAttribute("value"));
            if (sumField != Float.valueOf(summ.getText().replace(" ", ""))) {
                Assertions.fail("При клике на кнопку с суммой в поле суммы неправильное значение");
            }
            Stash.put("sumFieldKey", sumField);
        }
    }

    @ActionTitle("проверяет поведение попапа пополнения при вводе суммы меньше минимально допустимой")
    public void checkPopapWithMinSumm() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        WebElement field = driver.findElement(By.id("money_in_amount1"));
        Stash.put("fieldKey", field);
        LOG.info("Очищаем поле с суммой и затем вводим туда 1");
        field.clear();
        field.sendKeys(String.valueOf(1));
        Thread.sleep(1000);
        StringBuilder message = new StringBuilder();
        Stash.put("messageKey", message);
        LOG.info("Запоминаем все сообщения с ошибками и предупредлениями, которые появились на попапе пополнения");
        driver.findElements(By.xpath("//div[contains(@class,'money-in-out__messages')]")).forEach(element -> message.append(element.getText())); //строка, содержащая все сообщения на попапе пополнения
        LOG.info("Смотрим что есть соответсвующее проедупреждение о минимально допустимой сумме, и что кнопка #Пополнить# заблокировалась");
        if (message.toString().isEmpty() || !message.toString().contains("Сумма меньше минимально допустимой")) {
            Assertions.fail("При сумме пополнения = 1 нет сообщения об ошибке. Сообщение: " + message.toString());
        }
        if (driver.findElement(By.xpath("//div[@class='modal__body modal__body_moneyInOutBox']/button")).isEnabled()) {
            Assertions.fail("При сумме пополнения = 1 кнопка осталась активной.");
        }
    }

    @ActionTitle("проверяет поведение попапа пополнения при вводе суммы больше максимально допустимой")
    public void checkPopapWithMaxSumm() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        LOG.info("Проверим поведение попапа при вводе суммы будльше максимума");
        LOG.info("Очищаем поле с суммой и затем вводим туда 600 000");
        WebElement field = Stash.getValue("fieldKey");
        field.clear();
        field.sendKeys(String.valueOf(600000000));
        Thread.sleep(1000);
        StringBuilder message = Stash.getValue("messageKey");
        message.setLength(0);//очищаем список ошибок чтобы заново его создать
        LOG.info("Запоминаем все сообщения с ошибками и предупреждениями, которые появились на попапе пополнения");
        driver.findElements(By.xpath("//div[contains(@class,'money-in-out__messages')]")).forEach(element -> message.append(element.getText())); //строка, содержащая все сообщения на попапе пополнения
        LOG.info("Смотрим что есть соответсвующее проедупреждение о минимально допустимой сумме, и что кнопка #Пополнить# заблокировалась");
        if (message.toString().isEmpty() || !message.toString().contains("Сумма превышает максимальную допустимую")) {
            Assertions.fail("При сумме пополнения = 600.000 нет сообщения об ошибке. Сообщение: " + message.toString());
        }
        if (driver.findElement(By.xpath("//div[@class='modal__body modal__body_moneyInOutBox']/button")).isEnabled()) {
            Assertions.fail("При сумме пополнения = 60000.000 кнопка осталась активной.");
        }

    }


    @ActionTitle("проверяет, что для разных способов пополнения, но при одинаковой сумме кнопка будет то активна, то заблокирована")
    public void checkDiffSumms() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        int summ = 500000;
        StringBuilder message = Stash.getValue("messageKey");
        LOG.info("Очищаем поле с суммой и затем вводим туда 500 000");
        WebElement field = Stash.getValue("fieldKey");
        field.clear();
        field.sendKeys(String.valueOf(summ));
        Thread.sleep(1000);
        message.setLength(0);//очищаем список ошибок чтобы заново его создать
        List<WebElement> depositWays = Stash.getValue("depositWaysKey");
        String way = Stash.getValue("wayKey");
        for (WebElement sposob : depositWays) {
            sposob.click();
            Thread.sleep(5000);//да, это много. но прелоадер будет не всегда. если предыдущи способ не давал поополнить, и следующий не дает пополнить - то прелоадера не будет
            message.setLength(0);//очищаем список ошибок чтобы заново его создать
            driver.findElements(By.xpath("//div[contains(@class,'money-in-out__messages')]")).forEach(element -> message.append(element.getText()));
            way = sposob.findElement(By.xpath("preceding-sibling::input")).getAttribute("value").trim();
            LOG.info("Выбрали способ пополнения " + way + " и теперь смотрим правильно ли все на попапе");
            //maxSum = driver.findElement(By.xpath("//div[@class='modal ng-scope active']//table[@class='moneyInOutTable']//div[contains(@class,'smallJsLink__wrapper')]/span[last()]"));//берем последний элемент из списка кнопочек сумм.Этот элемент и есть последня возможная сумма пополнения
            //max = (int) Integer.valueOf(maxSum.getText().replace(" ", ""));
            Map maxForWay = Stash.getValue("maxForWayKey");
            int currentMaxFlag = ((int) maxForWay.get(way)) <= summ ? 0 : 1;//switch не работает с булями, поэтому придется испольоватьвот такой флаг, который равен 0 = если допустимый максимум больше введенно суммы, и 1 - если допустимй максимум меньше введенной суммы
            switch (currentMaxFlag) {
                case 1:  //т.е. для выбранного способа пополнения введенная сумма разршена
                    if (!driver.findElement(By.xpath("//div[@class='modal__body modal__body_moneyInOutBox']/button")).isEnabled()) {
                        Assertions.fail("При сумме " + summ + ", для выбранного способа пополнения " + way + " кнопка Пополнить недоступна, хотя максимально допустимая сумма " + maxForWay.get(way));
                    }
                    if (message.toString().contains("Сумма превышает максимальную допустимую") || message.toString().contains("Сумма меньше минимально допустимой")) {
                        Assertions.fail("При сумме " + summ + ", для выбранного способа пополнения " + way + " есть сообщение об ошибке  " + message.toString());
                    }
                    break;
                case 0: //т.е. для выбранного способа пополнения введенная сумма превышает максимум
                    if (driver.findElement(By.xpath("//div[@class='modal__body modal__body_moneyInOutBox']/button")).isEnabled()) {
                        Assertions.fail("При сумме " + summ + ", для выбранного способа пополнения " + way + " кнопка Пополнить доступна, хотя максимально допустимая сумма " + maxForWay.get(way));
                    }
                    if (!message.toString().contains("Сумма превышает максимальную допустимую")) {
                        Assertions.fail("При сумме " + summ + ", для выбранного способа пополнения " + way + " нет сообщения об ошибке  " + message.toString());
                    }
                    break;

            }
        }
    }

    @ActionTitle("открывает попап через кнопку 'Внести депозит'")
    public void deposit() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        LOG.info("Обновляем страницу");
        driver.navigate().refresh();
        CommonStepDefs.workWithPreloader();
        Thread.sleep(2000);
        LOG.info("Открываем попап через кнопку #Внести депозит#");
        driver.findElement(By.xpath("//*[@id='private_panel']//button[contains(.,'Внести депозит')]")).click();
        WebElement field = Stash.getValue("fieldKey");
        int sumField = Stash.getValue("sumFieldKey");
        Thread.sleep(1000);
        if (!driver.findElement(By.xpath("//div[@class='modal__body modal__body_moneyInOutBox']")).isDisplayed()){
            Assertions.fail("При нажатии на кнопку Внести депозит не открылся попап пополнения");
        }
        int randomSum = 100 + (int) Math.random()*900;//рандомное число до от 100 до 1000
        field = driver.findElement(By.id("money_in_amount1"));
        LOG.info("Очищаем поле с суммой и затем вводим туда " + randomSum);
        field.clear();
        field.sendKeys(String.valueOf(randomSum));
        Thread.sleep(5000);
        sumField = (int) Integer.valueOf(driver.findElement(By.id("money_in_amount1")).getAttribute("value"));
        if (sumField != randomSum) {
            Assertions.fail("На кнопке неправильная сумма." + sumField + " вместо " + randomSum);
        }
    }

    @ActionTitle("вводит сумму и выбирает способ пополнения")
    public void chooseSummAndClick() throws InterruptedException {
        String sumBet = "1000";
        Stash.put("sumBetKey",sumBet);
        deposit_field.clear();
        deposit_field.sendKeys(sumBet);
        if (visa_deposit.isDisplayed()) {
            visa_deposit.click();
            LOG.info("Пополнение проходит через карту Виза");
        } else {
            if (cupis_deposit.isDisplayed()) {
                cupis_deposit.click();
                LOG.info("Пополнение проходит через кошелёк ЦУПИС");
            } else {
                Assertions.fail("Нет доступных способов пополнения");
            }
        }
        Thread.sleep(2000);
        deposit_on.click();
    }

    @ActionTitle("входит в кабинет ЦУПИС и совершает все необходимые операции для потверждения пополнения")
    public void cupicIn(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String phoneNumber = null, passwordWord = null;
        WebDriver driver = PageFactory.getDriver();
        Set<String> allHandles = driver.getWindowHandles();
        String loginId = "form_login_phone";
        String passwordId = "form_login_password";

        try {
            if (data.get("Телефон").equals(Constants.DEFAULT)) {
                phoneNumber = JsonLoader.getData().get(STARTING_URL).get("phone").getValue();
            } else {
                phoneNumber = data.get("Телефон");
            }

            if (data.get("Пароль_ЦУПИС").equals(Constants.DEFAULT)) {
                passwordWord = JsonLoader.getData().get(STARTING_URL).get("password").getValue();

            } else {
                passwordWord = data.get("Пароль_ЦУПИС");
            }
        } catch (DataException e) {
        }

            LOG.info("Переходим на страницу ЦУПИС");
            driver.switchTo().window(allHandles.toArray()[1].toString());

            CommonStepDefs.workWithPreloader();
            waitForElementPresent(By.id(passwordId), 4);

            LOG.info("Ищем поле ввода логина");
            WebElement login = driver.findElement(By.id(loginId));
            LOG.info("Вводим логин::" + phoneNumber);
            fillField(login, phoneNumber);

            LOG.info("Ищем поле ввода пароля");
            WebElement password = driver.findElement(By.id(passwordId));
            LOG.info("Вводим пароль::" + password);
            fillField(password, passwordWord);

            CommonStepDefs.workWithPreloader();
            driver.findElement(By.id("btn_authorization_enter")).click();
            CommonStepDefs.workWithPreloader();

            List<WebElement> cvvField = PageFactory.getDriver().findElements(By.name("cvv")).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
            if(cvvField.size() > 0){
                LOG.info("В ЦУПИС предлагается ввести CVV карты");
                String cvv = Generators.randomNumber(3);
                LOG.info("Вводим в поле CVV::" + cvv);
                fillField(cvvField.get(0),cvv);
                LOG.info("Нажимаем на кнопку 'Продолжить'");
                PageFactory.getDriver().findElements(By.xpath("//input[contains(@value,'Продолжить')]")).stream().filter(e -> e.isDisplayed()).findFirst().get().click();
                new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@value,'Подтвердить')]")));
                LOG.info("Нажимаем на кнопку 'Подтвердить'");
                PageFactory.getDriver().findElements(By.xpath("//input[contains(@value,'Подтвердить')]")).stream().filter(e -> e.isDisplayed()).findFirst().get().click();
                new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Вернуться к букмекеру')]")));
                LOG.info("Нажимаем на кнопку 'Вернуться к букмекеру'");
                PageFactory.getDriver().findElements(By.xpath("//a[contains(text(),'Вернуться к букмекеру')]")).stream().filter(e -> e.isDisplayed()).findFirst().get().click();
                LOG.info("Закрываем текущую вкладку");
                driver.close();
            }


//            driver.findElement(By.xpath("//input[@class='ui-button ui-button-final right']")).click();
//            waitForElementPresent(By.xpath("//input[@type='submit']"), 4);
//            driver.findElement(By.xpath("//input[@type='submit']")).click();
//            CommonStepDefs.workWithPreloader();

            LOG.info("Переходим обратно в на сайт");
            driver.switchTo().window(allHandles.toArray()[0].toString());
        }

    @ActionTitle("проверяет, увеличился ли баланс")
    public void checkIsBalance(){
        BigDecimal sumBet;
        WebDriver driver = PageFactory.getWebDriver();
        driver.navigate().refresh();
        waitForElementPresent(By.id("topPanelWalletBalance"), 10);
        sumBet = new BigDecimal((String) Stash.getValue("sumBetKey")).setScale(2,RoundingMode.UP).negate();
        Stash.put("sumKey",sumBet.toString());
        CouponPage.balanceIsOK("рубли");
    }

}




