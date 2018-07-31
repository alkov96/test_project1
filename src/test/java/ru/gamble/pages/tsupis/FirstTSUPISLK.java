package ru.gamble.pages.tsupis;

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
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.stream.Collectors;

@PageEntry(title = "Первый ЦУПИС ЛК")
public class FirstTSUPISLK extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(FirstTSUPISLK.class);

    @FindBy(xpath = "//div[@class='contract-details-content']")
    private WebElement pageTitle;

    @ElementTitle("Продолжить")
    @FindBy(xpath = "//input[contains(@value,'Продолжить')]")
    private WebElement buttonContinue;

    public FirstTSUPISLK() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("вводит случайное число в CVV")
    public void entersRandomNumberInCVV(){
        List<WebElement> cvvField = PageFactory.getDriver().findElements(By.name("cvv")).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
        if(cvvField.size() > 0) {
            LOG.info("В ЦУПИС предлагается ввести CVV карты");
            String cvv = Generators.randomNumber(3);
            LOG.info("Вводим в поле CVV::" + cvv);
            fillField(cvvField.get(0), cvv);
        }
    }

    @ActionTitle("нажимает кнопку 'Вернуться к букмекеру'")
    public void pressKeyBackToBookie(){
        PageFactory.getDriver().findElement(By.id("success")).click();

    }


}
