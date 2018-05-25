package ru.gamble.pages.rulesPages;

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

/**
 * @author p.sivak.
 * @since 24.05.2018.
 */
@PageEntry(title = "Левое меню в правилах")
public class LeftMenu extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(LeftMenu.class);

    @FindBy(xpath = "//div[@class='text-page-sidebar__body']")
    private WebElement menu;

    @ElementTitle("Как пополнить баланс? в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Как пополнить баланс?')]")
    private WebElement howToFeelBalance;

    @ElementTitle("Тарифы на ввод и вывод средств в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Тарифы на ввод и вывод средств')]")
    private WebElement tariffs;

    @ElementTitle("Публичная оферта в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Публичная оферта')]")
    private WebElement offerta;

    @ElementTitle("Футбол в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Футбол')]")
    private WebElement footbol;

    @ElementTitle("Хоккей в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Хоккей')]")
    private WebElement hokkey;

    @ElementTitle("Баскетбол в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Баскетбол')]")
    private WebElement basketbol;

    @ElementTitle("Прочие командные виды в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Прочие командные виды')]")
    private WebElement anotherSports;

    @ElementTitle("Теннис в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Теннис')]")
    private WebElement tennis;

    @ElementTitle("Авто- и мотоспорт в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Авто- и мотоспорт')]")
    private WebElement avtoMoto;

    @ElementTitle("Циклические виды спорта в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Циклические виды спорта')]")
    private WebElement cycls;

    @ElementTitle("Единоборства в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Единоборства')]")
    private WebElement mma;

    @ElementTitle("Другие события в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Другие события')]")
    private WebElement anotherEvents;

    @ElementTitle("Фрибет за миллион в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Фрибет за миллион')]")
    private WebElement freebetMillion;

    @ElementTitle("Получите фрибет на 1000 бонусов в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Получите фрибет на 1000 бонусов')]")
    private WebElement freebetThousand;

    @ElementTitle("Супербет в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Супербет')]")
    private WebElement superbet;

    @ElementTitle("Экспресс-бонус в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Экспресс-бонус')]")
    private WebElement express;

    @ElementTitle("Кэшаут в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Кэшаут')]")
    private WebElement cashout;

    @ElementTitle("Бонусы в меню")
    @FindBy(xpath = "//li[@class='static-menu__item']/a[contains(text(),'Бонусы')]")
    private WebElement bonuses;

    public LeftMenu() {
        WebDriver driver = PageFactory.getDriver();
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(driver)), this);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(menu));
    }

}
