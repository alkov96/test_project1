package ru.gamble.pages.administrationPages.servicesPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author p.sivak.
 * @since 18.05.2018.
 */
@PageEntry(title = "Сервисные сообщения")
public class ServiceMessagesPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceMessagesPage.class);

    @FindBy(xpath = "//tbody[@id='gridview-1472-body']")
    private WebElement table;

    @ElementTitle("Последняя страница")
    @FindBy(xpath = "//span[@id='button-1460-btnIconEl']")
    private WebElement lastPage;

    @ElementTitle("Предыдущая страница")
    @FindBy(xpath = "//span[@id='button-1453-btnIconEl']")
    private WebElement prePage;

    @ElementTitle("Поле с количеством страниц")
    @FindBy(xpath = "//div[@id='tbtext-1457']")
    private WebElement maxPagesText;

    @ElementTitle("Добавить сообщение")
    @FindBy(xpath = "//span[@id='button-1474-btnIconEl']")
    private WebElement newMessageBotton;

    public ServiceMessagesPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(table));
    }

    @ActionTitle("очищает все активные сообщения")
    public void clearActives() {
        WebDriver driver = PageFactory.getWebDriver();
        String xpathActiveBox = "//td[contains (@class,'x-grid-cell-checkcolumn-1468')]";
        if (lastPage.isDisplayed()) {
            lastPage.click();
        }
        int pageCount = 0;
        Pattern pat = Pattern.compile("[-]?[0-9]+(.[0-9]+)?");
        Matcher matcher = pat.matcher(maxPagesText.getAttribute("innerText"));
        while (matcher.find()) {
            pageCount = Integer.parseInt(matcher.group());
        }
        for (int i = 0; i < pageCount; i++) {
            new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathActiveBox)));
            List<WebElement> activeBoxes = driver.findElements(By.xpath(xpathActiveBox));
            for (int z = 0; z < activeBoxes.size(); z++) {
                new WebDriverWait(driver, 1).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathActiveBox)));
                boolean check = false;
                while (check == false){
                    try {
                        activeBoxes.get(z).findElement(By.xpath("div/img"));
                        check = true;
                    } catch (Exception e){
                        check = false;
                        activeBoxes = driver.findElements(By.xpath(xpathActiveBox));
                    }
                }
                if (activeBoxes.get(z).findElement(By.xpath("div/img")).getAttribute("class").contains("checked")) {
                    PageFactory.getActions().doubleClick(activeBoxes.get(z)).build().perform();
                    String activeBottomId = PageFactory.getWebDriver().findElement(By.xpath("//table[contains(@class,'x-form-cb-checked')]")).getAttribute("id");
                    PageFactory.getWebDriver().findElement(By.xpath("//input[@id='" + activeBottomId + "-inputEl']")).click();
                    PageFactory.getWebDriver().findElement(By.xpath("//a[@class='x-btn x-unselectable x-box-item x-toolbar-item x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon']")).click();
                    activeBoxes = PageFactory.getWebDriver().findElements(By.xpath(xpathActiveBox));
                }
            }
            if (prePage.isDisplayed()) {
                prePage.click();
            }
        }
    }
}
