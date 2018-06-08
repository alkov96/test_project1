package ru.gamble.pages.mainPages;


import cucumber.api.DataTable;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
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
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.*;


@PageEntry(title = "Главная страница")
public class MainPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MainPage.class);

    @FindBy(xpath = "//div[@class='topLogo888']")
    private WebElement pageTitle;

    @FindBy(xpath = "//div[contains(@class,'main-slider__wrapper')]")
    private WebElement slider;

    @ElementTitle("Регистрация")
    @FindBy(id = "register")
    private WebElement registrationButton;

    @ElementTitle("Вход")
    @FindBy(id = "log-in")
    private WebElement enterButton;

    @ElementTitle("Прематч")
    @FindBy(id = "prematch")
    private WebElement prematchButton;

    @ElementTitle("Лайв")
    @FindBy(id = "live")
    private WebElement liveButton;

    @ElementTitle("Настройки")
    @FindBy(id = "preferences")
    private WebElement preferences;


    // Блок новостей
    @ElementTitle("Стрелка-вправо")
    @FindBy(xpath = "//*[contains(@class,'news-widget') and contains(@style,'visible')]/button[contains(@class,' next')]")
    private WebElement arrowRightButton;

    @ElementTitle("Стрелка-влево")
    @FindBy(xpath = "//*[contains(@class,'news-widget') and contains(@style,'visible')]/button[contains(@class,'previous')]")
    private WebElement arrowLeftButton;

    @ElementTitle("Новости")
    @FindBy(xpath = "//a[contains(@class,'bets-widget') and contains(.,'Новости')]")
    private WebElement newsButton;

    @ElementTitle("Анонсы")
    @FindBy(xpath = "//a[contains(@class,'bets-widget') and contains(.,'Анонсы')]")
    private WebElement announceButton;

    public MainPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        tryingLoadPage(pageTitle,3);
        workWithPreloader();
        tryingLoadPage(slider,3);
        workWithPreloader();
    }

    @ActionTitle("переключение видов спорта")
    public void checkChangeSport(String widget) {
        String path;
        switch (widget) {
            case "Горячие ставки":
                path = "//div[@class='bets-widget lastMinutesBets']";
                break;
            default:
                path = "//div[@class='bets-widget nearestBroadcasts']";
                break;
        }

        //boolean flag = true;
        // DemoSingleton allError = DemoSingleton.getInstance();
        WebDriver driver = PageFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver,10);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        CommonStepDefs.waitOfPreloader();
        LOG.info("Смотрим что страницы в виджете переключаются и содержимое контейнера соответсвует выбранному виду спорта");
        String sportName;
        //    List<WebElement> allSport = driver.findElements(By.xpath("//div[contains(@class,'nearestBroadcasts')]//li[contains(@class,'sport-tabs__item') and not(contains(@class,'no-link'))]"));
        List<WebElement> allSport = driver.findElements(By.xpath(path + "//li[contains(@class,'sport-tabs__item') and not(contains(@class,'no-link'))]"));

        for (WebElement selectSport : allSport) {
            selectSport.click();
            sportName = selectSport.findElement(By.xpath("i")).getAttribute("class").replace("sport-tabs__icon sport-icon icon-", "");
            LOG.info(sportName);
            path.toString();
            wait.until(CommonStepDefs.attributeContainsLowerCase(
                    By.xpath(path + "//div[contains(@class,'bets-widget-table__inner')]"),"class",sportName));

            if (driver.findElements(By.xpath(path + "//div[contains(@class,'bets-widget-table__inner')]/table[1]/tbody/tr")).size() == 1) {
                LOG.error("В ближайших трансляциях есть вкладка спорта " + sportName + ", но список для него пустой");
            }
        }

    }

    /**
     * поиск на виджете Ближайшие трансляции игры с (без) кнопки Смотреть. Если игры по кнопк не найдено, то берется просто первая попавшаяся
     * @param param - параметр, указывающий ищем ли мы игру с кнопкой, или без
     * в Stash сохраняет найденную игру. ключ - "gameBT"
     */
    @ActionTitle("ищет игру на БТ")
    public  void searchVideoGameBT(String param) throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        boolean haveButton = param.equals("с кнопкой Смотреть")?true:false;
        String ngclick = new String();
        List<WebElement> games = new ArrayList<>();
        if (haveButton) {
            ngclick = "button";
        } else {
            {
                ngclick = "command";
            }
        }
        CommonStepDefs.waitOfPreloader();
        int x = driver.findElement(By.xpath("//div[contains(@class,'nearestBroadcasts')]//li[contains(@class,'sport-tabs__item')]/../li[last()]")).getLocation().getX();
        int y = driver.findElement(By.xpath("//div[contains(@class,'nearestBroadcasts')]//li[contains(@class,'sport-tabs__item')]/../li[last()]")).getLocation().getY();
        CommonStepDefs.scrollPage(x, y);
        List<WebElement> allSport = driver.findElements(By.xpath("//div[contains(@class,'nearestBroadcasts')]//li[contains(@class,'sport-tabs__item')]"));//все вид спортов на виджете БТ
        int number = 0;
        do {
            games = driver.findElements(By.xpath("//div[@class='bets-widget nearestBroadcasts']/div[2]/div[1]/table[1]/tbody/tr/td[position()=1 and contains(@ng-click,'" + ngclick + "')]"));
            if (!games.isEmpty()) {
                break;
            }
            allSport.get(number).click();

            CommonStepDefs.workWithPreloader();

            number++;
        } while (number <= allSport.size() - 1);
        if (games.isEmpty()){
            LOG.info("Подходящей игры не найдено. Придется брать просто первую попавшуюся из БТ");
            games = driver.findElements(By.xpath("//div[@class='bets-widget nearestBroadcasts']/div[2]/div[1]/table[1]/tbody/tr/td[position()=1 and @ng-click]"));
            haveButton = !haveButton;
        }else {
            LOG.info("Игра " + param + " найдена ");
        }
        Stash.put("gameBT",games.get(0).findElement(By.xpath("ancestor::tr")));
        Stash.put("haveButtonKey",haveButton);
    }

    //переходит на игру нажатием на название первой команды в виджете
    @ActionTitle("переходит на игру из виджета БТ")
    public void lala(){
        WebDriver driver = PageFactory.getDriver();
        WebElement selectGame = Stash.getValue("gameBT");
        //запоминаем названия команд
        String team1 = selectGame.findElement(By.xpath("td[contains(@class,'bets-item_who1')]/div[1]")).getAttribute("title").trim();
        String team2 = selectGame.findElement(By.xpath("td[contains(@class,'bets-item_who2')]/div[1]")).getAttribute("title").trim();
        String sportName = selectGame.findElement(By.xpath("ancestor::div[contains(@class,'bets-widget-table__inner active')]")).getAttribute("class").split("active-")[1].toLowerCase();
        LOG.info("Игра, на которой будем проверять переход из виджета БТ: " + team1 + " - " + team2 + ". Спорт - " + sportName);
        selectGame.findElement(By.xpath("td[contains(@class,'bets-item_who1')]/div[1]")).click();
        Stash.put("team1BTkey",team1);
        Stash.put("team2BTkey",team2);
        Stash.put("sportKey",sportName);
    }


    @ActionTitle("проверяет что переход удался")
    public void openGame() throws Exception {
        CommonStepDefs commonStepDefs = new CommonStepDefs();
        commonStepDefs.checkLinkToGame();
    }

    //добавление коэфа победы первой команды в виджете БТ
    @ActionTitle("добавляет коэф с виджета в купон")
    public void addToCouponFromBT(String widget){
        String path;
        switch (widget) {
            case "Горячие ставки":
                path = "//div[contains(@class,'lastMinutesBets')]";
                break;
            default:
                path = "//div[contains(@class,'nearestBroadcasts')]";
                break;
        }
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> games = new ArrayList<>();
        List<WebElement> allSport = driver.findElements(By.xpath(path + "//li[contains(@class,'sport-tabs__item')]"));//все вид спортов на виджете
        int number = 0;
        do {
            games = driver.findElements(By.xpath(path + "/div[2]/div[1]/table[1]/tbody/tr/td[contains(@class,'bets-item_k1')]/div[not(contains(@class,'blocked'))]"));
            if (!games.isEmpty()) {
                break;
            }
            allSport.get(number).click();

            CommonStepDefs.workWithPreloader();

            number++;
        } while (number <= allSport.size() - 1);

        WebElement selectGame = games.get(0).findElement(By.xpath("ancestor::tr"));
        String team1 = selectGame.findElement(By.xpath("td[contains(@class,'bets-item_who1')]/div[1]")).getAttribute("title").trim();
        String team2 = selectGame.findElement(By.xpath("td[contains(@class,'bets-item_who2')]/div[1]")).getAttribute("title").trim();
        float p1 = Float.valueOf(selectGame.findElement(By.xpath("td[contains(@class,'bets-item_k1')]/div[1]/span")).getText());
        LOG.info("Игра, на которой будем проверять добавление в купон из виджета БТ: " + team1 + " - " + team2);
        LOG.info("Коэффициент победы первой команды = " + p1);
        selectGame.findElement(By.xpath("td[contains(@class,'bets-item_k1')]/div[1]/span")).click();
        new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(By.id("menu-toggler")));
        CommonStepDefs.workWithPreloader();
        Stash.put("team1key",team1);
        Stash.put("team2key",team2);
        Stash.put("ishodKey",team1);//мы выбирали победу первой команды, поэтому и в купоне название ихода должно совпадать с первой командой
        Stash.put("coefKey",p1);
    }

    @ActionTitle("осуществляет переход на страницу, проверяет, что открылась нужная страница")
    public void widgetsOnMain(){
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> attr = driver.findElements(By.xpath("//div[@class='benef__item']/a"));
        boolean flag = true; //flag, который говорит что все ок. в конце программы смотрим, если он false - значит были ошибки и их выводим
        for (WebElement element : attr) {
            String link = element.getAttribute("href");
            flag &= CommonStepDefs.goLink(element, link);
            LOG.info("Ссылка " + link + " открылась");
        }
    }

    @ActionTitle("проверяет наличие обязательных разделов новостей с")
    public void checksForNewsBlock(DataTable dataTable){
        String xpathAvailability;
        WebElement current;
        String xpath = "//div[contains(@class,'news-widget-head') and contains(@class,'active')]";
        WebDriver driver = PageFactory.getWebDriver();
        List<String> table = dataTable.asList(String.class);
        String section;
        List<WebElement> main = PageFactory.getWebDriver().findElements(By.xpath("//div[contains(@class,'news-widget-head__')]"))
                .stream().filter(element -> element.isDisplayed()).collect(Collectors.toList());

            for(int i = 0; i < table.size(); i++) {
                section = table.get(i);
                if (main.get(i).getText().isEmpty() || main.get(i).getText() == null){main.get(i).click();}
                String tmp = main.get(i).getText().toUpperCase();
                assertThat(tmp).as("Строка [" + tmp + "] не соответсвует [" + section + "]").contains(section);
                LOG.info("Найден раздел новостей::" + main.get(i).getText());
            }
    }

    @ActionTitle("проверяет что дайджест новостей не пустой")
    public void verifiesThatNewsDigestsNotEmpty() {
        List<WebElement> digestList = PageFactory.getWebDriver().findElements(By.xpath("//a[@class='news-widget__item-inner']"))
                .stream().filter(element -> !element.getText().isEmpty()).collect(Collectors.toList());
        assertThat(!digestList.isEmpty()).as("Ошибка! Не найден ни один дайджест в блоке новостей").isTrue();

        for(WebElement element: digestList){
          LOG.info("Найдена новость::" + element.getText().replaceAll("\n", " ").replaceAll("\\?","\""));
        }
    }



    @ActionTitle("ищет доступные коэффиценты на Главной")
    public void findAvailableCoef() {
        WebDriver driver = PageFactory.getDriver();
        List<WebElement> coeff = driver.findElements(By.cssSelector("div.bets-widget-table__link"));
            if (coeff.size() == 0) {
                LOG.error("Нет доступных коэффициентов в разделе 'Горячие ставки'");
                LOG.info("Переходим в прематч");
                prematchButton.click();
                CommonStepDefs.workWithPreloader();
                coeff = driver.findElements(By.cssSelector("div.bets-block__bet-cell"));
                if (coeff.size() == 0) {
                    Assertions.fail("Нет доступных коэффициентов");
                }
            }else{
                Stash.put("coeffKey", coeff.get(0));
            }
    }
    @ActionTitle("переходит в настройки и меняет коэффицент на Главной")
    public void changePreferencesCoeff() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        LOG.info("переходит в настройки и меняет коэффицент");
        preferences.click();
        String previous;
        List<WebElement> list = driver.findElements(By.cssSelector("span.prefs__key"));
        WebElement coeff = Stash.getValue("coeffKey");
        for (int i = 1; i < 6; i++) {
            previous = coeff.getText();
            LOG.info("Переключаемся на '" + list.get((i*3)%7-1).getText() + "' формат отображения"); // рандомно берёт 1 тип из 6
            list.get((i*3)%7-1).click();
            LOG.info("Текущее значение коэффициента : " + coeff.getText());
            Thread.sleep(350);
            if (previous.equals(coeff.getText())){
                LOG.error("Формат отображения коэффициентов не изменился");
                Assertions.fail("Формат отображения коэффициентов не изменился: " + previous +" " + coeff.getText());
            }
        }
        list.get(0).click();
        LOG.info("Смена форматов отображения коэффицентов прошла успешно");
    }

    @ActionTitle("проверяет смену цвета точек при нажатии на кнопку c")
    public void checksChangeColorDotsWhenButtonPressed(DataTable dataTable){
        WebDriver driver = PageFactory.getWebDriver();
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
        String direction, buttonName;

        LOG.info("Ищем навигационные точки под слайдером новостей");
         List<WebElement> dots = driver.findElements(By.xpath("//*[contains(@class,'news-widget')]//li[contains(@class,'dot')]"))
                 .stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
         LOG.info("Всего точек::" + dots.size());

        for(int i = 0; i < table.size(); i++) {
            direction = table.get(i).get(DIRECTION);
            buttonName = table.get(i).get(BUTTON);


            if(direction.contains("Вправо")){
                LOG.info("Нажимаем на самую левую точку");
                dots.get(0).click();
                for (int j = 0; j < dots.size(); j++){
                    if(dots.get(j).getAttribute("class").contains("is-selected")){
                        LOG.info("Нажимаем на::" + buttonName);
                        pressButtonAP(buttonName);
                        if(j == (dots.size()-1)) {
                            assertThat(dots.get(0).getAttribute("class").contains("is-selected"))
                                    .as("Ошибка! Следующая точка не стала закрашенной").isTrue();
                            LOG.info("Первая точка [1] закрашена");

                        }else {
                            assertThat(dots.get(j + 1).getAttribute("class").contains("is-selected"))
                                    .as("Ошибка! Следующая точка не стала закрашенной").isTrue();
                            LOG.info("Следующая точка [" + (j + 2) + "] закрашена");
                            verifiesThatNewsDigestsNotEmpty();
                        }
                    }
                }
            }else if(direction.contains("Влево")){
                LOG.info("Нажимаем на самую правую точку");
                dots.get(dots.size() - 1).click();
                for (int k = dots.size() - 1; k >= 0; k--){
                    if(dots.get(k).getAttribute("class").contains("is-selected")){
                        LOG.info("Нажимаем на::" + buttonName);
                        pressButtonAP(buttonName);
                        if(k == 0) {
                            assertThat(dots.get(dots.size() - 1).getAttribute("class").contains("is-selected"))
                                    .as("Ошибка! Следующая точка не стала закрашенной").isTrue();
                            LOG.info("Последняя точка [" + dots.size() + "] закрашена");

                        }else {
                            assertThat(dots.get(k - 1).getAttribute("class").contains("is-selected"))
                                    .as("Ошибка!Предыдующая точка не стала закрашенной").isTrue();
                            LOG.info("Предыдущая точка [" + (k) + "] закрашена");
                            verifiesThatNewsDigestsNotEmpty();
                        }
                    }
                }
            }
        }
    }

}
