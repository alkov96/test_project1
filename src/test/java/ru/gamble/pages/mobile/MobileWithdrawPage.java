package ru.gamble.pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

@PageEntry(title = "Вывод")
public class MobileWithdrawPage extends MobileAuthorizedMainPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileWithdrawPage.class);

    @FindBy(xpath = "//*[contains(.,'Выберите способ вывода средств')]")
    private WebElement pageTitle;

    @ElementTitle("Сумма вывода")
    @FindBy(xpath = "//input[@class='form-input']")
    private WebElement withdrawInput;


    public MobileWithdrawPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        waitingForPreloaderToDisappear(30);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }


    @ActionTitle("заполняет поле 'Сумма вывода'")
    public void fillFielWithdrawAmount(String keySum){
        String sum = Stash.getValue(keySum);
        fillField(amountInput,sum);
        LOG.info("В поле [Сумма вывода] ввели сумму [" + amountInput.getAttribute("value") + "]");
    }
}
