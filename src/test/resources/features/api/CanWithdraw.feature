# language: ru
Функционал: API

  Предыстория:
    * сохраняем в память
      | DEVID  | randomNumber 4 |
    * сохраняем в память
      | PASS  | Default |
    * сохраняем в память
      | ISSUEPLACE  | random |
    * сохраняем в память
      | CITY  | random |
    * сохраняем в память
      | STREET  | random |
    * сохраняем в память
      | BIRTHPLACE  | random |
    * сохраняем в память
      | HOUSE  | randomNumber 2 |
    * сохраняем в память
      | DOCNUM  | randomNumber 6 |
    * сохраняем в память
      | DOCSERIES  | randomNumber 4 |
    * сохраняем в память
      | FLAT  | randomNumber 2 |
    * сохраняем в память
      | GENDER | randomSex |

  @api
  @canWithdraw
  @correct
  Сценарий: Проверка доступных способов вывода для полностью зарегистрированного пользователя

    * поиск акаунта со статуом регистрации "=2" "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | authToken   | AUTHTOKEN |
      | source      | 16 |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "withdrawStatus":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |


  @api
  @canWithdraw
  @incorrect
  Сценарий: Проверка доступных способов вывода для не до конца зарегистрированного пользователя

    * поиск акаунта со статуом регистрации ">=9" "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | authToken   | AUTHTOKEN |
      | source      | 16 |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":12 |


  @api
  @canWithdraw
  @correct
  Сценарий: Проверка доступных способов вывода пользователя, не вводившего ПД (full,alternative)

    * поиск пользователя проходившего ускоренную регистрацию "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | authToken   | AUTHTOKEN |
      | source      | 16 |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "withdrawStatus":1 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |