# language: ru
Функционал: API

  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * сохраняем в память
      | USER  | Default |

    * сохраняем в память
      | PASS  | Default |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID |
      | email  | USER  |
      | pass   | PASS  |
      | source | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * сохраняем в память
      | PHONE  | randomPhone |

  @api
  @requestPhoneCall
  @correct
  Сценарий: 3_25 Запросить/отменить обратный телефонный звонок. Позитивный кейс

    * запрос к API "api/mobile/v3/requestPhoneCall" и сохраняем в "RESPONCE_API":
      | devId           | DEVID     |
      | authToken       | AUTHTOKEN |
      | source          | 16        |
      | phone           | 16        |
      | comment         |           |
      | operatingSystem |           |
      | appVersion      |           |
      | phone           | PHONE     |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "CALLBACKREQUESTS" из "RESPONCE_API"
