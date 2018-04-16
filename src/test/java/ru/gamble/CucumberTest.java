package ru.gamble;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import ru.gamble.pages.EventViewerPage;
import ru.sbtqa.tag.cucumber.TagCucumber;
import ru.sbtqa.tag.pagefactory.PageFactory;

import java.io.File;

import static org.openqa.selenium.logging.LogType.DRIVER;

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
}




