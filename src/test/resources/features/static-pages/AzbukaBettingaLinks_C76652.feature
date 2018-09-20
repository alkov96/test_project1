# language: ru
Функционал: 6_Статические страницы

  @regress
  @AzbukaBettingaLinks_C76652
  Сценарий: Проверка ссылок на странице Азбука беттинга.
    * разлогиниваем пользователя

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Азбука беттинга"
    * открывается страница "Азбука беттинга"

    * (проверяем переход на страницы с) данными
      | ССЫЛКА                             | ТЕКСТ                                |
      | Что такое экспресс?                | Экспресс — пари на определенное     |
      | Что такое ординар?                 | Самое простое пари — ординар        |
      | Что такое система?                 | Система (система экспрессов)         |
      | Словарь терминов                   | Основные термины                     |
      | Игровые стратегии                  | Игровые стратегии                    |
      | Руководство для новичка            | Новичку                              |

    * (пролистываем страницу до блока 'Новичку')
    * (в блоке 'Новичку' проверяем ссылку на 'Термины')
    * (в блоке 'Новичку' проверяем ссылку на 'Стратегии')
    * (в блоке 'Новичку' проверяем ссылку на 'Курс молодого бойца')

    * (пролистываем страницу до блока 'Платформы')
    * (проверяем переход на страницы с) данными
      | ССЫЛКА                  | ТЕКСТ                         |
      | Facebook                | @bk888ru                      |
      | ВКонтакте               | 888.ru — онлайн-букмекер,    |
      | Youtube                 | 888ru Ставки на спорт         |
      | Для айфонов             | BK FAVORIT, OOO               |
      | Прематч-ставки          | Статистика                    |
      | Лайв-ставки             | Мультимонитор                 |

    * (нажимает на кнопку для загрузки приложения на android)
    * (проверяет скачивание приложения на android)
