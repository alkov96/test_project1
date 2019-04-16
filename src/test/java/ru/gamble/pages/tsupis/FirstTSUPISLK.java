package ru.gamble.pages.tsupis;

import cucumber.api.DataTable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.utility.Generators;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageEntry(title = "Первый ЦУПИС ЛК")
public class FirstTSUPISLK extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(FirstTSUPISLK.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//div[@class='contract-details-content']")
    private WebElement pageTitle;

    @ElementTitle("Номер карты")
    @FindBy(name = "number")
    private WebElement numberCardInput;

    @ElementTitle("ММ")
    @FindBy(name = "month")
    private WebElement monthInput;

    @ElementTitle("YY")
    @FindBy(name = "year")
    private WebElement yearInput;

    @ElementTitle("Имя и фамилия латиницей")
    @FindBy(name = "name")
    private WebElement nameFamilyLatinInput;

    @ElementTitle("Продолжить")
    @FindBy(xpath = "//input[contains(@value,'Продолжить')]")
    private WebElement buttonContinue;

    @ElementTitle("Подтвердить")
    @FindBy(xpath = "//input[contains(@value,'Подтвердить')]")
    private WebElement buttonConfirm;

    @ElementTitle("Код из СМС")
    @FindBy (name = "validation")
    private WebElement inputSMSCode;

    public FirstTSUPISLK() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }


    @ActionTitle("вводит случайное число в CVV")
    public void entersRandomNumberInCVV(){
        List<WebElement> cvvField = PageFactory.getDriver().findElements(By.name("cvv")).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
        if(cvvField.size() > 0) {
            LOG.info("В ЦУПИС предлагается ввести CVV карты");
            String cvv = Generators.randomNumber(3);
            fillField(cvvField.get(0), cvv);
            LOG.info("Ввели в поле CVV [" + cvvField.get(0).getAttribute("value") + "]");
        }
    }

    @ActionTitle("нажимает кнопку 'Вернуться к букмекеру'")
    public void pressKeyBackToBookie(){
        new WebDriverWait(driver,60).until(ExpectedConditions.visibilityOfElementLocated(By.id("success")));
        driver.findElement(By.id("success")).click();
        LOG.info("Нажали на кнопку 'Вернуться к букмекеру'");

    }

    @ActionTitle("вводит содержимое в поле")
    public void userEntersContentInField(String keySMS, String elementTitle) {
            String sms = Stash.getValue(keySMS);
            LOG.info("Пытаемя ввести СМС код [" + sms + "] в поле 'Код из СМС'");
        try {
            fillField(elementTitle, sms);
        } catch (PageException e) {
           throw new AutotestError("Ошибка! Не смогли ввести СМС код [" + sms + "] в поле 'Код из СМС'");
        }
    }

    @ActionTitle("проверяет успешность операции")
    public void checksSuccessOfOperation(){
        try {
            new WebDriverWait(PageFactory.getDriver(), 5).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='icon fail']")));
        }catch (Exception e){
            throw new AutotestError("Ошибка! Операция не получилось.\n" + e.getMessage());
        }
    }

    @ActionTitle("заполняем форму банковской карты")
    public void fillOutBankCardForm(DataTable dataTable) {
        if(!driver.findElements(By.name("number")).stream().filter(WebElement::isDisplayed).collect(Collectors.toList()).isEmpty()) {
            Map<String, String> data = dataTable.asMap(String.class, String.class);
            String key = "", value = "";
            for (Map.Entry<String, String> entry : data.entrySet()) {
                key = entry.getKey();
                if (entry.getValue().matches("^[A-Z_]+$")) {
                    value = Stash.getValue(entry.getValue());
                } else {
                    value = entry.getValue();
                }

                if (key.equals("Номер карты")) {
                    fillField(numberCardInput, value);
                    LOG.info("Ввели номер карты [" + numberCardInput.getAttribute("value") + "]");
                } else if (key.equals("ММ/YY")) {
                    String[] tmp = value.split("/");
                    fillField(monthInput, tmp[0]);
                    LOG.info("Ввели в дате месяц [" + monthInput.getAttribute("value") + "]");
                    fillField(yearInput, tmp[1]);
                    LOG.info("Ввели в дате год [" + yearInput.getAttribute("value") + "]");
                } else if (key.equals("Имя и фамилия латиницей")) {
                    fillField(nameFamilyLatinInput, value);
                    LOG.info("Ввели Имя и фамилию латиницей [" + nameFamilyLatinInput.getAttribute("value") + "]");
                }
            }
        }
    }

}
