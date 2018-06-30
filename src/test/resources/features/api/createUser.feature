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
      | PHONE  | randomPhone |
    * сохраняем в память
      | EMAIL  | randomEmail |
    * сохраняем в память
      | PASS  | Default |

  @api
  @createUser
  @correct
  Сценарий: 3_7	Создание пользователя. Позитивный кейс

    * переходит на главную страницу
    * открывается страница "Главная страница"
    * пользователь (получает смс-код для подтверждения телефона) "CODE"

    * запрос к API "api/mobile/v3/createUser" и сохраняем в "RESPONCE_API":
      | devId                 | DEVID      |
      | source                | 16         |
      | first_name            | FIRST_NAME |
      | surname               | SURNAME    |
      | patronymic            | PATRONYMIC |
      | birth_date            | BIRTH_DATE |
      | phone                 | PHONE      |
      | phoneConfirmationCode | CODE       |
      | email                 | EMAIL      |
      | pass                  | PASS       |

    * проверка ответа API из "RESPONCE_API":
      | exepted  |  "code":0,"data":{  |
