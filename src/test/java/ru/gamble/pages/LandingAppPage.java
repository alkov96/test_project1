package ru.gamble.pages;

import cucumber.api.java.mn.Харин;
import cucumber.runtime.junit.Assertions;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openqa.selenium.By.xpath;

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
        boolean flag = true;
        WebDriver driver = PageFactory.getDriver();
        int x, y;
        y = driver.findElement(xpath("//div[contains(@class,'block-text_first')]//i[contains(@class,'icon-mac')]")).getLocation().getY() - 100;
        x = driver.findElement(xpath("//div[contains(@class,'block-text_first')]//i[contains(@class,'icon-mac')]")).getLocation().getX() - 100;
        CommonStepDefs.scrollPage(x, y);
        WebElement iosLink = driver.findElement(xpath("//div[contains(@class,'block-text_first')]//i[contains(@class,'icon-mac')]"));
        flag &= CommonStepDefs.goLink(iosLink, "itunes.apple.com");
        LOG.info("Ссылка " + "itunes.apple.com" + " открылась");
    }

    @ActionTitle("проверяет скачивание приложения на android")
    public void downloadAndroid() throws IOException {
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;
        driver.findElement(xpath("//div[contains(@class,'block-text_first')]//i[contains(@class,'icon-android')]")).click();
        if (!driver.findElement(xpath("//div[@class='modal__body modal__body_app']")).isDisplayed()) {
            Assert.fail("Не открылся попап на скачивание приложения для андроида");
            flag = false;
        } else {
            if (!driver.findElement(By.id("app_desctop_popup_link_more")).isDisplayed()) {
                Assert.fail("На попапе нет ссылки на правила GP. нет скопки @Подробнее@");
                flag = false;
            } else {
                String pattern = "play.google.com/intl/ru_ALL/about/restricted-content/gambling";
                WebElement rules = driver.findElement(By.id("app_desctop_popup_link_more"));
                flag &= CommonStepDefs.goLink(rules, pattern);
            }
            if (!driver.findElement(By.id("app_desctop_popup_btn_download")).isDisplayed()) {
                Assert.fail("На попапе нет кнопки на скачивание");
                flag = false;
            } else {
                String link = driver.findElement(By.id("app_desctop_popup_btn_download")).getAttribute("href").trim();
                HttpClient httpClient = new DefaultHttpClient();
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
                        flag = false;
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
        int x,y;
        boolean flag = true;
        y=driver.findElement(xpath("//div[@class='m-landing__inner-block-text-p-links']/p[contains(@class,'active')]")).getLocation().getY()-100;
        x=driver.findElement(xpath("//div[@class='m-landing__inner-block-text-p-links']/p[contains(@class,'active')]")).getLocation().getX()-100;
        CommonStepDefs.scrollPage(x,y);
        List<WebElement> links = driver.findElements(xpath("//div[@class='m-landing__inner-block-text-p-links']/p"));//список всех ссылок
        List <WebElement> images = driver.findElements(xpath("//div[contains(@class,'js-parallaxed-block2_fast')]/img[@class]"));//список картинок

        for (int count=0;count<links.size();count++){
            LOG.info("Переходим на пункт "+(count+1));
            links.get(count).click();
            Thread.sleep(500);
            if (!links.get(count).getAttribute("class").contains("active")){
                int currentLink = driver.findElements(xpath("//div[@class='m-landing__inner-block-text-p-links']/p[contains(@class,'active')]/preceding-sibling::p")).size() + 1;
                flag=false;
                Assert.fail("Нажали на пункт №" + (count+1) + " в блоке Все как любите, но на этот пункт не было перехода. Вместо этого активен пункт №" + currentLink);
            }
            if(!images.get(count).getAttribute("class").contains("active")){
                int currentImg = driver.findElements(xpath("//div[contains(@class,'js-parallaxed-block2_fast')]/img[contains(@class,'active')]/preceding-sibling::img[@class]")).size() + 1;
                flag=false;
                Assert.fail("Нажали на пункт №" + (count+1) + " в блоке Все как любите, но картинка не изменилась. Вмсето этого активна картинка №" + currentImg);
            }
        }

    }

    @ActionTitle("проверка того, что все нужные картинки прогрузились и есть футер")
    public void picsAndFooter(){
        WebDriver driver = PageFactory.getDriver();
        boolean flag = true;
        List<String> waitingImg = Arrays.asList (
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
        List<String> allImg=new ArrayList<>();
        driver.findElements(xpath("//img[contains(@src,'/images/landing/mobile_app')]")).forEach(element -> allImg.add(element.getAttribute("src").replace("https://dev-bk-bet-site1.tsed.orglot.office/images/landing/mobile_app/","")));
        if (!allImg.containsAll(waitingImg)){
            flag=false;
            Assert.fail("Не все картинки прогрузились. На сайте есть следующие картинки " + allImg);
        }

        if (driver.findElements(xpath("//div[contains(@class,'footer')]")).isEmpty()){
            flag=false;
            Assert.fail("Нет футера");
        }
    }
}


