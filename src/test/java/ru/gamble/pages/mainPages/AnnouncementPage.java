package ru.gamble.pages.mainPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ActionTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@PageEntry(title = "Анонсы")
public class AnnouncementPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(AnnouncementPage.class);
    static WebDriver driver = PageFactory.getDriver();

    @FindBy(xpath = "//div[@class='g-text-container']//h1")
    private WebElement pageTitle;

    public AnnouncementPage() {
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    @ActionTitle("проверяет наличие анонсов")
    public void checksForAnnouncements(){
        String xpathDigests = "//a[contains(@class,'newslist__title')]";
        String actualText;

        List<WebElement> announcementsList = driver.findElements(By.xpath(xpathDigests))
                .stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
        assertThat(announcementsList.size()).as("Ошибка! Анонсы не найдены").isGreaterThan(0);

        for(WebElement announcement: announcementsList){
            if(!announcement.getAttribute("innerText").equals("")){
                actualText = announcement.getAttribute("innerText");
                LOG.info("Найдено::" + actualText);
            }
        }
    }
}

