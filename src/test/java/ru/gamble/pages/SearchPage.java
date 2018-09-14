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
import ru.gamble.pages.prematchPages.EventViewerPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.openqa.selenium.By.xpath;
import static ru.gamble.stepdefs.CommonStepDefs.stringParse;

@PageEntry(title = "Поиск в левом меню")
public class SearchPage extends AbstractPage{
    private static final Logger LOG = LoggerFactory.getLogger(EventViewerPage.class);

    @FindBy(id = "search-bar")
    private WebElement searchField;

    public SearchPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(PageFactory.getDriver(), 10).until(ExpectedConditions.elementToBeClickable(searchField));

    }

    @ActionTitle("вводит в поле поиска текст")
    public void inputSearchPattern(String pattern){

        searchField.clear();
        searchField.sendKeys(pattern);


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
        List <WebElement> searchResult = driver.findElements(xpath("//dl[contains(@class,'search-result-item')]/dd"));
        String whenGame = searchResult.get(numberRes).findElement(xpath("div/div[contains(@class,'result-search__competition-name')]")).getAttribute("title");

        boolean timeLive=false;
        int indexTime = whenGame.indexOf(":");
        String timeGame = whenGame.substring(indexTime-2,indexTime+3);

        Date dateNow = new Date(System.currentTimeMillis());//текущие время и дата
        int yearToday = dateNow.getYear();
        int monthToday = dateNow.getMonth();
        int dayToday = dateNow.getDay();

        SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        try {
            timeLive = formatTime.parse(formatTime.format(dateNow)).getTime() >= formatTime.parse(timeGame).getTime();//время игры (только время, без учет даты) меньше или совпадает с текущим?
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
            Date dateGame = formatDate.parse(day);//дата игры
            if ((dateGame.getYear()==yearToday && dateGame.getMonth() == monthToday && dateGame.getDay() == dayToday) && timeLive){
                return 1;
            }
        }
        catch (ParseException e) {
            Assertions.fail("Не удалось распарсить дату");
        }

        return 0;
    }

    /**
     * функция выбора в результатх поиска игры по названию
     * game - игра с запомненым названием
     * typeGame - игра из какого раздела. 0-прематч. 1-лайв
     * return - возвращает номер позиции в списке результатов
     */


    public int searchByName (TeamEntry game, int typeGame) {
        return searchByName (game,typeGame,0);
    }

    public int searchByName (TeamEntry game, int typeGame,int step) {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> searchResult = driver.findElements(xpath("//dl[contains(@class,'search-result-item')]/dd"));//список игр в результате поиска

        int number=-1;
        for (int i=step; i<searchResult.size();i++){
            if (stringParse(searchResult.get(i).findElement(By.xpath("div/span")).getText()).equals(stringParse(game.getFullName()))){//если в результатах поиска название игры совпадает с искомым
                number=searchResult.indexOf(searchResult.get(i));//то запоминаем номер в списке результатов
                break;
            }
        }

        Assert.assertNotEquals(number, -1);//нет подходящих игр -значит наша игра через поиск не нашлась - ошибка

        String whenGame = searchResult.get(number).findElement(xpath("div/div[contains(@class,'result-search__competition-name')]")).getAttribute("title");//время в результатх поиска для игры по номеру number
        int indexTime = whenGame.indexOf(":");
        String time = whenGame.substring(indexTime-2,indexTime+3);

        switch (typeGame){
            case 1://1- лайв. значит нужно сравнивать просто типа игры
                if (checkTypeGameOnSearch(number)!=typeGame){
                    searchByName(game,typeGame,number+1);//если не совпало, то нужно посмотреть остальные результаты поиска - вызываем эту же функию но от другого step уже
                }
                break;
            case 0://0-прематч, значит нужно сравнивать тип игры и время
                if (checkTypeGameOnSearch(number)!=typeGame || !time.equals(game.getTimeGame().substring(0,5))){
                    searchByName(game,typeGame,number+1);//аналогично case 1
                }
                break;
        }
        return number;
    }
}

