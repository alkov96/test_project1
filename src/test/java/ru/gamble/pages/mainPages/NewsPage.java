package ru.gamble.pages.mainPages;

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
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author p.sivak.
 * @since 10.05.2018.
 */
@PageEntry(title = "Новости")
public class NewsPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(NewsPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//div[@class='g-row newslist']")
    private WebElement newslist;

    public NewsPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(newslist));
    }

    @ActionTitle("проверяет наличие верхней линейки вкладок")
    public void checksPresenceOfTopRulerOfTabs(){
        List<WebElement> tabsNews = PageFactory.getWebDriver().findElements(By.xpath("//a[contains(@class,'newslist-')]"))
                .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
        assertThat(tabsNews.size()).as("Ошибка! Верхняя линейка вкладок не найдена").isGreaterThan(0);
        for(WebElement tab: tabsNews){
            if(!tab.getAttribute("innerText").equals("")){
                LOG.info("Найдено::" + tab.getAttribute("innerText"));
            }
        }
    }

    @ActionTitle("проверяет наличие дайжеста новостей на имеющихся вкладках")
    public void checksForNewsDigestsOnExistingTabs(){
        String xpathTabs = "//div/a[contains(@class,'newslist-categories')]";
        String xpathButtonMore = "//div[contains(@class,'categories__add-box')]";
        String xpathDigests = "//a[contains(@class,'newslist__title')]";
        String actualText;

        LOG.info("Собираем список всех верхних вкладок");
        List<WebElement> tabsNews = driver.findElements(By.xpath(xpathTabs));
        for(int i = 0; i < tabsNews.size(); i++){
            if(!tabsNews.get(i).isDisplayed()){
                LOG.info("Нажимаем кнопку [Ещё]");
                driver.findElement(By.xpath(xpathButtonMore)).click();
            }
            LOG.info("Собираем список всех дайджестов на вкладке::" + tabsNews.get(i).getAttribute("innerText"));
            tabsNews.get(i).click();
            //Для ожидание прогрузки
            workWithPreloader();
            List<WebElement> dagestsNews = driver.findElements(By.xpath(xpathDigests))
                    .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
            assertThat(dagestsNews.size()).as("Ошибка! Дайджесты не найдены").isGreaterThan(0);

            for(WebElement dagest: dagestsNews){
                if(!dagest.getAttribute("innerText").equals("")){
                    actualText = dagest.getAttribute("innerText");
                    LOG.info("Найдено::" + actualText);
                }
            }
            // Так-как элементы перерисовываются
            tabsNews = driver.findElements(By.xpath(xpathTabs));
        }
    }
}
