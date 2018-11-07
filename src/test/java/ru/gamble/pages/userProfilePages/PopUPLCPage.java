package ru.gamble.pages.userProfilePages;

import org.assertj.core.api.Assertions;
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
import ru.gamble.pages.mainPages.AuthenticationMainPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static ru.gamble.stepdefs.CommonStepDefs.waitToPreloader;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;


@PageEntry(title = "Мини Личный Кабинет")
public class PopUPLCPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(PopUPLCPage.class);

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

    @ElementTitle("Вывести средства")
    @FindBy(id = "withdraw-button")
    private WebElement withdrawButton;

    @FindBy(id = "money_in_amount2")
    private WebElement withdraw_field;

    @FindBy(id = "money_in_amount1")
    private WebElement deposit_field;

    @ElementTitle("кнопка ВЫВЕСТИ на попапе")
    @FindBy(xpath = "//div[@class='money-in-out__line']/button[contains(@class,'money-in-out__btn')]")
    private WebElement withdrawOk;

    @ElementTitle("Профиль")
    @FindBy(xpath = "//div[@class='user-menu__item']/a[contains(.,'Профиль')]")
    private WebElement profile;


    public PopUPLCPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
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
    public void summMin(){
        WebDriver driver = PageFactory.getDriver();
        withdraw_field.click();
        withdraw_field.clear();
        withdraw_field.sendKeys("1");

        LOG.info("Кликаем на каждый доступный способ вывода и проверяем, что появляет ошибка лимитов.");
        List<WebElement> minSumElements = driver.findElements(By.xpath("//table[@class='moneyInOutTable']//div[not(contains(@class,'not-available')) and @ng-repeat='type in withdrawalTypes']" +
                "//label[contains(@for,'withdraw-method')]/following-sibling::div/span[1]"));
        List<String> minSumStrings = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?u)[^0-9]");
        minSumElements.forEach(element -> minSumStrings.add(pattern.matcher(element.getAttribute("innerText")).replaceAll("")));
        String errorMessage;
        String errorMessageSum;
        LOG.info("Кликаем на каждый доступный способ вывода и проверяем, что появляет ошибка лимитов.");
        for (int i = 0; i < minSumElements.size(); i++) {
            minSumElements.get(i).findElement(By.xpath("../../label/div")).click();//кликаем на способ вывода
            workWithPreloader();
            errorMessage = driver.findElement(By.xpath("//div[@class='money-in-out__messages']/div[contains(@class, 'money-in-out__message money-in-out__message-error')]")).getAttribute("innerText");
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
        for (WebElement minSumElement : minSumElements) {//Если нажимаем на сумму на серой ссылке, то должны отображаться бонусы и НДФЛ.
            Thread.sleep(1000);
            driver.findElement(By.xpath("//div[@class='smallJsLink__wrapper']/span[1]")).click(); //кликаем на сумму на ссылке
            Thread.sleep(1000);
            waitToPreloader();
            minSumElement.findElement(By.xpath("../../label/div")).click();//кликаем на способ вывода
            waitToPreloader();
            workWithPreloader();
            try {
                ndfl = driver.findElement(By.xpath("//div[contains(@class,'money-in-out__line')]/p/span")).getAttribute("innerText");
                LOG.info("НДФЛ [" + ndfl + "]");
            } catch (Exception e) {
                throw new AutotestError("Ошибка! Не найден НДФЛ");
            }
            sumOnButton = driver.findElement(By.xpath("//button[@type = 'submit']/span[1]/span[1]")).getAttribute("innerText").replace(" ", "").replace(",", ".");
            LOG.info("Cумма на кнопке [" + sumOnButton + "]");//сумма на кнопке
            linkBalance = driver.findElement(By.xpath("//div[@class='smallJsLink__wrapper']/span[1]")).getAttribute("innerText").replace(" ", "");//сумма баланса на ссылке;
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


   @ActionTitle("переходит на страницу 'ЦУПИС'")
   public void goToPageTSUPIS(){
        WebDriver driver = PageFactory.getWebDriver();
       Set<String> allHandles = driver.getWindowHandles();
       LOG.info("Переходим на страницу ЦУПИС");
       driver.switchTo().window(allHandles.toArray()[1].toString());
   }

    @ActionTitle("открывает попап через кнопку 'Внести депозит'")
    public void deposit() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        LOG.info("Обновляем страницу");
        driver.navigate().refresh();
        CommonStepDefs.workWithPreloader();
        Thread.sleep(2000);
        LOG.info("Открываем попап через кнопку #Внести депозит#");
        driver.findElement(By.id("btn-header-deposit")).click();
        WebElement field = Stash.getValue("fieldKey");
        int sumField = Stash.getValue("sumFieldKey");
        Thread.sleep(1000);
        if (!driver.findElement(By.xpath("//div[@class='modal__body modal__body_moneyInOutBox money-in-out__form']")).isDisplayed()){
            Assertions.fail("При нажатии на кнопку Внести депозит не открылся попап пополнения");
        }
        int randomSum = 100 + (int) (Math.random() * 900);//рандомное число до от 100 до 1000
        field = driver.findElement(By.id("money_in_amount1"));
        LOG.info("Очищаем поле с суммой и затем вводим туда " + randomSum);
        field.clear();
        field.sendKeys(String.valueOf(randomSum));
        Thread.sleep(5000);
        sumField = Integer.valueOf(driver.findElement(By.id("money_in_amount1")).getAttribute("value"));
        if (sumField != randomSum) {
            Assertions.fail("На кнопке неправильная сумма." + sumField + " вместо " + randomSum);
        }
    }



}




