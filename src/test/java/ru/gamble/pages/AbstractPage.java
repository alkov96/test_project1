package ru.gamble.pages;

import cucumber.api.DataTable;
import cucumber.api.java.en.When;
import cucumber.api.java.ru.Когда;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.gamble.stepdefs.CommonStepDefs;
import ru.gamble.utility.BeforeTest;
import ru.gamble.utility.YandexPostman;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.Page;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.exceptions.PageException;
import ru.sbtqa.tag.pagefactory.exceptions.PageInitializationException;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.gamble.stepdefs.CommonStepDefs.workWithPreloader;
import static ru.gamble.utility.Constants.DATEOFBIRTH;
import static ru.gamble.utility.Constants.RANDOM;
import static ru.gamble.utility.Generators.randomNumber;
import static ru.gamble.utility.Generators.randomString;
import static ru.sbtqa.tag.pagefactory.PageFactory.getWebDriver;


public abstract class AbstractPage extends Page {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPage.class);

    @ElementTitle("День")
    @FindBy(className = "inpD")
    protected WebElement fieldDay;

    @ElementTitle("Месяц")
    @FindBy(xpath = "//div[contains(@class,'dateInput')]/div[@class='inpM']")
    protected WebElement fieldMonth;

    @ElementTitle("Год")
    @FindBy(className = "inpY")
    protected WebElement fieldYear;

    @ElementTitle("Подвал")
    @FindBy(xpath = "//*[@class='footer__pin']")
    protected WebElement footerButton;

    @ElementTitle("На главную страницу")
    @FindBy(xpath = "//a[@class = 'btn btn_important']")
    protected WebElement onMainPageButton;


    @ActionTitle("сохраняет с")
    public void saveKeyValue(DataTable dataTable){ CommonStepDefs.saveValueToKey(dataTable); }

    @ActionTitle("нажимает кнопку")
    public static void pressButton(String param){
        CommonStepDefs.pressButton(param);
        workWithPreloader();
    }

    @ActionTitle("stop")
    public static void stop(){
        LOG.info("STOP");
    }

    @ActionTitle("закрываем браузер")
    public static void closeBrowser(){
        getWebDriver().close();
    }


    /**
     * Метод получения письма и перехода по ссылке для завершения регистрации на сайте
     *
     * @param key - ключ по которому получаем е-mail из памяти.
     */
    @ActionTitle("завершает регистрацию перейдя по ссылке в")
    public void endRegistrationByEmailLink(String key){
        WebDriver driver = getWebDriver();
        String email = Stash.getValue(key);
        String link = "";
        String url = "";

        try {
            url = BeforeTest.getData().get("site1").get("mainurl").getValue();
            link = YandexPostman.getLinkForAuthentication(email);
        }catch (DataException de){
            LOG.error("Ошибка! Не смогли получить ссылку сайта");
        } catch (Exception e) {
            LOG.error("Ошибка! Не смогли получить ссылку для аутентификации.");
            e.printStackTrace();
        }

        LOG.info("Переходим по ссылке из e-mail");

        driver.get(url + "?action=verify&" + link);

        new WebDriverWait(driver,30).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.modal__closeBtn.closeBtn")));

        LOG.info("Закрываем уведомление об успешном подтверждении почты");
        driver.findElement(By.cssSelector("a.modal__closeBtn.closeBtn")).click();
    }


     @ActionTitle("проверяет ссылки")
     public void checkElementsOfFooter(DataTable dataTable) throws PageInitializationException,PageException {
         List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
         String linkTitle, expextedUrl;
         String currentUrl = PageFactory.getWebDriver().getCurrentUrl();

         for(int i = 0; i < table.size(); i++) {
             linkTitle = table.get(0).get("Ссылка");

             CommonStepDefs.goesByReference(linkTitle);
             PageFactory.getWebDriver().getCurrentUrl();
             LOG.info("Переходим по ссылке");


             expextedUrl = table.get(0).get("Соответсвует");
         }
    }

    /**
     * Открывает выпадающий список и выбирает оттуда пункт случайным образом
     *
     * @param element  - поле где ждем выпадающий список
     * @param max - максимальный пункт, который можно выбрать (включая этот max). Второго параметра может и не быть. тогда максимум - это длина всего списка
     * @return Возвращает номер пункта который был выбран
     */
    protected int selectMenu(WebElement element, int max) {
        WebDriver driver = PageFactory.getWebDriver();
        int menuSize = element.findElements(By.xpath("custom-select/div[2]/div")).size();
        menuSize -= (max + 1);
        int select = 1 + (int) (Math.random() * menuSize);
        element.findElement(By.xpath("custom-select")).click();
        element.findElement(By.xpath("custom-select/div[2]/div[" + select + "]")).click();
        return select;
    }

    protected int selectMenu(WebElement element) {
        return selectMenu(element, 0);
    }

    protected void enterDate(String value){
        if(value.equals(RANDOM)){
            LOG.info("Вводим случайную дату");
            do {
                selectMenu(fieldDay);
                selectMenu(fieldMonth);
                selectMenu(fieldYear);
            } while (PageFactory.getWebDriver().findElement(By.className("inpErrText")).isDisplayed());
        }else {
            String[] tmp = value.split(".");
            LOG.info("Вводим дату");
            selectMenu(fieldDay,Integer.parseInt(tmp[0]));
            selectMenu(fieldMonth,Integer.parseInt(tmp[1]));
            selectMenu(fieldYear,Integer.parseInt(tmp[2]));
        }
    }

    /**
     * В выбранном поле вводит один символ и если появляется выпадающий список - выбирает первый пункт из него.
     * Иначе либо заново вводит символ и ждет список, либо заполняет поле рандомной последовательностью
     *
     * @param field    - поле, которое заполняем
     * @param authFill - булев параметр, говорит о том обязательно ли выбирать из списка (true), или можно заполнить рандомом (false)
     */
    public void fillAddress(WebElement field, boolean authFill)  {
        List<WebElement> list;
        do {
            field.clear();
            StringBuilder n = new StringBuilder();
            n.append((char) ('А' + new Random().nextInt(64)));
            field.sendKeys(n);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list = field.findElements(By.xpath("../ul[1]/li"));
        } while (list.isEmpty() && authFill);
        if (field.findElements(By.xpath("../ul[1]/li")).isEmpty()) {
            field.clear();
            field.sendKeys(randomString(20));
        } else
            field.findElement(By.xpath("../ul[1]/li[" + (new Random().nextInt(list.size()) + 1) + "]")).click();

        if (field.getAttribute("value").length() == 0) {
            LOG.info("ОШИБКА. Нажали на пункт в выпадающем списке для города,а знчение не выбралось!!");
            field.sendKeys(randomString(20));
        }
    }

    @ActionTitle("проверяет присутствие текста")
    public void checksPresenceOfText(String text){
        List<WebElement> list = PageFactory.getWebDriver().findElements(By.xpath("//*[text()='" + text + "']"))
                .stream().filter(element -> element.isDisplayed()).collect(Collectors.toList());
        assertThat(!list.isEmpty()).as("Ошибка.Не найден::[" + text+ " ]").isTrue();
    }
}
