package ru.gamble.pages;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.livePages.VewingEventsPage;
import ru.gamble.pages.prematchPages.EventViewerPage;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import sun.util.resources.cldr.CalendarData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.openqa.selenium.By.xpath;
import static ru.gamble.stepdefs.CommonStepDefs.stringParse;

@PageEntry(title = "Поиск в левом меню")
public class SearchPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(EventViewerPage.class);

    @FindBy(id = "search-bar")
    private WebElement searchField;

    public SearchPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.elementToBeClickable(searchField));

    }

    @ActionTitle("вводит в поле поиска текст")
    public void inputSearchPattern(String key){
        LOG.info("Поиск по подстроке '" + Stash.getValue(key).toString() + "'");
        searchField.clear();
        searchField.sendKeys(Stash.getValue(key).toString());


    }

    /**
     * функция определения является ли игра в результатх поиска Лайв - или Прематч
     * @param numberRes - какой по счету пункт в результатах поиска рассматирвается
     * @return возвращает тип игры.1-лайв. 0-прематч
     * @throws Exception
     */
    public int checkTypeGameOnSearch(int numberRes) {
        WebDriver driver = PageFactory.getDriver();
        int typeGame;//1-live; 0-prematch
        new WebDriverWait(driver,15)
                .withMessage("Нет вообще никаких результатов поиска. Ни правильнх, ни неправильных")
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(xpath("//dl[contains(@class,'search-result-item')]/dd"),0));
        List <WebElement> searchResult = driver.findElements(xpath("//dl[contains(@class,'search-result-item')]/dd"));
        String whenGame = searchResult.get(numberRes).findElement(xpath("div/div[contains(@class,'result-search__competition-name')]")).getAttribute("title");

        boolean timeLive=false;
        int indexTime = whenGame.indexOf(":");
        String timeGame = whenGame.substring(indexTime-2,indexTime+3);

        Calendar cal = new GregorianCalendar();
        Calendar today = new GregorianCalendar();

        SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        try {
            timeLive =formatTime.parse(formatTime.format(today.getTime())).getTime() >= formatTime.parse(timeGame).getTime();//время игры (только время, без учет даты) меньше или совпадает с текущим?
        }
        catch (ParseException e){
            Assertions.fail("Не удалось распарсить время");
        }

        if (whenGame.contains("Сегодня")){
            return timeLive?1:0;
        }
        if (whenGame.contains("Завтра")){
            return 0;
        }
        int a = whenGame.indexOf(":");
        String day = whenGame.substring(a+6);//строка с датой игры



        try {
            cal.setTime(formatDate.parse(day));

            if(cal.before(today)) {
                return 1;
            }


        } catch (ParseException e) {
            Assertions.fail("Не удалось распарсить дату \n" + e.toString());
        }

        return 0;
    }

    /**
     * функция выбора в результатх поиска игры по названию
     * game - игра с запомненым названием
     * typeGame - игра из какого раздела. 0-прематч. 1-лайв
     * return - возвращает номер позиции в списке результатов
     */

    @ActionTitle("ищет игру в результатах поиска")
    public void searchByName (String type, String keyIndex) {


        int number = searchByNameStep(type,0);
        LOG.info("В результатх поиска нужная игра на позиции " + number);
        Stash.put(keyIndex,number); //в стэш положим порядковый номер результата в поиске, по которому нужная игра
    }


    public int searchByNameStep (String type,int step) {
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        WebDriver driver = PageFactory.getDriver();
        waitForElementPresent(xpath("//dl[contains(@class,'search-result-item')]/dd"),10);
        int typeGame = type.contains("Live")?1:0;
        List<WebElement> searchResult = driver.findElements(xpath("//dl[contains(@class,'search-result-item')]/dd"));//список игр в результате поиска
        List <String> types = Stash.getValue("typeGameKey");
        int index = types.indexOf(type);
        List<String> games = Stash.getValue("nameGameKey");
        String gameName = stringParse(games.get(index));
        LOG.info("В результатх поиска будем выбирать игру " + gameName);
        int number=-1;
        for (int i=step; i<searchResult.size();i++){
            LOG.info("В поиске есть результат: " + searchResult.get(i).findElement(By.xpath("div/span")).getText());
            if (stringParse(searchResult.get(i).findElement(By.xpath("div/span")).getText()).equals(gameName)){//если в результатах поиска название игры совпадает с искомым
                //number=searchResult.indexOf(searchResult.get(i));//то запоминаем номер в списке результатов
                number = i;
                break;
            }
        }

        Assert.assertFalse("Нет ожидаемой игры в результатах поиска: " + gameName,number==-1);//нет подходящих игр -значит наша игра через поиск не нашлась - ошибка

        String whenGame = searchResult.get(number).findElement(xpath("div/div[contains(@class,'result-search__competition-name')]")).getAttribute("title");//время в результатх поиска для игры по номеру number
        int indexTime = whenGame.indexOf(":");
        String time = whenGame.substring(indexTime-2,indexTime+3);

        switch (typeGame){
            case 1://1- лайв. значит нужно сравнивать просто тип игры
                if (checkTypeGameOnSearch(number)!=typeGame){
                    number = searchByNameStep(type,number+1);//если не совпало, то нужно посмотреть остальные результаты поиска - вызываем эту же функию но от другого step уже
                }
                break;
            case 0://0-прематч, значит нужно сравнивать тип игры и время
                String timeGame = Stash.getValue("timeGameKey").toString();
                int a = timeGame.indexOf(":");
                if (checkTypeGameOnSearch(number)!=typeGame || !time.equals(timeGame.substring(a-2,a+3))){
                    LOG.info("тип игры совпадает? " + (checkTypeGameOnSearch(number)==typeGame));
                    LOG.info("начало игры совпадает? " + time + "  " + (timeGame.substring(a-2,a+3)));
                    number = searchByNameStep(type,number+1);//аналогично case 1
                }
                break;
        }

        return number;
    }


    @ActionTitle("выбирает пункт из результата поиска")
    public void clickOnResultSearchByIndex(String keyIndex){
        WebDriver driver = PageFactory.getDriver();
        int index = Stash.getValue(keyIndex);
        driver.findElements(xpath("//dl[contains(@class,'search-result-item')]/dd")).get(index).click();
        LOG.info("Нажимаем на пункт " + (index+1) + " в результатх поиска");
    }


    @ActionTitle("Проверка перехода на игру через Поиск")
    public void checkGameLive(String type){
        LOG.info("Проверяем что открылась правильная игра");
        List <String> types = Stash.getValue("typeGameKey");
        int index = types.indexOf(type);
        List<String> games = Stash.getValue("nameGameKey");
        String gameName = stringParse(games.get(index));
        //   switch(typeGame){
        switch (type) {
            case "LiveWithVideo":
                Assert.assertTrue("Неполадки при переходе на лайв-игру",VewingEventsPage.pageLive(gameName,true, false));
                break;
            case "LiveWithoutVideo":
                Assert.assertTrue("Неполадки при переходе на лайв-игру",VewingEventsPage.pageLive(gameName,  false,false));
                break;
            case "PrematchVnePeriod":
                Assert.assertTrue("Неполадки при переходе на лайв-игру",EventViewerPage.pagePrematch(gameName, "Любое время",false));
                break;
            case "PrematchInPeriod":
                Assert.assertTrue("Неполадки при переходе на лайв-игру",EventViewerPage.pagePrematch(gameName,  "2 часа",false));
                break;
            default:
                LOG.error("Непонятно что за игра(с видео или нет). Проверять не буду");
                break;
        }

    }


}

