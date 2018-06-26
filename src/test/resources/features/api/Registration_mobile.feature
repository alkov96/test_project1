# language: ru
Функционал: API

    Предыстория:
    * сохраняем в память
      | FIRST_NAME | random |
    * сохраняем в память
      | SURNAME | random |
    * сохраняем в память
      | PATRONYMIC | random |
    * сохраняем в память
      | BIRTH_DATE | randomdate |
    * сохраняем в память
      | CODE | random |
    * сохраняем в память
      | DEVID  | Default |
    * сохраняем в память
      | PHONE  | Default |
    * сохраняем в память
      | EMAIL  | Default |
    * сохраняем в память
      | PASS  | Default |

  @regress
  @Registration_mobile

  Сценарий: Мобильная регистрация


#    * запрос к API "api/mobile/v3/sendPhoneCode":
#      | devId  | Default                           |
#      | phone  | Default |
#
#    * проверка ответа API:
#      | exepted  |  "code":0  |

    * переходит на главную страницу
    * открывается страница "Главная страница"
    * пользователь (получает смс-код для подтверждения телефона) "CODE"

    * запрос к API "api/mobile/v3/createUser":
      | devId  | DEVID |
      | source  | 16 |
      | first_name    |  FIRST_NAME|
      | surname    |  SURNAME |
      | patronymic    |  PATRONYMIC |
      | birth_date    |  BIRTH_DATE |
      | phone    |  PHONE  |
      | phoneConfirmationCode    |  CODE  |
      | email    |  EMAIL  |
      | pass    |  PASS  |
