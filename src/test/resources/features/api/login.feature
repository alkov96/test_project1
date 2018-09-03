# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * сохраняем в память
      | USER  | Default |

    * сохраняем в память
      | PASSWORD  | Default |

    * сохраняем в память
      | SOURCE | 16 |

  @api
  @login
  @correct
  Сценарий: 3_19 Аутентификация пользователя. Позитивный кейс

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId    | DEVID    |
      | email    | USER     |
      | pass     | PASSWORD |
      | source   | SOURCE   |

    * проверка ответа API из "RESPONCE_API":
    | exepted | "code":0,"data":{ |

  @fail
  Сценарий: 3_19 Аутентификация пользователя. Негативный кейс

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId    | 0 |
      | email    | 0 |
      | pass     | 0 |
      | source   | 0 |

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":1,"data":{"message": |


