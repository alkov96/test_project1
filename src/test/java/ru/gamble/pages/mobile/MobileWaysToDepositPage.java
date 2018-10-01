package ru.gamble.pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

@PageEntry(title = "Способы пополнения")
public class MobileWaysToDepositPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(MobileWaysToDepositPage.class);

    @ElementTitle("Сумма депозита")
    @FindBy(xpath="//input[@class='form-input']")
    private WebElement depositAmountInput;

    @ElementTitle("VISA_МИР")
    @FindBy(xpath="//span[@class='icon icon_svg icon_svg-cupis_card_ok']")
    private WebElement visaMirButton;

    @ElementTitle("Далее")
    @FindBy(xpath = "//button[contains(.,'Далее')]")
    private WebElement nextButton;

    public MobileWaysToDepositPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        waitingForPreloaderToDisappear(30);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(depositAmountInput));
    }

    @ActionTitle("заполняет поле 'Сумма депозита'")
    public void fillFielDepositAmount(String keySum){
        String sum = Stash.getValue(keySum);
        fillField(depositAmountInput,sum);
        LOG.info("В поле [Сумма депозита] ввели сумму [" + depositAmountInput.getAttribute("value") + "]");
    }
}
