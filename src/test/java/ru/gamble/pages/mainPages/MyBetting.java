package ru.gamble.pages.mainPages;

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
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

@PageEntry(title = "Мои пари")
public class MyBetting extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MyBetting.class);

    @FindBy(xpath = "//*[contains(@class,'subMenuArea')]//h4[contains(.,'Мои пари')]")
    private WebElement pageTitle;

    @ElementTitle("Фильтр по типу пари")
    @FindBy(xpath = "//div[contains(@class,'my-stakes')]//div[contains(@custom-select,'my-stakes__filter-grid_M')]")
    private WebElement filterByTypeOfBid;

    @ElementTitle("Фильтр по дате пари")
    @FindBy(xpath = "//div[contains(@class,'input888wrpr')]")
    private WebElement filterByDateOfBid;

    public MyBetting() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("проверяет фильтры по типу ставки с")
    public void checksFiltersByTypeOfBidWith(DataTable dataTable){
        String filterXpath = "//*[@class='input888wrpr']//custom-select";
        WebDriver driver = PageFactory.getWebDriver();
        WebElement select;
        List<String> data = dataTable.asList(String.class);
        try {
            select = driver.findElements(By.xpath(filterXpath)).get(0);
        }catch (Exception e){
            throw new AutotestError("Ошибка! Выпадающее меню не найдено.");
        }
        LOG.info("Открываем фильтр по типу пари.");
        select.click();
        new WebDriverWait(driver,2);
        String actual = select.getText().replaceAll("\n", " ").toLowerCase();
        for(String existed: data){
            assertThat(actual).as("Строка [" + actual + "] не соответсвует [" + existed + "]").contains(existed);
        }
        LOG.info("Закрываем фильтр по типу пари.");
        select.click();
    }


}
