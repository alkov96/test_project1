#language: ru
Функционал: 10_Разное

  @regress
  @MyBettingFilters
  Сценарий: Мои пари - проверка фильтров и поиска.
    * разлогиниваем пользователя

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default |
      | Пароль | Default |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (нажимает кнопку) "Мои пари"

    * открывается страница "Мои пари"
    * пользователь (проверяет фильтры по типу ставки с) данными
     | ординар | экспресс | система |

    * пользователь (проверяет попадание ставок в диапазон дат)

    * пользователь (проверяет тип исхода с типом пари с) данными
    | Тип пари | Выбранный исход      |
    | Ординар  | !Экспресс & !Система |
    | Экспресс | Экспресс             |
    | система  | Система              |

    * (закрываем браузер)