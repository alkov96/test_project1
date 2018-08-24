package ru.gamble.pages;

import org.openqa.selenium.By;
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
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@PageEntry(title = "Вывод средств")
public class WithdrawalOfFundsPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(WithdrawalOfFundsPage.class);

    @FindBy(xpath = "//div[contains(@class,'modal__title') and contains(.,'Вывод средств')]")
    private WebElement pageTitle;

    @ElementTitle("Сумма")
    @FindBy(name = "withdrawSum")
    private WebElement summInput;

    @ElementTitle("Вывести")
    @FindBy(id = "btn-submit-money-out")
    private WebElement withdrawButton;

    public WithdrawalOfFundsPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("вводит минимальную сумму вывода для карт и сохраняет в")
    public void inputMinSum(String keyMinSumm) throws InterruptedException {
        checkForErrorLoadingPaymentSystems();
        WebDriver driver = PageFactory.getWebDriver();
        Pattern pattern = Pattern.compile("(?u)[^0-9]");
        List<WebElement> allWayWithdraw = driver.findElements(By.xpath("//table[@class='moneyInOutTable']//tr[3]/td[2]/div[not(contains(@class,'not-available'))]/span/label[contains(@for, 'withdraw-method')]"));
        String min = allWayWithdraw.get(0).findElement(By.xpath("//div/span[@ng-if='method.limit.min']")).getText();

        String xpathCard = "//label[@for='withdraw-method-cupis_card']";

        LOG.info("Ищем способ 'VISA/МИР' и нажимаем");
        driver.findElement(By.xpath(xpathCard)).click();

        min = pattern.matcher(min).replaceAll("");
        fillField(summInput,min);
        String tmp = summInput.getAttribute("value");
        Stash.put(keyMinSumm, tmp);
        LOG.info("Ввели и сохранили в память минимальная сумма вывода: key=>[" + keyMinSumm + "] value=>[" + tmp + "]");
        Thread.sleep(1000);}

    private void checkForErrorLoadingPaymentSystems(){
        WebDriver driver = PageFactory.getWebDriver();
        if(driver.findElements(By.xpath("//div[contains(.,'Ошибка при загрузке платежных систем')]")).stream().filter(e -> e.isDisplayed()).collect(Collectors.toList()).size() > 0){
            throw new AutotestError("Ошибка при загрузке платежных систем!");
        }
    }

}
