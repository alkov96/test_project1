# language: ru
Функционал: 10_Разное

  @regress
  @ServiceMessageSiteTests_C1073
  Сценарий: Проверка сервисного сообщения (Отображается, вовремя скрывается, закрывается по нажатию на крестик).
    * переходит в админку
    * открывается страница "Логин в админку"

    * пользователь (логинится в админку)
    * открывается страница "Верхнее меню"

    * пользователь (нажимает кнопку) "Сервисы"
    * открывается страница "подменю Сервисы"

    * пользователь (нажимает кнопку) "Сервисные сообщения"
    * открывается страница "Сервисные сообщения"

    * пользователь (очищает все активные сообщения)
    * пользователь (нажимает кнопку) "Добавить сообщение"

    * открывается страница "Новое сервисное сообщение"
    * пользователь (создаёт новое сообщение с) данными
      | Активно через, мин    | -20      |
      | Активность            | Да       |
      | Можно скрыть          | Нет      |
      | Название              | Автотест |
      | Время активности, мин | 60       |

    * открывается страница "Сервисные сообщения"
    * переходит на главную страницу

    * открывается страница "Главная страница"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь (проверяет отсутствие иконки закрытия)
    * пользователь (нажимает кнопку) "Вход"

    * открывается страница "Вход"
    * пользователь (логинится с) данными
      | Логин  | Default |
      | Пароль | Default |

    * открывается страница "Главная страница"

    * пользователь осуществляет переход в "Авторизованная Главная страница"
    * открывается страница "Авторизованная Главная страница"

    * пользователь (нажимает кнопку) "Иконка юзера"
    * открывается страница "Мини Личный Кабинет"

    * пользователь (нажимает кнопку) "адрес почты"
    * открывается страница "Профиль"

    * пользователь (проверяет наличие сообщения с текстом) "Автотест"
    * пользователь осуществляет переход в "верхнее меню"

    * открывается страница "верхнее меню"
    * пользователь (нажимает кнопку) "Бонусы в личном кабинете"

    * открывается страница "Бонусы в личном кабинете"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "верхнее меню"
    * открывается страница "верхнее меню"

    * пользователь (нажимает кнопку) "История операций"
    * открывается страница "История операций"

    * пользователь (проверяет наличие сообщения с текстом) "Автотест"
    * пользователь осуществляет переход в "верхнее меню"

    * открывается страница "верхнее меню"
    * пользователь (нажимает кнопку) "Настройка уведомлений"

    * открывается страница "Настройка уведомлений"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Лайв"
    * открывается страница "Лайв просмотр событий"

    * пользователь осуществляет переход в "Лайв"
    * пользователь (нажимает кнопку) "События дня"

    * открывается страница "События дня"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Лайв"
    * пользователь (нажимает кнопку) "Лайв-обзор"

    * открывается страница "Лайв-обзор"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Лайв"
    * пользователь (нажимает кнопку) "Мультимонитор"

    * открывается страница "Мультимонитор"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Просмотр событий"

    * пользователь (проверяет наличие сообщения с текстом) "Автотест"
    * пользователь осуществляет переход в "Прематч"

    * пользователь (нажимает кнопку) "Лайв-календарь"
    * открывается страница "Лайв-календарь"

    * пользователь (проверяет наличие сообщения с текстом) "Автотест"
    * пользователь осуществляет переход в "Прематч"

    * пользователь (нажимает кнопку) "Результаты"
    * открывается страница "Результаты"

    * пользователь (проверяет наличие сообщения с текстом) "Автотест"
    * переходит на главную страницу

    * открывается страница "Главная страница"
    * пользователь (нажимает кнопку) "Бургер"

    * открывается страница "Бургер"
    * пользователь (нажимает кнопку) "Правила"

    * открывается страница "Основные положения"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Как пополнить баланс? в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Тарифы на ввод и вывод средств в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Публичная оферта в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Футбол в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Хоккей в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Баскетбол в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Прочие командные виды в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Теннис в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Авто- и мотоспорт в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Циклические виды спорта в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Единоборства в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Другие события в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Фрибет за миллион в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Получите фрибет на 1000 бонусов в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Супербет в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Экспресс-бонус в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Кэшаут в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в правилах"
    * открывается страница "Левое меню в правилах"

    * пользователь (нажимает кнопку) "Бонусы в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Бургер"
    * открывается страница "Бургер"

    * пользователь (нажимает кнопку) "Часто задаваемые вопросы"
    * открывается страница "Регистрация и заключение пари"

    * пользователь (проверяет наличие сообщения с текстом) "Автотест"
    * пользователь осуществляет переход в "Левое меню в часто задаваемых вопросах"

    * открывается страница "Левое меню в часто задаваемых вопросах"
    * пользователь (нажимает кнопку) "Технические вопросы в меню"

    * пользователь (проверяет наличие сообщения с текстом) "Автотест"
    * пользователь осуществляет переход в "Левое меню в часто задаваемых вопросах"

    * открывается страница "Левое меню в часто задаваемых вопросах"
    * пользователь (нажимает кнопку) "Словарь в меню"

    * пользователь (проверяет наличие сообщения с текстом) "Автотест"
    * переходит на главную страницу

    * открывается страница "Главная страница"
    * пользователь (нажимает кнопку) "Бургер"

    * открывается страница "Бургер"
    * пользователь (нажимает кнопку) "ЦУПИС"

    * открывается страница "ЦУПИС"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в интерактивных ставках"
    * открывается страница "Левое меню в интерактивных ставках"

    * пользователь (нажимает кнопку) "Интерактивные ставки и выигрыши в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в интерактивных ставках"
    * открывается страница "Левое меню в интерактивных ставках"

    * пользователь (нажимает кнопку) "Как начать пари? в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь осуществляет переход в "Левое меню в интерактивных ставках"
    * открывается страница "Левое меню в интерактивных ставках"

    * пользователь (нажимает кнопку) "Получите фрибет на 1000 бонусов в меню"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

  Сценарий: Закрытие сервисного сообщения
    * переходит в админку
    * открывается страница "Логин в админку"

    * пользователь (логинится в админку)
    * открывается страница "Верхнее меню"

    * пользователь (нажимает кнопку) "Сервисы"
    * открывается страница "подменю Сервисы"

    * пользователь (нажимает кнопку) "Сервисные сообщения"
    * открывается страница "Сервисные сообщения"

    * пользователь (очищает все активные сообщения)
    * пользователь (нажимает кнопку) "Добавить сообщение"

    * открывается страница "Новое сервисное сообщение"
    * пользователь (создаёт новое сообщение с) данными
      | Активно через, мин     | -20      |
      | Активность             | Да       |
      | Можно скрыть           | Да       |
      | Название               | Автотест |
      | Время активности, мин  | 60       |

    * открывается страница "Сервисные сообщения"
    * переходит на главную страницу

    * открывается страница "Главная страница"
    * пользователь (ждёт мс) "60000"

    * пользователь (перезагружает страницу)
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь (проверяет наличие иконки закрытия)
    * пользователь (нажимает кнопку) "Иконка закрытия сервисного сообщения"

    * пользователь (проверяет отсутствие сообщения с текстом после закрытия)

  Сценарий: Время отображения сообщения вышло
    * переходит в админку
    * открывается страница "Логин в админку"

    * пользователь (логинится в админку)
    * открывается страница "Верхнее меню"

    * пользователь (нажимает кнопку) "Сервисы"
    * открывается страница "подменю Сервисы"

    * пользователь (нажимает кнопку) "Сервисные сообщения"
    * открывается страница "Сервисные сообщения"

    * пользователь (очищает все активные сообщения)
    * пользователь (нажимает кнопку) "Добавить сообщение"

    * открывается страница "Новое сервисное сообщение"
    * пользователь (создаёт новое сообщение с) данными
      | Активно через, мин     | 0        |
      | Активность             | Да       |
      | Можно скрыть           | Да       |
      | Название               | Автотест |
      | Время активности, мин  | 2        |

    * открывается страница "Сервисные сообщения"
    * переходит на главную страницу

    * открывается страница "Главная страница"
    * пользователь (проверяет наличие сообщения с текстом) "Автотест"

    * пользователь (ждёт мс) "150000"
    * пользователь (перезагружает страницу)

    * пользователь (проверяет отсутствие сообщения с текстом после закрытия)

  Сценарий: Сообщение не активно
    * переходит в админку
    * открывается страница "Логин в админку"

    * пользователь (логинится в админку)
    * открывается страница "Верхнее меню"

    * пользователь (нажимает кнопку) "Сервисы"
    * открывается страница "подменю Сервисы"

    * пользователь (нажимает кнопку) "Сервисные сообщения"
    * открывается страница "Сервисные сообщения"

    * пользователь (очищает все активные сообщения)
    * пользователь (нажимает кнопку) "Добавить сообщение"

    * открывается страница "Новое сервисное сообщение"
    * пользователь (создаёт новое сообщение с) данными
      | Активно через, мин     | 0        |
      | Активность             | Нет      |
      | Можно скрыть           | Нет      |
      | Название               | Автотест |
      | Время активности, мин  | 2        |

    * открывается страница "Сервисные сообщения"
    * переходит на главную страницу

    * открывается страница "Главная страница"
    * пользователь (ждёт мс) "60000"

    * пользователь (перезагружает страницу)
    * пользователь (проверяет отсутствие сообщения с текстом)

    #* закрываем браузер







