package ru.gamble;

import cucumber.api.CucumberOptions;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import ru.sbtqa.tag.cucumber.TagCucumber;
import ru.sbtqa.tag.pagefactory.PageFactory;

@RunWith(TagCucumber.class)
@CucumberOptions(monochrome = true,
        format ={"pretty"},
        glue = {"ru.gamble.stepdefs", "ru.sbtqa.tag.pagefactory.stepdefs"},
        features = {"src/test/resources/features/"},
        plugin= {"io.qameta.allure.cucumber2jvm.AllureCucumber2Jvm"},
        tags = {"@smoke"})

public class CucumberTest {
}




