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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.waitEnabled;
import static ru.gamble.utility.Constants.AMOUNT;
import static ru.gamble.utility.Constants.PAYMENT_METHOD;


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


    @ActionTitle("проверяет, что на попапе пополнения есть кнопки-ссылки сумм")
    public void sposobSumm() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> summList = driver.findElements(By.xpath("//span[contains(@class,'jsLink smallJsLink')]")).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
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

    @ActionTitle("проверяет коректность смены макс.суммы при выборе пополнения с")
    public void verifiesCorrectnessOfChangeOfMaximumAmountWhenChoosingRefill(String methodsKey,String keyJsonByWSS){
        String methodName, exeptedMaxLimit;
        BigDecimal maxLimitInDB,maxLimitByWSS, maxValueOnPage;
        Object jsonByDB =  JSONValue.parse(Stash.getValue(methodsKey).toString()), checkValueByNull;
        Object jsonByWSS =  JSONValue.parse(Stash.getValue(keyJsonByWSS).toString());
        List<WebElement> methodsOfRefill = PageFactory.getWebDriver().findElements(By.xpath("//div[contains(@class,'active')]//div[contains(@class,'moneyChnl')]//label/div")).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
        LOG.info("Нашли на странице [" + methodsOfRefill.size() + "] элементов способов пополенния");

        LOG.info("Сравниваем максимальный лимит из базы с суммой на web-странице");
        for(WebElement method : methodsOfRefill){
            methodName = method.getAttribute("class").replace("payPartner ","");
            method.click();
            LOG.info("Нажали на метод [" + methodName + "]");
            checkValueByNull = JsonLoader.hashMapper(JsonLoader.hashMapper(JsonLoader.hashMapper(jsonByDB, methodName),"limit"),"max");
            maxLimitInDB = new BigDecimal(checkValueByNull == null ? "0" : String.valueOf(checkValueByNull)).divide( new BigDecimal("100"));
            LOG.info("Достали из [" + methodsKey + "] по имени способа[" + methodName + "] максимальный лимит[" + maxLimitInDB.toString() + "]");
            maxLimitByWSS = new BigDecimal(JsonLoader.hashMapper(JsonLoader.hashMapper(JsonLoader.hashMapper(JsonLoader.hashMapper(jsonByWSS,"data"),"limits"),methodName),"max_deposit").toString()).divide( new BigDecimal("100"));
            LOG.info("Достали из [" + keyJsonByWSS + "] по имени способа[" + methodName + "] максимальный лимит[" + maxLimitByWSS.toString() + "]");
            List<WebElement> list = PageFactory.getWebDriver().findElements(By.xpath("//div[contains(@class,'active')]//div[contains(@class,'smallJsLink')]/span"));
            if(list.isEmpty()){
                throw new AutotestError("Ошибка! Нет предложений номиналов сумм.");
            }

            LOG.info("Сравниваем два максимума [" + maxLimitInDB.toString() + "] и [" + maxLimitByWSS + "]");
            exeptedMaxLimit = (maxLimitInDB.compareTo(maxLimitByWSS) > 0) ? maxLimitByWSS.toString() : maxLimitInDB.toString();
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
        amount = data.get(AMOUNT);
        depositMethod = data.get(PAYMENT_METHOD);
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
        if(driver.findElements(By.xpath("//div[contains(.,'Ошибка при загрузке платежных систем')]")).stream().filter(WebElement::isDisplayed).collect(Collectors.toList()).size() > 0){
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

    @ActionTitle("проверяет, что при выборе суммы с помощью кнопок эта сумма правильно отображается на кнопке и в поле ввода")
    public void checkLinkSumm() throws Exception {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> summList = Stash.getValue("summListKey");
        summList = driver.findElements(By.xpath("//div[@class='modal modal_money-in ng-scope active']//table[@class='moneyInOutTable']//div[contains(@class,'smallJsLink__wrapper')]/span"))
                .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
        LOG.info("Проверка что при выборе суммы с помощью кнопок эта сумма правильно отображается на кнопке и в поле ввода");
        WebElement buttonOk;
        int sumOnButton, sumField;
        for (WebElement summ : summList) {
            buttonOk =  driver.findElement(By.id("btn-submit-money-in"));
            LOG.info("Вводим сумму с помощью кнопки " + summ.getText());
            summ.click();
            waitEnabled(buttonOk);
            sumOnButton = Integer.valueOf(buttonOk.findElement(By.xpath("//span[contains(@ng-bind,'totalPrice | number')]")).getText().replace(" ", ""));
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
        if (driver.findElement(By.id("btn-submit-money-in")).isEnabled()) {
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
        if (driver.findElement(By.id("btn-submit-money-in")).isEnabled()) {
            Assertions.fail("При сумме пополнения = 60000.000 кнопка осталась активной.");
        }

    }

//TODO Переделать этот метод, чтобы работал
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
            Map maxForWay = Stash.getValue("maxForWayKey");
            int currentMaxFlag = ((int) maxForWay.get(way)) <= summ ? 0 : 1;//switch не работает с булями, поэтому придется испольоватьвот такой флаг, который равен 0 = если допустимый максимум больше введенно суммы, и 1 - если допустимй максимум меньше введенной суммы
            switch (currentMaxFlag) {
                case 1:  //т.е. для выбранного способа пополнения введенная сумма разршена
                    if (!driver.findElement(By.id("btn-submit-money-in")).isEnabled()) {
                        Assertions.fail("При сумме " + summ + ", для выбранного способа пополнения " + way + " кнопка Пополнить недоступна, хотя максимально допустимая сумма " + maxForWay.get(way));
                    }
                    if (message.toString().contains("Сумма превышает максимальную допустимую") || message.toString().contains("Сумма меньше минимально допустимой")) {
                        Assertions.fail("При сумме " + summ + ", для выбранного способа пополнения " + way + " есть сообщение об ошибке  " + message.toString());
                    }
                    break;
                case 0: //т.е. для выбранного способа пополнения введенная сумма превышает максимум
                    if (driver.findElement(By.id("btn-submit-money-in")).isEnabled()) {
                        Assertions.fail("При сумме " + summ + ", для выбранного способа пополнения " + way + " кнопка Пополнить доступна, хотя максимально допустимая сумма " + maxForWay.get(way));
                    }
                    if (!message.toString().contains("Сумма превышает максимальную допустимую")) {
                        Assertions.fail("При сумме " + summ + ", для выбранного способа пополнения " + way + " нет сообщения об ошибке  " + message.toString());
                    }
                    break;
            }
        }
    }


}
