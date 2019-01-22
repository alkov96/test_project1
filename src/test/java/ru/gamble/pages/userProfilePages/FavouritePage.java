package ru.gamble.pages.userProfilePages;


import org.assertj.core.api.Assertions;
import org.junit.Assert;
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
import ru.gamble.pages.livePages.VewingEventsPage;
import ru.gamble.pages.prematchPages.EventViewerPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.openqa.selenium.By.xpath;

@PageEntry(title = "Избранное")
public class FavouritePage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(FavouritePage.class);

    //@FindBy(id = "elected")
    @FindBy(xpath = "//div[contains(@class,'subMenuArea_fullwidth') and contains(@class,'active')]")
    private WebElement pageTitle;


    public FavouritePage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
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
               org.assertj.core.api.Assertions.fail("Не удалилась игра из избранного через верхнее меню");
            }
            sizeFavourite = sizeFavouriteUpdate;
        }
    }


    @ActionTitle("сравнивает названия событий на странице и в избранном")
    public void compareEventsAndFav(){
        WebDriver driver = PageFactory.getDriver();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'elected__teams ellipsis-text')]")));
        String electedGame = driver.findElement(By.xpath("//div[contains(@class,'elected__teams ellipsis-text')]")).getAttribute("title");  //игра в избранном
        String team1name = Stash.getValue("team1nameKey");
        String team2name = Stash.getValue("team2nameKey");
        if (!CommonStepDefs.stringParse(team1name+team2name).equals(CommonStepDefs.stringParse(electedGame))) {
            fail("Названия команд в списке и в блоке Избранное не совпадают: " + team1name+"-"+team2name +"="+electedGame);
        }
        LOG.info("Названия команд в списке и в блоке Избранное совпадают: " + team1name+"-"+team2name +"="+electedGame);

    }

    @ActionTitle("проверяет что переходы с игр из Избранного работают верно")
    public void goFromFavourite(){
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;
        String period = Stash.getValue("keyPeriod");
        List<WebElement> allMyGames = driver.findElements(By.xpath("//div[contains(@class,'elected-box-scroll')]//div[@game='game']"));
        List<String> names = Stash.getValue("nameGameKey");
        List<String> teams = new ArrayList<>();
        names.forEach(name -> teams.add(CommonStepDefs.stringParse(name)));
        List<String> types = Stash.getValue("typeGameKey");
        LOG.info("Переход из избранного на игры");
        for (int MyGameN = 0; MyGameN < allMyGames.size(); MyGameN++) {
            String nameMyGame = allMyGames.get(MyGameN).findElement(By.xpath("div[1]//div[contains(@class,'elected__teams')]")).getAttribute("title");
            int index;
            index = teams.indexOf(CommonStepDefs.stringParse(nameMyGame));
            Assert.assertFalse(
                    "Игра в Избранное не добавлялась. Игра [" + CommonStepDefs.stringParse(nameMyGame) + "], а в Избранном [" + teams + "]",
                    index<0
            );

            LOG.info("game is " + nameMyGame);
//переходим на игру из Избранного и ждем загрузки страницы
            //driver.findElement(By.xpath("//*[@id='private_panel']/li[3]/div[1]/div[1]/div[2]/div[" + (MyGameN + 1) + "]")).click();
            driver.findElements(By.xpath("//div[contains(@class,'elected-box-scroll')]//div[@game='game']//div[contains(@class,'elected__teams')]")).get(MyGameN).click();
            CommonStepDefs.workWithPreloader();
            //   switch(typeGame){
            switch (types.get(index)) {
                case "PrematchVnePeriod":
                    flag=EventViewerPage.pagePrematch(teams.get(index), "Любое время",true);
                    Assert.assertTrue("Переход на игру " + teams.get(index) +" не удался", flag);
                    break;
                case "PrematchInPeriod":
                    flag=EventViewerPage.pagePrematch(teams.get(index),  period,true);
                    Assert.assertTrue("Переход на игру " + teams.get(index) +" не удался", flag);
                    break;
                case "LiveWithVideo":
                    flag=VewingEventsPage.pageLive(teams.get(index),true,true);
                    Assert.assertTrue("Переход на игру " + teams.get(index) +" не удался", flag);
                    break;
                case "LiveWithoutVideo":
                    flag=VewingEventsPage.pageLive(teams.get(index),  true,true);
                    Assert.assertTrue("Переход на игру " + teams.get(index) +" не удался", flag);
                    break;
                default:
                    fail("В избранном игра, для которой не сохранился тип");
                    break;
            }
            driver.findElement(By.xpath("//*[@id='elected']")).click();
            CommonStepDefs.workWithPreloader();
        }
    }



    @ActionTitle("запоминаем название команд в избранном")
    public void rememberTeamsName(String team1key, String team2key){
        WebDriver driver = PageFactory.getDriver();
        WebElement game = driver.findElement(xpath("//div[@ng-repeat='game in games']//div[contains(@class,'elected__block_data')]/div[not(contains(@class,'blocked'))]/ancestor::div[@class='elected__game']"));//первая игра в списоке игр в избранном
        String nameFavour = game.findElement(xpath("div//div[contains(@class,'elected__teams')]")).getAttribute("title");
        String team1Name = nameFavour.split(" - ")[0].trim();
        String team2Name = nameFavour.split(" - ")[1].trim();
        LOG.info("Название игры: " + team1Name + " - " + team2Name);
        Stash.put(team1key,team1Name);
        Stash.put(team2key,team2Name);
    }

    @ActionTitle("запоминаем значение коэффициента")
    public void rememberCoef(String coefKey){
        WebDriver driver = PageFactory.getDriver();
        WebElement game = driver.findElement(xpath("//div[@ng-repeat='game in games']//div[contains(@class,'elected__block_data')]/div[not(contains(@class,'blocked'))]/ancestor::div[@class='elected__game']"));//первая игра в списоке игр в избранном
        float coefFavour = Float.valueOf(game.findElement(xpath("div//div[contains(@class,'elected-data__event-price')]")).getAttribute("innerText"));
        LOG.info("Значение коэффициента выбранного исхода = " + coefKey);
        Stash.put(coefKey,coefFavour);
    }


    @ActionTitle("запоминаем название маркета")
    public void rememberMarket(String ishodKey){
        WebDriver driver = PageFactory.getDriver();
        WebElement game = driver.findElement(xpath("//div[@ng-repeat='game in games']//div[contains(@class,'elected__block_data')]/div[not(contains(@class,'blocked'))]/ancestor::div[@class='elected__game']"));//первая игра в списоке игр в избранном
        String ishod = game.findElement(xpath("div//div[contains(@class,'elected-data__event-content')]")).getAttribute("title").trim();
        LOG.info("Название исхода: " + ishod);
        Stash.put(ishodKey,ishod);
    }


    @ActionTitle("добавляет ставку в купон")
    public void addToCoupon(){
        WebDriver driver = PageFactory.getDriver();
        WebElement game = driver.findElement(xpath("//div[@ng-repeat='game in games']//div[contains(@class,'elected__block_data')]/div[not(contains(@class,'blocked'))]/ancestor::div[@class='elected__game']"));//первая игра в списоке игр в избранном
        LOG.info("Добавляем в купон ставку из Избранного");
        game.findElement(xpath("//div[contains(@class,'elected-data__event-price')]")).click();
        CommonStepDefs.workWithPreloader();
    }


    @ActionTitle("проверяет что в избранном все нужные игры")
    public void checkFavourite(){
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> allMyGames =  driver.findElements(By.xpath("//*[@id='elected']/..//div[contains(@class,'elected-box ng-scope')]/div"));
        List<String> names = Stash.getValue("nameGameKey");
        List<String> teams = new ArrayList<>();
        names.forEach(name->teams.add(CommonStepDefs.stringParse(name)));
        if(allMyGames.size() == 0){
            throw  new AutotestError("В Избранное не найдено ни одного события!");
        }
        LOG.info("Проверка что в Избранном игр не больше, чем добавлялось");
        if (teams.size() < allMyGames.size())
        {
            fail("В избранном лишние игры:" + allMyGames.size() + " вместо " + teams.size());
        }
        LOG.info("Проверка названий игр, которе оказались в Избранном по названию");
        for (WebElement gameN : allMyGames){
            String nameGameFull = gameN.findElement(By.xpath("div[1]/div[1]/div[1]/div[2]")).getAttribute("title");
            LOG.info("В избранном еть игра: " + nameGameFull );
            if (!teams.contains(CommonStepDefs.stringParse(nameGameFull))) {
                fail("В избранном неверная игра. Добавляли Игры " + teams.toString() + " а в избранном оказалась " +  CommonStepDefs.stringParse(nameGameFull));
            }
        }
    }

    @ActionTitle("переходит в настройки и меняет коэффицент в избранном")
    public void changePreferencesCoeff() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        LOG.info("переходит в настройки и меняет коэффицент");
        String previous;
        LOG.info("Нажимаем на кнопку с шетсерёнкой");
        preferences.click();
        List<WebElement> listCoeff = driver.findElements(By.xpath("//ul[@class='prefs']//span[contains(@class, 'prefs__val')]"));
        if(!listCoeff.isEmpty()){
            Thread.sleep(500);
            previous = listCoeff.get(0).getAttribute("innerText");
            AbstractPage.openFavourite();
            preferences.click();
            listCoeff.get(2).click();
            LOG.info("Переключаемся на '" + listCoeff.get(2).getAttribute("innerText") + "' формат отображения");
            AbstractPage.openFavourite();
            Thread.sleep(500);
            LOG.info("Предыдущий: " + previous + "Текущий: " + listCoeff.get(2).getAttribute("innerText"));
            if (previous.equals(listCoeff.get(2).getAttribute("innerText"))) {
                LOG.error("Формат отображения коэффициентов не изменился");
                Assertions.fail("Формат отображения коэффициентов не изменился");
            }
        }else {
            throw new AutotestError("Ошибка! Ни один коэффициент не найден.");
        }
        LOG.info("Смена форматов отображения коэффицентов прошла успешно");
    }
}

