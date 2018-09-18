# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | SOURCE  | 16 |

    * сохраняем в память
      | DEVID  | randomNumber 4 |

    * сохраняем в память
      | PASSWORD  | Default |

    * поиск акаунта со статуом регистрации "=2" "EMAIL"

    * сохраняем в память
      | NEW_PASS  | 123Parol |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID  |
      | email  | EMAIL  |
      | pass   | PASSWORD   |
      | source | SOURCE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data":{ |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

  @api
  @changePassword
  @correct
  Сценарий: 3_30 Смена пароля. Позитивный кейс

    * запрос к API "api/mobile/v3/changePassword" и сохраняем в "RESPONCE_API":
      | devId       | DEVID     |
      | authToken   | AUTHTOKEN |
      | source      | SOURCE    |
      | oldPassword | PASSWORD      |
      | newPassword | NEW_PASS  |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data" |

    # Проверяем, что залогиниваемя с новым паролем
    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID    |
      | email  | EMAIL    |
      | pass   | NEW_PASS |
      | source | SOURCE   |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data" |

   # Возвращаем пароль обратно

    * запрос к API "api/mobile/v3/changePassword" и сохраняем в "RESPONCE_API":
      | devId       | DEVID     |
      | authToken   | AUTHTOKEN |
      | source      | SOURCE    |
      | oldPassword | NEW_PASS  |
      | newPassword | PASSWORD      |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data" |

 # Проверяем, что залогиниваемя с новым паролем
    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID    |
      | email  | EMAIL    |
      | pass   | PASSWORD     |
      | source | SOURCE   |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data" |

