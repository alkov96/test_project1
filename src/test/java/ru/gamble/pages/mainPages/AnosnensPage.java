package ru.gamble.pages.mainPages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;

@PageEntry(title = "Анонсы")
public class AnosnensPage extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(AnosnensPage.class);

    @FindBy(xpath = "//div[test()='Анонсы']")
    private WebElement pageTitle;
}
