package ru.gamble.pages.RegistrationPages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;

@PageEntry(title = "Поздравляем!")
public class CongratulationPage extends AbstractPage {

    private static final Logger LOG = LoggerFactory.getLogger(CongratulationPage.class);

    @FindBy(xpath = "//*[text()='Учетная запись']")
    private WebElement pageTitle;

    @ElementTitle("День")
    @FindBy(className = "inpD")
    protected WebElement fieldDay;
}
