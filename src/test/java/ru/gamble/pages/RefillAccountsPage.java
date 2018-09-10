package ru.gamble.pages;

import cucumber.api.DataTable;
import net.minidev.json.JSONValue;
import org.assertj.core.api.Assertions;
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
import ru.gamble.utility.JsonLoader;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@PageEntry(title = "Пополнение счёта")
public class RefillAccountsPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(RefillAccountsPage.class);

    @FindBy(xpath = "//div[contains(@class,'modal__title') and contains(text(),'Пополнение счёта')]")
    private WebElement pageTitle;

    @ElementTitle("Сумма")
    @FindBy(xpath = "//div[@class='input888wrpr']/input[contains(@class,'inpS')]")
    private WebElement inputAmount;

    @ElementTitle("Пополнить")
    @FindBy(id = "btn-submit-money-in")
    private WebElement buttonRefill;

    @ElementTitle("VISA_МИР")
    @FindBy(xpath = "//div[@class='payPartner cupis_card']")
    private WebElement visa_mirButton;

    @FindBy(xpath = "//div[@class='payPartner cupis_wallet']")
    private WebElement cupis_deposit;

    public RefillAccountsPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
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
            maxSum = driver.findElement(By.xpath("//table[@class='moneyInOutTable']//div[@class='smallJsLink__wrapper']/span[last()]"));//берем последний элемент из списка кнопочек сумм.Этот элемент и есть последня возможная сумма пополнения
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
    }

    @ActionTitle("проверяет коректность смены макс.суммы при выборе пополнения с")
    public void verifiesCorrectnessOfChangeOfMaximumAmountWhenChoosingRefill(String methodsKey,String keyJsonByWSS){
        String methodName, exeptedMaxLimit;
        BigDecimal maxLimitInDB,maxLimitByWSS, maxValueOnPage;
        Object jsonByDB =  JSONValue.parse(Stash.getValue(methodsKey).toString());
        Object jsonByWSS =  JSONValue.parse(Stash.getValue(keyJsonByWSS).toString());
        List<WebElement> methodsOfRefill = PageFactory.getWebDriver().findElements(By.xpath("//div[contains(@class,'active')]//div[contains(@class,'moneyChnl')]//label/div")).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
        LOG.info("Нашли на странице [" + methodsOfRefill.size() + "] элементов способов пополенния");

        LOG.info("Сравниваем максимальный лимит из базы с суммой на web-странице");
        for(WebElement method : methodsOfRefill){
            methodName = method.getAttribute("class").replace("payPartner ","");
            method.click();
            LOG.info("Нажали на метод [" + methodName + "]");
            maxLimitInDB = new BigDecimal(JsonLoader.hashMapper(JsonLoader.hashMapper(JsonLoader.hashMapper(jsonByDB, methodName),"limit"),"max").toString()).divide( new BigDecimal("100"));
            LOG.info("Достали из [" + methodsKey + "] по имени способа[" + methodName + "] максимальный лимит[" + maxLimitInDB.toString() + "]");
            maxLimitByWSS = new BigDecimal(JsonLoader.hashMapper(JsonLoader.hashMapper(JsonLoader.hashMapper(JsonLoader.hashMapper(jsonByWSS,"data"),"limits"),methodName),"max_deposit").toString()).divide( new BigDecimal("100"));
            LOG.info("Достали из [" + keyJsonByWSS + "] по имени способа[" + methodName + "] максимальный лимит[" + maxLimitByWSS.toString() + "]");
            List<WebElement> list = PageFactory.getWebDriver().findElements(By.xpath("//div[contains(@class,'active')]//div[contains(@class,'smallJsLink')]/span"));
            if(list.isEmpty()){
                throw new AutotestError("Ошибка! Нет предложений номиналов сумм.");
            }

            LOG.info("Сравниваем два максимума [" + maxLimitInDB.toString() + "] и [" + maxLimitByWSS + "]");
            exeptedMaxLimit = (maxLimitInDB.compareTo(maxLimitByWSS) == 1) ? maxLimitByWSS.toString() : maxLimitInDB.toString();
            LOG.info("Ожидаемый максимум должен быть[" + exeptedMaxLimit + "]");

            maxValueOnPage = new BigDecimal(list.get(list.size()-1).getText().replaceAll(" +",""));
            assertThat(maxValueOnPage)
                    .as("Ошибка! Фактический максимум на странице[" + maxValueOnPage.toString() + "] не равен ожидаемому максимуму [" + exeptedMaxLimit + "]")
                    .isEqualTo(exeptedMaxLimit);
        }

    }

    @ActionTitle("вводит сумму и выбирает способ пополнения c")
    public void enterAmountAndSelectDepositMethod(DataTable dataTable)  {
        checkForErrorLoadingPaymentSystems();

        WebDriver driver = PageFactory.getWebDriver();
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String amount, depositMethod;
        amount = data.get("СУММА");
        depositMethod = data.get("Способ");
        LOG.info("Cохраняем в память сумму в переменной 'СУММА' и вводим в поле::" + amount);
        Stash.put("СУММА",amount);
        fillField(inputAmount,amount);

        if(depositMethod.contains("") && visa_mirButton.isDisplayed()){
            visa_mirButton.click();
            LOG.info("Пополнение проходит через карту [" + depositMethod + "]");
        } else {
            if (cupis_deposit.isDisplayed()) {
                cupis_deposit.click();
                LOG.info("Пополнение проходит через кошелёк ЦУПИС");
            } else {
                Assertions.fail("Нет доступных способов пополнения");
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.getMessage();
        }
        buttonRefill.click();
    }

    /**
     * проверка появления окна "Ошибка при загрузке платежных систем"
     */
    private void checkForErrorLoadingPaymentSystems(){
        WebDriver driver = PageFactory.getWebDriver();
        if(driver.findElements(By.xpath("//div[contains(.,'Ошибка при загрузке платежных систем')]")).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList()).size() > 0){
            throw new AutotestError("Ошибка при загрузке платежных систем!");
        }
    }

    @ActionTitle("вводит сумму и выбирает способ пополнения")
    public void chooseSummAndClick() throws InterruptedException {
        String sumBet = "1000";
        Stash.put("sumBetKey",sumBet);
        fillField(inputAmount,sumBet);

        if (visa_mirButton.isDisplayed()) {
            visa_mirButton.click();
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
        visa_mirButton.click();
    }


}
