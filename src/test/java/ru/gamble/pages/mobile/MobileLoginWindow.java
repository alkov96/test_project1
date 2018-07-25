package ru.gamble.pages.mobile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.AbstractPage;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;

@PageEntry(title = "Мобильная главная страница")
public class MobileLoginWindow extends AbstractPage {
    private static final Logger LOG = LoggerFactory.getLogger(MobileLoginWindow.class);


    public MobileLoginWindow() {
    }
}
