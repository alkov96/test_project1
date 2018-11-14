# language: ru
Функционал: 9_Футер

  @regress
  @CheckingWorkFooterInAllSections_C1058
  Сценарий: Проверка наличия футера на всех страницах сайта. Проверка работы ссылок на соцсети, страницы сайта, на скачивание мобильного приложения. Проверка наличия иконок платежных систем.

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (переходит по ссылке) "Лайв"
    * открывается страница "Лайв"

    * пользователь (нажимает кнопку) "Подвал"
    * открывается страница "Подвал сайта"


    * проверяем ТЕКСТ при переходе по ссылке с
    | ССЫЛКА                  | ТЕКСТ                                            |
    | О компании              | Ваша игра началась                               |
    | Контакты                | На картах Дубль-гис                              |
    | Как получить выигрыш    | Основные правила приема Интерактивных ставок     |
    | Мобильное приложение    | Все события спорта                               |
    | Для iOS                 | BK FAVORIT, OOO                                  |
    | ВКонтакте               | 888.ru — онлайн-букмекер,                       |
    | Youtube                 | 888ru Ставки на спорт                            |
    | Студии Артемия Лебедева | 1995–2018 Студия Артемия Лебедева                |

    * проверяем ТЕКСТ при переходе по ссылке с
      | ССЫЛКА                  | ТЕКСТ    |
      | На футбол               | футбол     |
      | На хоккей               | хоккей     |
      | На волейбол             | волейбол   |
      | На баскетбол            | баскетбол  |
      | На теннис               | теннис     |
      | На киберспорт           | киберспорт |

    * пользователь (проверяет присутствие ссылки) "Для Android"
    * пользователь (проверяет что число платёжных систем) "12"

    * пользователь (нажимает кнопку) "Онлайн-чат"
    * открывается страница "Онлайн-чат"

    * пользователь (нажимает кнопку) "Свернуть"
    * открывается страница "Подвал сайта"

    * открывается страница "Лайв"

    * пользователь (переходит по ссылке) "События дня"
    * открывается страница "События дня"

    * пользователь (нажимает кнопку) "Подвал"
    * открывается страница "Подвал сайта"

    * открывается страница "Лайв"

    * пользователь (переходит по ссылке) "Лайв-обзор"
    * открывается страница "Лайв-обзор"

    * пользователь (нажимает кнопку) "Подвал"
    * открывается страница "Подвал сайта"

    * открывается страница "Лайв"

    * пользователь (переходит по ссылке) "Мультимонитор"
    * открывается страница "Мультимонитор"

    * пользователь (нажимает кнопку) "Подвал"
    * открывается страница "Подвал сайта"

    * пользователь (нажимает кнопку) "На главную"
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Прематч"

    * пользователь (нажимает кнопку) "Подвал"
    * открывается страница "Подвал сайта"

    * открывается страница "Прематч"

    * пользователь (переходит по ссылке) "Лайв-календарь"
    * открывается страница "Лайв-календарь"

    * пользователь (нажимает кнопку) "Подвал"
    * открывается страница "Подвал сайта"

    * открывается страница "Прематч"

    * пользователь (переходит по ссылке) "Результаты"
    * открывается страница "Результаты"

    * пользователь (нажимает кнопку) "Подвал"
    * открывается страница "Подвал сайта"







