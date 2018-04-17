package ru.gamble.pages;

import lombok.extern.slf4j.Slf4j;
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
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;

import static ru.gamble.pages.utility.Constants.PERIOD;

@Slf4j
@PageEntry(title = "Просмотр событий")
public class EventViewerPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(EventViewerPage.class);

    @FindBy(xpath = "//div[contains(@class,'menu-toggler')]")
    private WebElement expandСollapseMenusButton;

    @ElementTitle("Период времени")
    @FindBy(xpath = "//div[contains(@class,'periods__input')]")
    private WebElement selectPeriod;


    public EventViewerPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(expandСollapseMenusButton));
    }

    @ActionTitle("выбирает время")
    public void chooseTime(String key){
        String value = "";
        if(key.equals(PERIOD)){
            value = Stash.getValue(key);
        }else {value = key;}

        selectPeriod.click();
        selectPeriod.findElement(By.xpath("//*[contains(text(),'" + value + "')]")).click();
    }

    @ActionTitle("проверяет время игр и")
    public void checkGamesWithPeriod(String param){
        String valuePeriod = "";
        String rowDateTame = "";
        if(param.equals(PERIOD)){
            valuePeriod = Stash.getValue(param);
        }else{
            valuePeriod = param;
        }

        List<WebElement> leftOpenMenuArrows = PageFactory.getDriver().findElements(By.xpath("//li[@class = 'left-menu__list-item-sport ng-scope active']"));
        if(leftOpenMenuArrows.size()>0){
            for (WebElement openArrow:leftOpenMenuArrows) {
                List<WebElement> countriesList = openArrow.findElements(By.xpath("//left-menu__list-item-region ng-scope"));
                if(countriesList.size()>0){
                    for (WebElement country: countriesList){
                        country.click();
                        List<WebElement> gamesList = PageFactory.getWebDriver().findElements(By.className("ng-binding"));
                        if (gamesList.size()>0){
                            for (WebElement game:gamesList) {
                                game.click();
                                rowDateTame = PageFactory.getWebDriver().findElement(By.xpath("//div[@class='prematch-competition-games__item-date ng-binding']")).getText();
                            }
                        }
                    }
                }
            }
        }

        List<WebElement> leftMenuArrows = PageFactory.getWebDriver().findElements(By.xpath("//li[@class = 'left-menu__list-item-sport ng-scope']"));
        if(leftMenuArrows.size()>0){
            for (WebElement arrow: leftMenuArrows) {

                arrow.click();

            }
        }else {
        LOG.info("В период времени [" + param + "] нет игр.");
        }

    }

    private void openArrowAndCheckGameTime(List<WebElement> list, String expectedValue){
        String rowDateTame = "";
        List<WebElement> leftOpenMenuArrows = PageFactory.getDriver().findElements(By.xpath("//li[@class = 'left-menu__list-item-sport ng-scope active']"));
        if(leftOpenMenuArrows.size()>0){
            for (WebElement openArrow:leftOpenMenuArrows) {
                List<WebElement> countriesList = openArrow.findElements(By.xpath("//left-menu__list-item-region ng-scope"));
                if(countriesList.size()>0){
                    for (WebElement country: countriesList){
                        country.click();
                        List<WebElement> gamesList = PageFactory.getWebDriver().findElements(By.className("ng-binding"));
                        if (gamesList.size()>0){
                            for (WebElement game:gamesList) {
                                game.click();
                                rowDateTame = PageFactory.getWebDriver().findElement(By.xpath("//div[@class='prematch-competition-games__item-date ng-binding']")).getText();
                            }
                        }
                    }
                }
            }
        }
    }

}
