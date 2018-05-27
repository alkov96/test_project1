# language: ru
Функционал: Экспресс бонус

  @smoke
  @ExpressBonus_C39773

  @correct
  Сценарий: Позитивный кейс
    * переходит на главную страницу
    * открывается страница "Главная страница"
    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"
    * пользователь (логинится с) данными
      | Логин  | bladekeeper@ya.ru |
      | Пароль | 1qwert2ASDF       |
    * пользователь (нажимает кнопку) "Войти"
    * открывается страница "Главная страница"
    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Прематч"
    * пользователь (нажимает кнопку) "Лайв-календарь"
    * открывается страница "Лайв-календарь"
    * пользователь (ищет событие с коэффициентом) "1.25"
    * пользователь (добавляет корректные события, пока их не станет) "2"
    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"
    * пользователь (заполняет сумму для ставки) "Экспресс" "1"
    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)
    * пользователь осуществляет переход в "Лайв-календарь"
    * открывается страница "Лайв-календарь"
    * пользователь (добавляет корректные события, пока их не станет) "3"
    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"
    * пользователь (заполняет сумму для ставки) "Экспресс" "1"
    * пользователь (проверяет корректность ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)
    * пользователь осуществляет переход в "Лайв-календарь"
    * открывается страница "Лайв-календарь"
    * пользователь (добавляет корректные события, пока их не станет) "4"
    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"
    * пользователь (заполняет сумму для ставки) "Экспресс" "1"
    * пользователь (проверяет корректность ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет наличие бонуса к возможному выйгрышу)
    * пользователь (убирает события из купона, пока их не станет) "2"
    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)
    * пользователь осуществляет переход в "Авторизованная Главная страница"
    * открывается страница "Авторизованная Главная страница"
    * пользователь (нажимает кнопку) "Иконка юзера"
    * открывается страница "Мини Личный Кабинет"
    * пользователь (нажимает кнопку) "Выйти"

  @fail
  Сценарий: Негативный кейс
    * переходит на главную страницу
    * открывается страница "Главная страница"
    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"
    * пользователь (логинится с) данными
      | Логин  | bladekeeper@ya.ru |
      | Пароль | 1qwert2ASDF       |
    * пользователь (нажимает кнопку) "Войти"
    * открывается страница "Главная страница"
    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Прематч"
    * пользователь (нажимает кнопку) "Лайв-календарь"
    * открывается страница "Лайв-календарь"
    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"
    * пользователь (нажимает кнопку) "Очистить всё"
    * пользователь осуществляет переход в "Лайв-календарь"
    * открывается страница "Лайв-календарь"
    * пользователь (добавляет некорректные события, пока их не станет) "3"
    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"
    * пользователь (заполняет сумму для ставки) "Экспресс" "1"
    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)
    * пользователь осуществляет переход в "Лайв-календарь"
    * открывается страница "Лайв-календарь"
    * пользователь (добавляет корректные события, пока их не станет) "4"
    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"
    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)
    * пользователь осуществляет переход в "Лайв-календарь"
    * открывается страница "Лайв-календарь"
    * пользователь (добавляет корректные события, пока их не станет) "7"
    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"
    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)
    * пользователь (убирает события из купона, пока их не станет) "0"
    * пользователь осуществляет переход в "Лайв-календарь"
    * открывается страница "Лайв-календарь"
    * пользователь (добавляет корректные события, пока их не станет) "4"
    * пользователь осуществляет переход в "Купон"
    * пользователь (заполняет сумму для ставки) "Экспресс" "1"
    * пользователь (нажимает кнопку) "тип ставки"
    * пользователь (выбирает тип ставки) "Ординар"
    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)
    * пользователь (нажимает кнопку) "тип ставки"
    * пользователь (выбирает тип ставки) "Система"
    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)
    * пользователь (выбирает тип ставки) "Экспресс"
    * пользователь (нажимает кнопку) "бонусы"
    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)
    * пользователь (нажимает кнопку) "бонусы"
    * пользователь (нажимает кнопку) "фрибет"
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)
    * пользователь осуществляет переход в "Авторизованная Главная страница"
    * открывается страница "Авторизованная Главная страница"
    * пользователь (нажимает кнопку) "Иконка юзера"
    * открывается страница "Мини Личный Кабинет"
    * пользователь (нажимает кнопку) "Выйти"

    * (закрываем браузер)