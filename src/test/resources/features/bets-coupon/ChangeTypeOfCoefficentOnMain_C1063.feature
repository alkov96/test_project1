# language: ru
Функционал: 3_Ставки

  @regress
  @ChangeTypeOfCoefficientOnMain_C1066
  Сценарий: Изменение типа отображения коэффицентов на главной

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (ищет доступные коэффиценты на Главной)
    * пользователь (переходит в настройки и меняет коэффицент на Главной)

    #* закрываем браузер
