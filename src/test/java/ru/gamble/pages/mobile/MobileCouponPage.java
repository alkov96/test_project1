package ru.gamble.pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;

@PageEntry(title = "Купон")
public class MobileCouponPage extends MobileAuthorizedMainPage{
    private static final Logger LOG = LoggerFactory.getLogger(MobileCouponPage.class);

    @FindBy(xpath = "//h1/span[contains(.,'Купон')]")
    private WebElement pageTitle;

    @ElementTitle("Удалить все")
    @FindBy(xpath = "//span/span[contains(.,'Удалить все')]")
    private WebElement deleteAllLink;

    @ElementTitle("Сумма")
    @FindBy(xpath = "//input[contains(@placeholder, 'Сумма')]")
    private WebElement amountInput;

    @ElementTitle("ЗАКЛЮЧИТЬ ПАРИ")
    @FindBy(xpath="//button[contains(.,'Заключить пари')]")
    private WebElement bettingButton;

    public MobileCouponPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(driver, this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("нажимает кнопку 'ЗАКЛЮЧИТЬ ПАРИ'")
    public void clickBettingButton(){
        bettingButton.click();
        waitingForPreloaderToDisappear(30);
    }

}
