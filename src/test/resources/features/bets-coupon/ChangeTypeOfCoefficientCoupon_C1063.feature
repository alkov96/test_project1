# language: ru
Функционал: 3_Ставки

  @regress
  @ChangeTypeOfCoefficientCoupon_C1066
  Сценарий: Изменение типа отображения коэффицентов в купоне

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Бургер"
    * открывается страница "Бургер"

    * пользователь (нажимает кнопку) "События дня"
    * открывается страница "События дня"

    * пользователь (добавляет событие с баннера в купон)
    * пользователь осуществляет переход в "Купон"

    * открывается страница "Купон"
    * пользователь (переходит в настройки и меняет коэффицент в купоне)

    #* закрываем браузер

