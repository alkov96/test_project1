package ru.gamble.pages.userProfilePages;


import cucumber.runtime.junit.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.fail;

@PageEntry(title = "Избранное")
public class FavouritePage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(FavouritePage.class);

    @FindBy(id = "elected")
    private WebElement pageTitle;


    public FavouritePage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    /**
     * смотрит, есть ли какие-то события в избранном, если есть, то удаляет их
     * @throws Exception
     */
    public static void clearFavouriteGames() throws Exception {
        WebDriver driver = PageFactory.getDriver();
        int x = driver.findElement(By.xpath("//*[@id='elected']")).getLocation().getX() - 50;
        int y = driver.findElement(By.xpath("//*[@id='elected']")).getLocation().getY() - 50;
        ((JavascriptExecutor) driver).executeScript("window.scroll(" + x + ","
                + y + ");");
        driver.findElement(By.xpath("//*[@id='elected']")).click();        x = driver.findElement(By.xpath("//div[contains(@class,'elected-box-scroll')]")).getLocation().getX();
        y = driver.findElement(By.xpath("//div[contains(@class,'elected-box-scroll')]")).getLocation().getY();
        ((JavascriptExecutor) driver).executeScript("window.scroll(" + x + ","
                + y + ");");
        int sizeFavouriteUpdate;
        int sizeFavourite = driver.findElements(By.xpath("//*[@id='private_panel']/li[3]/div[1]/div[1]/div[2]/div")).size();
        // for(int count=sizeFavourite; count>0; count--)
        while (sizeFavourite > 0) {
            driver.findElement(By.xpath("//*[@id='private_panel']/li[3]/div[1]/div[1]/div[2]/div[1]/span[1]")).click();
            Thread.sleep(500);

            sizeFavouriteUpdate = driver.findElements(By.xpath("//*[@id='private_panel']/li[3]/div[1]/div[1]/div[2]/div")).size();
            if (sizeFavouriteUpdate >= sizeFavourite) {
                LOG.error("Не удалилась игра из избранного через верхнее меню");
                assert false;
            }
            sizeFavourite = sizeFavouriteUpdate;
        }
    }

    @ActionTitle("сравнивает названия событий на странице и в избранном")
    public void compareEventsAndFav(){
        WebDriver driver = PageFactory.getDriver();
        driver.findElement(By.id("elected")).click();//нажали на кнопку избранного
        String electedGame = driver.findElement(By.xpath("//div[contains(@class,'elected__teams ellipsis-text')]")).getAttribute("title");  //игра в избранном
        String team1name = Stash.getValue("team1nameKey");
        String team2name = Stash.getValue("team2nameKey");
        if (!CommonStepDefs.stringParse(team1name+team2name).equals(CommonStepDefs.stringParse(electedGame))) {
            fail("Названия команд в списке и в блоке Избранное не совпадают: " + team1name+"-"+team2name +"="+electedGame);
        }
        LOG.info("Названия команд в списке и в блоке Избранное совпадают: " + team1name+"-"+team2name +"="+electedGame);

    }

}

