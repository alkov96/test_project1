#language: ru
Функционал: 10_Разное

  @regress
  @MyBettingFilterAndSearch
  Сценарий: Мои пари - проверка фильтра по типу ставки и поиска.

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (нажимает кнопку) "Мои пари"

    * открывается страница "Мои пари"

    * пользователь (проверяет сортировку по сумме)

    * пользователь (проверяет попадание ставок в диапазон дат)

    * пользователь (переключается на другую дату)

    * пользователь (проверяет попадание ставок в диапазон дат)

    * пользователь (проверяет фильтр по типу ставки) данными
    | Тип пари | Выбранный исход      |
    | Ординар  | !Экспресс & !Система |
    | Экспресс | Экспресс             |
    | система  | Система              |

    * пользователь (проверяет фильтр по типу исхода ставки) данными
      | Исход ставки | Выбранный исход ставки  |
      | Ожидается    | Ожидается               |
      | Проигрыш     | Проигрыш                |
      | Возврат      | Возврат                 |
      | Выигрыш      | Выигрыш                 |
      | Кэшаут       | Кэшаут                  |
      | Все          | Все                     |



    * пользователь (проверяет поиск, вводит в поисковую строку: ) "id"
    * пользователь (проверяет поиск, вводит в поисковую строку: ) "12345678"

    * закрываем браузер