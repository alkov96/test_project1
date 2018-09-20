package ru.gamble.pages;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.stepdefs.CommonStepDefs;
import ru.gamble.utility.JsonLoader;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.By.xpath;
import static ru.gamble.utility.Constants.STARTING_URL;

/**
 * @author p.sivak.
 * @since 11.05.2018.
 */
@PageEntry(title = "Приложения для iOS и Android")
public class LandingAppPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(LandingAppPage.class);

    @FindBy(xpath = "//h1[text()='\n" +
            "Все события спорта']")
    private WebElement header;

    public LandingAppPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(header));
    }

    @ActionTitle("проверяет скачивание приложения на ios")
    public void downloadIos() {
        WebDriver driver = PageFactory.getDriver();
        int x, y;
        y = driver.findElement(xpath("//div[contains(@class,'block-text_first')]//i[contains(@class,'icon-mac')]")).getLocation().getY() - 100;
        x = driver.findElement(xpath("//div[contains(@class,'block-text_first')]//i[contains(@class,'icon-mac')]")).getLocation().getX() - 100;
        CommonStepDefs.scrollPage(x, y);
        WebElement iosLink = driver.findElement(xpath("//div[contains(@class,'block-text_first')]//i[contains(@class,'icon-mac')]"));
        assertThat(CommonStepDefs.goLink(iosLink, "itunes.apple.com")).isTrue();
        LOG.info("Ссылка " + "itunes.apple.com" + " открылась");
    }

    @ActionTitle("нажимает на кнопку для загрузки приложения на android")
    public void clickDownloadAndroid(){
        WebDriver driver = PageFactory.getDriver();
        driver.findElement(xpath("//div[contains(@class,'block-text_first')]//i[contains(@class,'icon-android')]")).click();
    }

    @ActionTitle("проверяет скачивание приложения на android")
    public void downloadAndroidForLanding() throws IOException {
        downloadAndroid();
    }

    public static void downloadAndroid() throws IOException {
        WebDriver driver = PageFactory.getDriver();
        if (!driver.findElement(xpath("//div[@class='modal__android-app-text']")).isDisplayed()) {
            Assert.fail("Не открылся попап на скачивание приложения для андроида");
        } else {
            if (!driver.findElement(By.id("app_desctop_popup_link_more")).isDisplayed()) {
                Assert.fail("На попапе нет ссылки на правила GP. нет скопки @Подробнее@");
            } else {
                String pattern = "play.google.com/intl/ru_ALL/about/restricted-content/gambling";
                WebElement rules = driver.findElement(By.id("app_desctop_popup_link_more"));
                assertTrue(CommonStepDefs.goLink(rules, pattern));
            }
            if (!driver.findElement(By.id("app_desctop_popup_btn_download")).isDisplayed()) {
                Assert.fail("На попапе нет кнопки на скачивание");
            } else {
                String link = driver.findElement(By.id("app_desctop_popup_btn_download")).getAttribute("href").trim();
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpContext localContext = new BasicHttpContext();
                HttpGet httpGet = new HttpGet(link);

                LOG.info(link);

                HttpResponse httpResponse = httpClient.execute(httpGet, localContext);
                LOG.info("Начинаю скачивание приложения на Android");
                switch (httpResponse.getStatusLine().getStatusCode()) {
                    case 200:
                        LOG.info("Скачивание удалось. Статус ответа = 200");
                        break;
                    default:
                        LOG.info("Ошибка при скачивании! Статус ответа = " + httpResponse.getStatusLine().getStatusCode());
                        Assert.fail("Скачивание приложения для андроида не удалось");
                        break;
                }
            }
        }

        LOG.info("Закрытие попапа на скачивание приложения");
        driver.findElement(By.id("app_desctop_popup_close")).click();

    }

    @ActionTitle("проверка блока %Все как любите")
    public void checkBlockAsULike() throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        int x, y;
        boolean flag = true;
        y = driver.findElement(xpath("//div[@class='m-landing__inner-block-text-p-links']/p[contains(@class,'active')]")).getLocation().getY() - 100;
        x = driver.findElement(xpath("//div[@class='m-landing__inner-block-text-p-links']/p[contains(@class,'active')]")).getLocation().getX() - 100;
        CommonStepDefs.scrollPage(x, y);
        List<WebElement> links = driver.findElements(xpath("//div[@class='m-landing__inner-block-text-p-links']/p"));//список всех ссылок
        List<WebElement> images = driver.findElements(xpath("//div[contains(@class,'js-parallaxed-block2_fast')]/img[@class]"));//список картинок

        for (int count = 0; count < links.size(); count++) {
            LOG.info("Переходим на пункт " + (count + 1));
            links.get(count).click();
            Thread.sleep(500);
            if (!links.get(count).getAttribute("class").contains("active")) {
                int currentLink = driver.findElements(xpath("//div[@class='m-landing__inner-block-text-p-links']/p[contains(@class,'active')]/preceding-sibling::p")).size() + 1;
                flag = false;
                Assert.fail("Нажали на пункт №" + (count + 1) + " в блоке Все как любите, но на этот пункт не было перехода. Вместо этого активен пункт №" + currentLink);
            }
            if (!images.get(count).getAttribute("class").contains("active")) {
                int currentImg = driver.findElements(xpath("//div[contains(@class,'js-parallaxed-block2_fast')]/img[contains(@class,'active')]/preceding-sibling::img[@class]")).size() + 1;
                flag = false;
                Assert.fail("Нажали на пункт №" + (count + 1) + " в блоке Все как любите, но картинка не изменилась. Вмсето этого активна картинка №" + currentImg);
            }
        }

    }

    @ActionTitle("проверка того, что все нужные картинки прогрузились и есть футер")
    public void picsAndFooter() {
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;
        List<String> waitingImg = Arrays.asList(
                "ipad_screen1.png",
                "ipad_screen6.png",
                "ipad_screen2.png",
                "man2.png",
                "man3.png",
                "phone_3.png",
                "woman.png",
                "phone_1.png",
                "man1.png",
                "ipad.png",
                "ipad_screen5.png"
        );
        List<String> allImg = new ArrayList<>();
        driver.findElements(xpath("//img[contains(@src,'/images/landing/mobile_app')]")).forEach(element -> {
            try {
                allImg.add(element.getAttribute("src")
                        .replace(JsonLoader.getData().get(STARTING_URL).get("MAIN_URL").getValue()+"/images/landing/mobile_app/", ""));
            } catch (DataException e) {
                LOG.error(e.getMessage());
            }
        });
        if (!allImg.containsAll(waitingImg)) {
            flag = false;
            Assert.fail("Не все картинки прогрузились. На сайте есть следующие картинки " + allImg);
        }

        if (driver.findElements(xpath("//div[contains(@class,'footer')]")).isEmpty()) {
            flag = false;
            Assert.fail("Нет футера");
        }
    }

    @ActionTitle("смотрит ссылку на правила про фрибет")
    public void linkFreeBet() {
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;
        int x, y;
        y = driver.findElement(By.id("app_desctop_freebet_block_btn")).getLocation().getY() - 100;
        x = driver.findElement(By.id("app_desctop_freebet_block_btn")).getLocation().getX() - 100;
        CommonStepDefs.scrollPage(x, y);
        String linkFreeBet = driver.findElement(By.id("app_desctop_freebet_block_btn")).getAttribute("href");
        assertTrue(CommonStepDefs.goLink(driver.findElement(By.id("app_desctop_freebet_block_btn")), linkFreeBet));
        LOG.info("Ссылка на фрибет работает");
    }


    @ActionTitle("смотрит ссылку на правила про выплаты выигрышей")
    public void linkForPrize() {
        WebDriver driver = PageFactory.getDriver();
        int x, y;
        y = driver.findElement(By.id("app_desctop_advantages_block_link_warranty")).getLocation().getY() - 100;
        x = driver.findElement(By.id("app_desctop_advantages_block_link_warranty")).getLocation().getX() - 100;
        CommonStepDefs.scrollPage(x, y);
        String linkWithdraw = driver.findElement(By.id("app_desctop_advantages_block_link_warranty")).getAttribute("href");
        assertTrue(CommonStepDefs.goLink(driver.findElement(By.id("app_desctop_advantages_block_link_warranty")), linkWithdraw));
    }

    @ActionTitle("смотрит ссылку на правила про НДФЛ")
    public void linkForNDFL() {
        WebDriver driver = PageFactory.getDriver();
        int x, y;
        y = driver.findElement(By.id("app_desctop_advantages_block_link_ndfl")).getLocation().getY() - 100;
        x = driver.findElement(By.id("app_desctop_advantages_block_link_ndfl")).getLocation().getX() - 100;
        CommonStepDefs.scrollPage(x, y);
        String linkNDFL = driver.findElement(By.id("app_desctop_advantages_block_link_ndfl")).getAttribute("href");
        assertTrue(CommonStepDefs.goLink(driver.findElement(By.id("app_desctop_advantages_block_link_ndfl")), linkNDFL));
    }


    @ActionTitle("отправляет СМС со страницы лэндинга на телефон")
    public void sendSMS(String phone, String isOk) throws InterruptedException {
        WebDriver driver = PageFactory.getDriver();
        boolean flag = isOk.equals("ожидаем успех");
        String hintBefore3times = "Мы отправили вам ссылку на скачивание";
        String hintAfter3times = "Ошибка. Повторите попытку через 24 часа";
        if (phone.matches("^[A-Z_]+$")){
            phone = Stash.getValue(phone);
        }
        String expectedHint = flag?hintBefore3times:hintAfter3times;
        int x, y;
        WebElement inputPhone=driver.findElement(By.id("app_desctop_sms_block_input_phone"));
        WebElement sendPhone=driver.findElement(By.id("app_desctop_sms_block_btn_send"));
        x=inputPhone.getLocation().getX()-100;
        y=inputPhone.getLocation().getY()-100;
        CommonStepDefs.scrollPage(x,y);

        String hint;


            inputPhone.clear();
            inputPhone.sendKeys(phone);

            LOG.info("Отправили смс на номер +7"+phone);
            sendPhone.click();
            Thread.sleep(1000);
            hint = driver.findElement(xpath("//div[contains(@class,'sms-form-hint')]")).getText();
            if (!hint.contains(expectedHint)) {
                Assert.fail("После отпраки смс текст подскази не соответсвует ожидаемому. Вместо @" + expectedHint + "@ написано " + hint);
            }

        Thread.sleep(10000);
        if (sendPhone.isEnabled()!=flag){
            Assert.fail("После отправки смс кнопка @Отправить@ оказалась не в том состоянии, что ожидалось. Её активность = " + !flag);
        }

    }

}


