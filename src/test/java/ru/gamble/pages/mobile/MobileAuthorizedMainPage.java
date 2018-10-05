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

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@PageEntry(title = "Авторизованная мобильная главная страница")
public class MobileAuthorizedMainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileAuthorizedMainPage.class);

    @ElementTitle("Баланс")
    @FindBy(xpath = "//a[@href='/private/balance/deposit']")
    private WebElement deposit;

    @ElementTitle("Бонус")
    @FindBy(xpath = "//a[@href='/private/balance/bonus']")
    private WebElement bonus;

    @ElementTitle("Далее")
    @FindBy(xpath = "//button[contains(.,'Далее')]")
    protected WebElement nextButton;

    @ElementTitle("Сумма")
    @FindBy(xpath="//input[@class='form-input']")
    protected WebElement amountInput;

    @ElementTitle("VISA_МИР")
    @FindBy(xpath = "//span[@class='btn btn_default btn_flex btn_checkbox btn_large']")
    protected WebElement visaMirButton;


    public MobileAuthorizedMainPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(driver, this);
        waitingForPreloaderToDisappear(30);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(deposit));
    }

    @ActionTitle("записываем значение баланса в")
    public void writeBalanceIn(String balanceKey){
        String value = deposit.getAttribute("innerText");
        Stash.put(balanceKey, value);
        LOG.info("Сохранили в память key [" + balanceKey + "] <== value [" + value + "]");
    }

    @ActionTitle("проверяет увеличение баланса")
    public void checksIncreaseBalanceOn(String keyOldBalance, String keySum){
        String oldBalance = Stash.getValue(keyOldBalance);
        String sum = Stash.getValue(keySum.trim());

        String newBalance = deposit.getAttribute("innerText").replaceAll("\\s","").trim();
        BigDecimal expectedAmount = new BigDecimal(oldBalance.replaceAll("\\s","").trim()).setScale(2, RoundingMode.HALF_UP)
                .add(new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP));
        BigDecimal actualAmount = new BigDecimal(newBalance).setScale(2, RoundingMode.HALF_UP);

        assertThat(expectedAmount.compareTo(actualAmount) == 0)
                .as("Ожидаемый баланс [" + expectedAmount.toString() + "] не соответсвует действительному [" + actualAmount.toString() + "]").isTrue();
        LOG.info("Ожидаемый баланс [" + expectedAmount.toString() + "] соответсвует действительному [" + actualAmount.toString() + "]");
    }

    @ActionTitle("записываем значение бонусов в")
    public void writeBonusIn(String bonusKey){
        String value = deposit.getAttribute("innerText");
        Stash.put(bonusKey, value);
        LOG.info("Сохранили в память key [" + bonusKey + "] <== value [" + value + "]");
    }

    @ActionTitle("проверяет уменьшение баланса")
    public void checksDecreaseBalanceOn(String keyOldBalance, String keySum){
        String oldBalance = Stash.getValue(keyOldBalance);
        String sum = Stash.getValue(keySum.trim());

        String newBalance = deposit.getAttribute("innerText").replaceAll("\\s","").trim();
        BigDecimal expectedAmount = new BigDecimal(oldBalance.replaceAll("\\s","").trim()).setScale(2, RoundingMode.HALF_UP)
                .subtract(new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP)).abs();
        BigDecimal actualAmount = new BigDecimal(newBalance).setScale(2, RoundingMode.HALF_UP);

        assertThat(expectedAmount.compareTo(actualAmount) == 0)
                .as("Ожидаемый баланс [" + expectedAmount.toString() + "] не соответсвует действительному [" + actualAmount.toString() + "]").isTrue();
        LOG.info("Ожидаемый баланс [" + expectedAmount.toString() + "] соответсвует действительному [" + actualAmount.toString() + "]");
    }

}
