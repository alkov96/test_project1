package ru.gamble.pages;

import cucumber.api.DataTable;
import cucumber.api.java.mn.Харин;
import org.apache.poi.ss.formula.functions.T;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.gamble.stepdefs.CommonStepDefs;
import ru.gamble.utility.JsonLoader;
import ru.gamble.utility.YandexPostman;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;
import ru.sbtqa.tag.qautils.errors.AutotestError;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.RANDOM;
import static ru.gamble.utility.Generators.randomString;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;


public abstract class AbstractPage extends Page {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPage.class);

    @ElementTitle("На главную")
    @FindBy(id = "main-logo")
    protected WebElement onMainPageButton;

    @ElementTitle("Иконка юзера")
    @FindBy(id = "user-icon")
    protected WebElement userIconButton;

    @ElementTitle("Бургер")
    @FindBy(id = "service-list")
    protected WebElement burgerBottom;

    @ElementTitle("Сервисное сообщение")
    @FindBy(xpath = "//div[contains(@class,'tech-msg__content')]")
    private WebElement serviceMessage;

    @ElementTitle("Иконка закрытия сервисного сообщения")
    @FindBy(xpath = "//span[contains(@class,'tech-msg__close')]")
    private WebElement loseServiceMessage;

    @ElementTitle("День")
    @FindBy(className = "inpD")
    protected WebElement fieldDay;

    @ElementTitle("Месяц")
    @FindBy(xpath = "//div[contains(@class,'dateInput')]/div[@class='inpM']")
    protected WebElement fieldMonth;

    @ElementTitle("Год")
    @FindBy(className = "inpY")
    protected WebElement fieldYear;

    @ElementTitle("Подвал")
    @FindBy(xpath = "//*[@class='footer__pin']")
    protected WebElement footerButton;

    @ElementTitle("Прематч")
    @FindBy(id = "prematch")
    private WebElement prematchBottom;

    @ElementTitle("Настройки")
    @FindBy(id = "preferences")
    protected WebElement preferences;

    @ElementTitle("Активация Быстрой ставки")
    @FindBy(id = "quickbet")
    protected WebElement quickButton;

//для ставок экспресс, быстрой ставки - т.е. там где 1 поле для ставки
    @ElementTitle("поле суммы общей ставки")
    @FindBy(id="express-bet-input")
    protected WebElement coupon_field;


    // Метод три раза пытается обновить главную страницу

    public void tryingLoadPage(WebElement element, int count){
        WebDriver driver = PageFactory.getWebDriver();
        LOG.info("Ищем элемент [" + element + "] на странице::" + driver.getCurrentUrl() + "\n");

        for(int j = 0; j < count; j++) {
            try {
                new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(element));
                break;
            } catch (Exception e){
                driver.navigate().refresh();
            }
            if(j >= count - 1){
                throw new AutotestError("Ошибка! Не нашли элемент после " + j + " попыток перезагрузки страницы");
            }
        }
    }


    @ActionTitle("открывает Избранное")
    public static void openFavourite(){
        LOG.info("vot");
        WebDriver driver = PageFactory.getDriver();
        driver.findElement(By.id("elected")).click();//нажали на кнопку избранного
    }

    @ActionTitle("сохраняет с")
    public void saveKeyValue(DataTable dataTable){ CommonStepDefs.saveValueToKey(dataTable); }

    @ActionTitle("нажимает кнопку")
    public static void pressButtonAP(String param){
        CommonStepDefs.pressButton(param);
    }

    @ActionTitle("stop")
    public static void stop(){
        LOG.info("STOP");
    }

    @ActionTitle("закрываем браузер")
    public static void closeBrowser(){
        PageFactory.dispose();
        LOG.info("Браузер закрыт");
    }

    /**
     * Метод получения письма и перехода по ссылке для завершения регистрации на сайте
     *
     * @param key - ключ по которому получаем е-mail из памяти.
     */
    @ActionTitle("завершает регистрацию перейдя по ссылке в")
    public void endRegistrationByEmailLink(String key){
        WebDriver driver = getWebDriver();
        String email = Stash.getValue(key);
        String link = "";
        String url = "";

        try {
            url = JsonLoader.getData().get("site1").get("mainurl").getValue();
            link = YandexPostman.getLinkForAuthentication(email);
        }catch (DataException de){
            LOG.error("Ошибка! Не смогли получить ссылку сайта");
        } catch (Exception e) {
            LOG.error("Ошибка! Не смогли получить ссылку для аутентификации.");
            e.printStackTrace();
        }

        LOG.info("Переходим по ссылке из e-mail");

        driver.get(url + "?action=verify&" + link);

        new WebDriverWait(driver,30).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.modal__closeBtn.closeBtn")));

        LOG.info("Закрываем уведомление об успешном подтверждении почты");
        driver.findElement(By.cssSelector("a.modal__closeBtn.closeBtn")).click();
    }

    /**
     * Открывает выпадающий список и выбирает оттуда пункт случайным образом
     *
     * @param element  - поле где ждем выпадающий список
     * @param max - максимальный пункт, который можно выбрать (включая этот max). Второго параметра может и не быть. тогда максимум - это длина всего списка
     * @return Возвращает номер пункта который был выбран
     */
    protected int selectMenu(WebElement element, int max) {
        WebDriver driver = PageFactory.getWebDriver();
        int menuSize = element.findElements(By.xpath("custom-select/div[2]/div")).size();
        menuSize -= (max + 1);
        int select = 1 + (int) (Math.random() * menuSize);
        element.findElement(By.xpath("custom-select")).click();
        element.findElement(By.xpath("custom-select/div[2]/div[" + select + "]")).click();
        return select;
    }

    protected int selectMenu(WebElement element) {
        return selectMenu(element, 0);
    }

    protected void enterDate(String value){
        if(value.equals(RANDOM)){

            do {
                selectMenu(fieldYear);
                selectMenu(fieldMonth);
                selectMenu(fieldDay);
                LOG.info("Вводим случайную дату::" );
            } while (PageFactory.getWebDriver().findElement(By.className("inpErrText")).isDisplayed());
        }else {
            String[] tmp = value.split(".");
            LOG.info("Вводим дату");
            selectMenu(fieldMonth,Integer.parseInt(tmp[1]));
            selectMenu(fieldDay,Integer.parseInt(tmp[0]));
            selectMenu(fieldYear,Integer.parseInt(tmp[2]));
        }
    }

    /**
     * В выбранном поле вводит один символ и если появляется выпадающий список - выбирает первый пункт из него.
     * Иначе либо заново вводит символ и ждет список, либо заполняет поле рандомной последовательностью
     *
     * @param field    - поле, которое заполняем
     * @param authFill - булев параметр, говорит о том обязательно ли выбирать из списка (true), или можно заполнить рандомом (false)
     */
    public void fillAddress(WebElement field, boolean authFill)  {
        List<WebElement> list;
        do {
            field.clear();
            StringBuilder n = new StringBuilder();
            n.append((char) ('А' + new Random().nextInt(64)));
            field.sendKeys(n);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list = field.findElements(By.xpath("../ul[1]/li"));
        } while (list.isEmpty() && authFill);
        if (field.findElements(By.xpath("../ul[1]/li")).isEmpty()) {
            field.clear();
            field.sendKeys(randomString(20));
        } else
            field.findElement(By.xpath("../ul[1]/li[" + (new Random().nextInt(list.size()) + 1) + "]")).click();

        if (field.getAttribute("value").length() == 0) {
            LOG.info("ОШИБКА. Нажали на пункт в выпадающем списке для города,а знчение не выбралось!!");
            field.sendKeys(randomString(20));
        }
    }

    @ActionTitle("проверяет присутствие текста")
    public void checksPresenceOfText(String text){
        List<WebElement> list = PageFactory.getWebDriver().findElements(By.xpath("//*[text()='" + text + "']"))
                .stream().filter(element -> element.isDisplayed()).collect(Collectors.toList());
        assertThat(!list.isEmpty()).as("Ошибка.Не найден::[" + text + " ]").isTrue();
    }


    /**
     * Метод который по имени WebElement находит его на текущей странице,
     * достаёт его ссылку и переходит по ней
     * @param param - имя WebElement
     */
    @ActionTitle("переходит по ссылке")
    public static void goesByReference(String param)throws PageInitializationException,PageException{
        Page page = null;
        page = PageFactory.getInstance().getCurrentPage();
        String link =  page.getElementByTitle(param).getAttribute("href");
        PageFactory.getWebDriver().get(link);
        LOG.info("Получили и перешли по ссылке::" + link);
        workWithPreloader();
    }

    @ActionTitle("проверяет наличие сообщения с текстом")
    public static void checkServiceMessageTrue(String param){

    }

    @ActionTitle("проверяет отсутствие сообщения с текстом")
    public static void checkServiceMessageFalse(){
    }

    @ActionTitle("проверяет наличие иконки закрытия")
    public static void chectCloseServiceMessageTrue(){
    }

    @ActionTitle("проверяет отсутствие иконки закрытия")
    public static void chectCloseServiceMessageFalse(){
    }

    public void fillCouponFinal(int count, String ifForExperss, By findCoeffs) throws InterruptedException {
        if (ifForExperss == "correct") {
            List<WebElement> eventsInCoupon;
            List<WebElement> correctMarkets;
            Thread.sleep(3000);
            waitForElementPresent(findCoeffs,1000);
            correctMarkets = getWebDriver().findElements(findCoeffs)
                        .stream().filter(e -> e.isDisplayed() && !e.getText().contains("-") && Double.parseDouble(e.getText()) >= 1.26)
                        .limit(count+10).collect(Collectors.toList());
            for (WebElement coefficient : correctMarkets) {
                tryToClick(coefficient);
                eventsInCoupon = PageFactory.getWebDriver().findElements(By.xpath("//li[@class='coupon-bet-list__item']"));
                if (eventsInCoupon.size() == count) {
                    break;
                }
            }
        }
        if (ifForExperss == "incorrect") {
            List<WebElement> eventsInCoupon;
            List<WebElement> inCorrectMarkets = null;
            waitForElementPresent(findCoeffs,1000);
            List<WebElement> allDaysPages = PageFactory.getWebDriver().findElements(By.cssSelector("span.livecal-days__weekday.ng-binding"));
            int tryPage = 0;
            do {
                try {
                    inCorrectMarkets = getWebDriver().findElements(findCoeffs)
                            .stream().filter(e -> e.isDisplayed() && !e.getText().contains("-") && Double.parseDouble(e.getText()) < 1.25)
                            .limit(count+3).collect(Collectors.toList());
                } catch (StaleElementReferenceException e){
                    tryPage ++;
                    allDaysPages.get(tryPage).click();
                }
            }  while (inCorrectMarkets.size() < count && tryPage < allDaysPages.size()-1);
            for (WebElement coefficient : inCorrectMarkets) {
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

    public void tryToClick(WebElement element){
        try {
            element.click();
        } catch (StaleElementReferenceException e){
            tryToClick(element);
        }
    }


    /**
     * включается быстрая свтака и в поле суммы вводится сумма, указанная в праметре. Если в параметр написано "больше баланса" то вводится (balance+1)
     * @param sum
     */
    @ActionTitle("включает быструю ставку и вводит сумму")
    public void onQuickBet(String sum){
        if (!quickButton.findElement(By.xpath("..")).getAttribute("class").contains("active")){

            quickButton.click();
        }
        float sumBet;
        sumBet = sum.equals("больше баланса")?(float)Stash.getValue("balanceKey")+1:Float.valueOf(sum);


//от сюда
        DecimalFormat formatter;

        if(sumBet - (int)sumBet > 0.0)
            formatter = new DecimalFormat("0.00"); //Here you can also deal with rounding if you wish..
        else
            formatter = new DecimalFormat("0");
        String aa=formatter.format(sumBet);
//и до сюда - это приведение экспонициального представления числа - в обычный вид. а то строчка вида "1.00571E5" - плохой вариант дл сумм ставки
        coupon_field.clear();
        coupon_field.sendKeys(aa);
        Stash.put("sumKey",sumBet);
    }


    @ActionTitle("проверяет наличие сообщения об ошибке в купоне")
    public void checkError(String pattern){
        WebDriver driver = PageFactory.getWebDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        List<WebElement> listErrors = driver.findElements(By.xpath("//div[contains(@class,'bet-notification__warning_visible')]"));
        if (listErrors.isEmpty()){
            Assertions.fail("Нет никаких предупреждений в купоне");
        }
        for (WebElement error: listErrors){
            if (error.getText().contains(pattern)){
                LOG.info("Искомое предупреждение в купоне найдено: " + pattern);
                break;
            }
            if (listErrors.indexOf(error)==(listErrors.size()-1)){
                Assertions.fail("Искомого предупреждения нет в купоне!");
            }
        }
    }

    @ActionTitle("ждет некоторое время")
    public void waiting(String sec) throws InterruptedException {
        Thread.sleep(Integer.valueOf(sec)*1000);
    }
}

