# language: ru
Функционал: API

  @browserIndependent
  @COLLECT_VIDEO_IDENT_DATA
  Сценарий: COLLECT_VIDEO_IDENT_DATA с двумя параметрами правильными
    * формирум параметры и сохраняем в "PARAMS"
      | ts      | прошлый месяц       |
      | ts_end  | текущая дата-время  |
    * запрос типа COLLECT "api/get_video_ident_data" c параметрами "PARAMS" и сохраняем в "RESPONCE"

    * проверка ответа API из "RESPONCE":
      | exepted     | "code":0 |
    * проверка что ответ "RESPONCE" "не пустой"
    * проверка что в ответе "RESPONCE" верные даты  "PARAMS":


  @browserIndependent
  @COLLECT_VIDEO_IDENT_DATA
  Сценарий: COLLECT_VIDEO_IDENT_DATA с двумя параметрами, перепутанными местами (ожидается пустой ответ)
    * формирум параметры и сохраняем в "PARAMS"
      | ts      | следующий месяц     |
      | ts_end  | текущая дата-время  |
    * запрос типа COLLECT "api/get_video_ident_data" c параметрами "PARAMS" и сохраняем в "RESPONCE"

    * проверка что ответ "RESPONCE" "пустой"


  @browserIndependent
  @COLLECT_VIDEO_IDENT_DATA
  Сценарий: COLLECT_VIDEO_IDENT_DATA с одним параметром
    * формирум параметры и сохраняем в "PARAMS"
      | ts      | прошлый месяц     |
    * запрос типа COLLECT "api/get_video_ident_data" c параметрами "PARAMS" и сохраняем в "RESPONCE"

    * проверка ответа API из "RESPONCE":
      | exepted     | "code":0 |
    * проверка что ответ "RESPONCE" "не пустой"
    * проверка что в ответе "RESPONCE" верные даты  "PARAMS":


  @browserIndependent
  @COLLECT_VIDEO_IDENT_DATA
  Сценарий: COLLECT_VIDEO_IDENT_DATA проверка что разница в 1 минуту - работает

    * формирум параметры и сохраняем в "PARAMS"
      | ts      | прошлый месяц       |
      | ts_end  | текущая дата-время  |
    * запрос типа COLLECT "api/get_video_ident_data" c параметрами "PARAMS" и сохраняем в "RESPONCE"

    * проверка ответа API из "RESPONCE":
      | exepted     | "code":0 |
    * проверка что ответ "RESPONCE" "не пустой"
    * проверка что в ответе "RESPONCE" верные даты  "PARAMS":

    * выбираем одну дату из "RESPONCE" и сохраняем в "VIDEOIDENTDATE" а id_user в "ID"
    * формирум параметры и сохраняем в "PARAMS"
      | ts      | прошлый месяц  |
      | ts_end  | VIDEOIDENTDATE  |
    * запрос типа COLLECT "api/get_video_ident_data" c параметрами "PARAMS" и сохраняем в "RESPONCE"

    * проверка что в ответе "RESPONCE" нет юзера с "ID"


  @browserIndependent
  @COLLECT_VIDEO_IDENT_DATA
  Сценарий: COLLECT_VIDEO_IDENT_DATA с некорректной датой(ожидается ошибка)
    * формирум параметры и сохраняем в "PARAMS"
      | ts      | 2018-13-07T11:01:10     |
    * запрос типа COLLECT "api/get_video_ident_data" c параметрами "PARAMS" и сохраняем в "RESPONCE"
    * проверка ответа API из "RESPONCE":
      | exepted     | Error |







