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
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.gamble.pages.utility.Constants.PERIOD;

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
            new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.visibilityOf(expandСollapseMenusButton));
            checkMenuIsOpen();
    }

    private void checkMenuIsOpen(){
        if(expandСollapseMenusButton.getAttribute("title").contains("Показать всё")){
            expandСollapseMenusButton.click();
        }
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
        if(leftOpenMenuArrows.size()>0) {
            openArrowAndCheckGameTime(leftOpenMenuArrows, valuePeriod);
        }

//        List<WebElement> leftOpenMenuArrows = PageFactory.getDriver().findElements(By.xpath("//li[@class = 'left-menu__list-item-sport ng-scope active']"));
//        if(leftOpenMenuArrows.size()>0){
//            for (WebElement openArrow:leftOpenMenuArrows) {
//                List<WebElement> countriesList = openArrow.findElements(By.xpath("//left-menu__list-item-region ng-scope"));
//                if(countriesList.size()>0){
//                    for (WebElement country: countriesList){
//                        country.click();
//                        List<WebElement> gamesList = PageFactory.getWebDriver().findElements(By.className("ng-binding"));
//                        if (gamesList.size()>0){
//                            for (WebElement game:gamesList) {
//                                game.click();
//                                rowDateTame = PageFactory.getWebDriver().findElement(By.xpath("//div[@class='prematch-competition-games__item-date ng-binding']")).getText();
//                            }
//                        }
//                    }
//                }
//            }
//        }

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
            for (WebElement openArrow:list) {
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

//    private void clickOnGameAndCheckDateTime(List<WebElement> gamesList,String xpath1, String expectedValue){
//        String xpath = "//left-menu__list-item-region ng-scope";
//        String rowDateTame = "";
//        for (WebElement openArrow:gamesList) {
//            List<WebElement> countriesList = openArrow.findElements(By.xpath(xpath));
//            if(countriesList.size()>0){
//                for (WebElement country: countriesList){
//                    country.click();
//                    List<WebElement> gamesList = PageFactory.getWebDriver().findElements(By.className("ng-binding"));
//                    if (gamesList.size()>0){
//                        for (WebElement game:gamesList) {
//                            game.click();
//                            rowDateTame = PageFactory.getWebDriver().findElement(By.xpath("//div[@class='prematch-competition-games__item-date ng-binding']")).getText();
//                        }
//                    }
//                }
//            }
//        }
//
//    }

//    private void checkDateTime(int diapason, String currentGameDateTime){
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - dd MMM yyyy");
//
//        Date currentDateTime = new Date(System.currentTimeMillis());
//        Date gameDateTime = formatter.parse(currentGameDateTime);
//        Date dateTimePlusPeriod = new Date(System.currentTimeMillis() + diapason * 3600 * 1000);
//
//        assertThat(gameDateTime.after(currentDateTime)&& gameDateTime.before(dateTimePlusPeriod)).as("Дата-время [] вне диапазона []");
//    }

//    private void checkDateTime(WebElement webElement) {
//        Date dateGame;
//        Date Period = new Date(System.currentTimeMillis() + hour * 3600 * 1000);
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - dd MMM yyyy");
//        try {
//            dateGame = formatter.parse(webElement.getText());
//        } catch (ParseException e) {
//            LOG.error("Не получается распарсить");
//            Assertions.fail("Проблемы с датой игры. Не получается распарсить");
//            return;
//        }
//        if ((dateGame.getTime() > Period.getTime()) && LeftMenuTriggersPrematch.boolRez) {
//
//            log.info("игра вне фильтра" + dateGame);
//            LeftMenuTriggersPrematch.boolRez = false;
//            log.info("время игры вне фильтра = " + dateGame + " а период = " + Period);
//            return;
//        }
//    }

}
