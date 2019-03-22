  # language: ru
  Функционал: 3_Ставки

    @smoke
    @HistoryInCoupon
    @coupon

    Сценарий: Проверка вкладки "заключенные пари" в купоне

      * разлогиниваем пользователя
      * открывается страница "Главная страница"

      * пользователь (нажимает кнопку) "Прематч"
      * открывается страница "Прематч"
      * открывается страница "Купон"
      * пользователь (пытается перейти на вкладку в купоне) "Заключенные пари" "неуспешно"

      * открывается страница "Прематч"
      * пользователь (нажимает кнопку) "Вход"
      * открывается страница "Вход"

      * пользователь (логинится с) данными
        | Логин  | Default  |
        | Пароль | Default  |

      * открывается страница "Авторизованная Главная страница"

      * пользователь (открывает/закрывает Мои пари)
      * открывается страница "Мои пари"

      * отматывает дату начала на самую раннюю

      * (ищет и запоминает ожидаемые события по фильтру) "Все" "listAll"
      * (ищет и запоминает ожидаемые события по фильтру) "Ординар" "listOrdinar"
      * (ищет и запоминает ожидаемые события по фильтру) "Экспресс" "listExpress"
      * (ищет и запоминает ожидаемые события по фильтру) "Cистема" "listSystema"

      * пользователь (открывает/закрывает Мои пари)


      * открывается страница "Купон"
      * пользователь (пытается перейти на вкладку в купоне) "Заключенные пари" "успешно"

      * пользователь (включает фильтр в заключённых пари) "Все"
      * пользователь (запоминает первые события в заключенных пари) "listAllCoupon"

      * пользователь (включает фильтр в заключённых пари) "Ординар"
      * пользователь (запоминает первые события в заключенных пари) "listOrdinarCoupon"

      * пользователь (включает фильтр в заключённых пари) "Экспресс"
      * пользователь (запоминает первые события в заключенных пари) "listExpressCoupon"

      * пользователь (включает фильтр в заключённых пари) "Cистема"
      * пользователь (запоминает первые события в заключенных пари) "listSystemaCoupon"

      * пользователь (проверяет совпадение записей в купоне и в Моих Пари) "listAllCoupon" "listAll"
      * пользователь (проверяет совпадение записей в купоне и в Моих Пари) "listOrdinarCoupon" "listOrdinar"
      * пользователь (проверяет совпадение записей в купоне и в Моих Пари) "listExpressCoupon" "listExpress"
      * пользователь (проверяет совпадение записей в купоне и в Моих Пари) "listSystemaCoupon" "listSystema"

