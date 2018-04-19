package ru.gamble;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.qameta.allure.Attachment;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.pagefactory.PageFactory;
import ru.sbtqa.tag.pagefactory.drivers.TagWebDriver;

@RunWith(Cucumber.class)
@CucumberOptions(monochrome = true,
        format ={"pretty"},
        glue = {"ru.gamble.stepdefs", "ru.sbtqa.tag.pagefactory.stepdefs"},
        features = {"src/test/resources/features/"},
        plugin= {"io.qameta.allure.cucumber2jvm.AllureCucumber2Jvm"},
        tags = {"@1"})

public class CucumberTest {
    private static final Logger LOG = LoggerFactory.getLogger(CucumberTest.class);

    @BeforeClass
    public static void setUp() {
        TagWebDriver.getDriver().manage().window().setSize(new Dimension(1920, 1080));
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
//        options.addArguments("start-maximized");
//        WebDriver driver = new ChromeDriver(options);
////        System.setProperty("webdriver.chrome.driver", "webdrivers\\chromedriver.exe");
//        PageFactory.getWebDriver().manage().window().setSize(new Dimension(1920, 1080));
//
//
//
//        driver.navigate().to("https://google.com");
////        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
//        options.addArguments("start-maximized");
//        WebDriver driver = new ChromeDriver(options);
   }


    @Rule
    public TestWatcher watchman = new TestWatcher() {
        String fileName;

        @Override
        protected void failed(Throwable e, Description description) {
            screenshot();
        }

        @Attachment(value = "Page screenshot", type = "image/png")
        public byte[] saveScreenshot(byte[] screenShot) {
            return screenShot;
        }

        public void screenshot() {
            if (PageFactory.getWebDriver() == null) {
                LOG.info("Driver for screenshot not found");
                return;
            }

            saveScreenshot(((TakesScreenshot) PageFactory.getWebDriver()).getScreenshotAs(OutputType.BYTES));

        }
    };

    @AfterClass
    public static void tearDown() {
        PageFactory.dispose();
    }


}




