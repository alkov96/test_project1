# language: ru
Функционал: 3_Ставки

  Предыстория:
    * редактируем некоторые активные опции сайта
    |express_bonus|true|
  @smoke
  @ExpressBonus_C39773

  @correct
  Сценарий: Экспресс бонус. Позитивный кейс.

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default |
      | Пароль | Default |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (нажимает кнопку) "Прематч"

    * открывается страница "Прематч"
    * пользователь (нажимает кнопку) "Лайв-календарь"

    * открывается страница "Лайв-календарь"
    * пользователь (в меню выбора видов спорта выбирает) "Все Виды Спорта"
    * пользователь (выбирает следующий день недели с более чем событиями) "1"

    * пользователь (очищает купон)

    * пользователь (ищет событие с коэффициентом) "1.25"
    * пользователь (добавляет корректные события, пока их не станет) "2"

    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"

    * пользователь (выбирает тип ставки) "Экспресс"
    * пользователь (вводит сумму ставки экспресс) "1"

    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)

    * открывается страница "Лайв-календарь"

    * пользователь (добавляет корректные события, пока их не станет) "3"

    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"

    * пользователь (выбирает тип ставки) "Экспресс"
    * пользователь (вводит сумму ставки экспресс) "1"
    * пользователь (проверяет корректность ссылки О бонусах к экспрессу и текста о бонусе)

    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)

    * открывается страница "Лайв-календарь"
    * пользователь (добавляет корректные события, пока их не станет) "4"

    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"

    * пользователь (выбирает тип ставки) "Экспресс"
    * пользователь (вводит сумму ставки экспресс) "1"
    * пользователь (проверяет корректность ссылки О бонусах к экспрессу и текста о бонусе)

    * пользователь (проверяет наличие бонуса к возможному выигрышу)

    * пользователь (выбирает ставку бонусами)

    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)

    * пользователь (убирает события из купона, пока их не станет) "2"

    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)


  @fail
  Сценарий: Экспресс бонус. Негативный кейс.

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default |
      | Пароль | Default |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (нажимает кнопку) "Прематч"

    * открывается страница "Прематч"
    * пользователь (нажимает кнопку) "Лайв-календарь"

    * пользователь осуществляет переход в "Лайв-календарь"
    * открывается страница "Лайв-календарь"

    * пользователь (в меню выбора видов спорта выбирает) "Все Виды Спорта"

    * пользователь (очищает купон)

    * пользователь (добавляет некорректные события, пока их не станет) "1"
    * пользователь (добавляет корректные события, пока их не станет) "3"
    * пользователь осуществляет переход в "Купон"

    * открывается страница "Купон"
    * пользователь (выбирает тип ставки) "Экспресс"
    * пользователь (вводит сумму ставки экспресс) "1"

    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)

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

    * открывается страница "Лайв-календарь"

    * пользователь (добавляет корректные события, пока их не станет) "4"

    * пользователь осуществляет переход в "Купон"
    * пользователь (выбирает тип ставки) "Экспресс"

    * пользователь (выбирает тип ставки) "Ординар"

    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)

    * пользователь (выбирает тип ставки) "Система"

    * пользователь (проверяет отсутствие ссылки О бонусах к экспрессу и текста о бонусе)
    * пользователь (проверяет отсутствие бонуса к возможному выйгрышу)

    * пользователь (выбирает тип ставки) "Экспресс"
    * пользователь (включает фрибет если есть и проверяет наличие экспресс-бонуса)

