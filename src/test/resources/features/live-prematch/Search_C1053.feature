# language: ru
Функционал: 5_Лайв, Прематч и динамическое меню

  @regress
  @Search_C1053
  Сценарий: Проверка работы поиска в левом меню.
    * разлогиниваем пользователя

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Лайв"
    * открывается страница "Лайв просмотр событий"
    * пользователь (находит игру по фильтру видео) "без видео" "и запоминает название"
    * пользователь (вычленяет из названия игры одно слово) "searchLive" "LiveWithoutVideo"
    * пользователь (выставляет фильтр по видео на) "включен"

    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Просмотр событий"

    * открывается страница "Просмотр событий"
    * пользователь (находит игру по фильтру) "2 часа" "позже" "и запоминает название"
    * пользователь (вычленяет из названия игры одно слово) "searchPrematch" "PrematchVnePeriod"
    * пользователь (включает фильтр по времени) "1 час"

    * открывается страница "Поиск в левом меню"

    * пользователь (вводит в поле поиска текст) "searchLive"

