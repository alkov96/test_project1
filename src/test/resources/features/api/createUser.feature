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
      | BIRTH_DATE | randomDate |
    * сохраняем в память
      | CODE | random |
    * сохраняем в память
      | DEVID  | randomNumber 4 |
#      | DEVID  | Default |
    * сохраняем в память
      | PHONE  | randomPhone |
    * сохраняем в память
      | EMAIL  | randomEmail |
    * сохраняем в память
      | PASSWORD  | Default |

      * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
        | devId | DEVID |
        | phone | PHONE |

      * проверка ответа API из "RESPONCE_API":
        | exepted | "code":0 |

      * получаем и сохраняем в память код подтверждения "CODE" телефона "PHONE"


  @createUser
  @correct
  Сценарий: 3_7	Создание пользователя. Позитивный кейс

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
      | password                  | PASSWORD       |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data":{"status": |
