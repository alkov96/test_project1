package ru.gamble.pages.userProfilePages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;


@PageEntry(title = "Мини Личный Кабинет")
public class PopUPLCPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(PopUPLCPage.class);

    @ElementTitle("адрес почты")
    @FindBy(id ="user-profile")
    private WebElement emailBotton;

    @ElementTitle("Выйти")
    @FindBy(id ="log-out-button")
    private WebElement exitButton;

    @ElementTitle("Личный кабинет")
    @FindBy(id ="user-profile")
    private WebElement profileButton;


    public PopUPLCPage() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(exitButton));
    }

}
