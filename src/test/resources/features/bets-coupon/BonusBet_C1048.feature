 # language: ru
 Функционал: 3_Ставки

   @smoke
   @BonusBet_C1048
   Сценарий: Проверка нескольких ставок на бонусы

     * разлогиниваем пользователя
     * открывается страница "Главная страница"

     * пользователь (нажимает кнопку) "Вход"
     * открывается страница "Вход"

     * пользователь (логинится с) данными
       | Логин  | Default  |
       | Пароль | Default  |

     * открывается страница "Авторизованная Главная страница"
     * пользователь (запоминает значение баланса) "бонусов"

     * пользователь (нажимает кнопку) "Прематч"
     * открывается страница "Прематч"

     * пользователь (нажимает кнопку) "Лайв-календарь"
     * открывается страница "Лайв-календарь"

     * пользователь (в меню выбора видов спорта выбирает) "Все Виды Спорта"
     * пользователь (выбирает следующий день недели с более чем событиями) "1"

     * пользователь (очищает купон)
     * пользователь (добавляет ставки из разных событий в количестве) "1"

     * пользователь осуществляет переход в "Купон"
     * открывается страница "Купон"

     * пользователь (выбирает тип ставки) "Ординар"
     * пользователь (вводит сумму одной ставки Ординар) "1"

     * пользователь (выбирает ставку бонусами)
     * пользователь (заключает пари)

     * пользователь (проверяет изменение баланса) "бонусов"
     * закрываем браузер

