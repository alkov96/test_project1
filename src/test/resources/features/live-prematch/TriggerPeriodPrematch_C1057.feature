# language: ru
Функционал: 5.Лайв, Прематч и динамическое меню.
  Предыстория:
    * сохраняем в память
      | Период | 1 час |

    * сохраняем в память
      | Количество | 3 |

  @smoke
  @TriggerPeriodPrematch_C1057
  Сценарий: Проверка работы триггера "Фильтр по времени" левого меню в "Прематче".

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Просмотр событий"

    * пользователь (выбирает время) "Период"
    * пользователь (проверяет время игр) "Период" "Количество"

    * (закрываем браузер)

