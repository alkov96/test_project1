# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID  | randomNumber 4 |

    * сохраняем в память
      | PASSWORD  | Default |

    * поиск акаунта со статуом регистрации "=2" "EMAIL"


  @api
  @sendEmailForChangePassword
  @correct
  Сценарий: 3_27 Восстановление забытого пароля

    * запрос к API "api/mobile/v3/sendEmailForRestorePassword" и сохраняем в "RESPONCE_API":
      | devId       | DEVID   |
      | email       | EMAIL   |

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":0} |

    * проверка что в БД есть код для восстановления пароля "EMAIL"