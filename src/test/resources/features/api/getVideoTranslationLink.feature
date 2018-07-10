# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * сохраняем в память
      | USER  | Default |

    * сохраняем в память
      | PASS  | Default |

    * сохраняем в память
      | SOURCE | 16 |

    * сохраняем в память
      | PROVIDER_NAME | IMG |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID   |
      | email  | USER    |
      | pass   | PASS    |
      | source | SOURCE  |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/getVideoTranslationList" и сохраняем в "RESPONCE_API"

    * находим и сохраняем "DATA" из "RESPONCE_API"

    * достаём случайную видеотрансляцию из списка "DATA" и сохраняем в переменую "GAME_ID"

#    * достаём видеотрансляцию провайдера "PROVIDER_NAME" из списка "DATA" и сохраняем в переменую "GAME_ID"

    * достаём параметр из "GAME_ID" и сохраняем в переменую:
      | Параметр      | Переменная     |
      | gameId        | GAME_ID        |
      | providerName  | PROVIDER_NAME  |
      | translationId | TRANSLATION_ID |


  @api
  @getVideoTranslationLink
  @correct
  Сценарий: 3_29 Получение ссылки на определенную видео трансляцию от IMG. Позитивный кейс

    * запрос к API "api/mobile/v3/getVideoTranslationLink" и сохраняем в "RESPONCE_API":
    | authToken     | AUTHTOKEN      |
    | devId         | DEVID          |
    | source        | SOURCE         |
    | gameId        | GAME_ID        |
    | providerName  | PROVIDER_NAME  |
    | translationId | TRANSLATION_ID |

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":0,"data":{"videoTranslation": |

    * находим и сохраняем "VIDEOTRANSLATION" из "RESPONCE_API"

#    * сохраняем в память
#      | VIDEOTRANSLATION | https://stream.betconstruct.tv:7791/927249/index.m3u8?token=2cc3b0ab44698366991fd6f68fa068a0-1531218960-325-16 |
#  "https:\/\/dge.imggaming.com\/api\/v2\/streaming\/events\/2\/stream?operatorId=91&auth=fe6e49d3f47130c5f34aa57b4c581a19&timestamp=1531222404558&thumbnail=true"
# {"statusCode":401,"statusText":"Unauthorized","message":null}
    * запрос к IMG "VIDEOTRANSLATION" и сохраняем в "RESPONCE_IMG"

#    * проверка вариантов ответа
#
#    * проверка полей и типов в ответе "RESPONCE_IMG":
#      | Параметр     | Тип     |
#      | eventId      | Integer |
#      | rtmpUrl      | String  |
#      | hlsUrl       | String  |
#      | thumbnailUrl | String  |
#      | statusCode   | Integer |
#
#
#    * находим и сохраняем "DATA" из "RESPONCE_API"
#
#    * проверка полей и типов в ответе "DATA":
#      | Параметр          | Тип    |
#      | videoTranslations | List   |
#      | providerName      | String |
#      | translationId     | String |


#  switch (providerData.providerName) {
#  case 'IMG':
#  if (!videoData.streamUrl) {
#  return $q.reject('No streamUrl field in IMG video data');
#  }
#
#  // IMG can return direct .m3u8 URL or URL to JSON object containing it
#  if (/\.m3u8(?:[^a-z0-9]|$)/i.test(videoData.streamUrl)) {// direct .m3u8 URL
#  return {
#  url: videoData.streamUrl,
#  withCredentials: false
#  };
#  } else {
#  // URL to JSON object. Fetch it to get .m3u8 URL
#  return Utils.simpleRequest(videoData.streamUrl)
#  .then(function (responseData) {
#  var data;
#
#  try {
#  if (responseData !== '') {
#  data = JSON.parse(responseData);
#
#  if (data.hlsUrl) {
#  return {
#  url: data.hlsUrl,
#  withCredentials: true
#  };
#  }
#  }
#  } catch (e) {
#  }
#
#  return $q.reject('Bad IMG data response');
#  });
#  }
#  }