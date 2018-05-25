package ru.gamble;

import cucumber.api.CucumberOptions;
import cucumber.api.java.After;
import cucumber.api.junit.Cucumber;
import io.qameta.allure.Attachment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.pagefactory.PageFactory;

import java.time.LocalTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@RunWith(Cucumber.class)
@CucumberOptions(monochrome = true,
        format ={"pretty"},
        glue = {"ru.gamble.stepdefs", "ru.sbtqa.tag.pagefactory.stepdefs"},
        features = {"src/test/resources/features/"},
        plugin= {"io.qameta.allure.cucumber2jvm.AllureCucumber2Jvm"},
        tags = {"@smoke"})

public class CucumberTest {
    private static final Logger LOG = LoggerFactory.getLogger(CucumberTest.class);

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
//
//    @AfterClass
//    public static void afterScenario(){
//           WebDriver driver =  PageFactory.getWebDriver();
//        if(PageFactory.getWebDriver().getWindowHandles().size() > 0) {
//            LOG.info("Закрываем WebDriver");
//            driver.quit();
//            PageFactory.dispose();
//        }else {
//            LOG.info("WebDriver уже закрыт");
//        }
//    }
}




