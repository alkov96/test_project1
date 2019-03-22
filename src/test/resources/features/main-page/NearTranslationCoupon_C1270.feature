# language: ru
Функционал: 4_Главная страница

  @regress
  @NearTranslationCoupon_C1270
  @coupon

  Сценарий: Главная страница. Добавление ставки в купон с виджета "Ближайшие трансляции".

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Прематч"
    * пользователь (очищает купон)
    * пользователь (нажимает кнопку) "На главную"
    * открывается страница "Главная страница"

    * пользователь (добавляет коэф с виджета в купон и сохраняет название команд, коэф и исход) "Ближайшие трансляции" данными
      | team1key |
      | team2key |
      | ishodKey |
      | coefKey  |

    * пользователь осуществляет переход в "Купон"

    * открывается страница "Купон"
    * пользователь (проверяет, совпадают ли события в купоне с ожидаемыми из) "team1key" "team2key"

    * пользователь (проверяет, совпадает ли исход в купоне с ожидаемым) "ishodKey"
    * пользователь (сравнивает коэфиценты) "coefKey"

