package ru.gamble.stepdefs;

import cucumber.api.DataTable;
import cucumber.api.java.ru.Когда;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.remote.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.Stash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestApi {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRequest.class);

    private static Response reguestPOST(String path, Map<String,String> body) {
        JSONObject requestBody = new JSONObject();
        RestAssured.useRelaxedHTTPSValidation();
        Object paramValue=new Object();
        for (String name:body.keySet()){
            paramValue=body.get(name).matches("[_A-Z]*")?Stash.getValue(body.get(name)):body.get(name);
            if (body.get(name).isEmpty()){
                paramValue="";
            }
            if (body.get(name).equals("null")){
                paramValue=null;
            }
            requestBody.put(name,paramValue);
        }

        LOG.info("отправляем запрос :" + path + "\nтело:\n" + requestBody.toString());
        RequestSpecification request = RestAssured.given();
        Map<String,String> headers = Stash.getValue("headers");
        for (String name : headers.keySet()){
            LOG.info("Добавляем header " + name);
            request.header(name,headers.get(name));
        }
        request.body(requestBody.toString());
        Response response = request.post(path);
        return response;
    }

    private static Response reguestGET(String path){
        //JSONObject requestBody = new JSONObject();
        RestAssured.useRelaxedHTTPSValidation("TLS");
        RequestSpecification request = RestAssured.given().config(RestAssured.config().sslConfig(new SSLConfig().allowAllHostnames()));
        request.relaxedHTTPSValidation("TLS");
        request.log().all();
        Map<String,String> headers = Stash.getValue("headers");
        for (String name : headers.keySet()){
            LOG.info("Добавляем header " + name);
            request.header(name,headers.get(name));
        }
        Response response = request.get(path);

        return response;
    }

    @Когда("^отправляем http-запрос \"([^\"]*)\" \"([^\"]*)\"$")
    public static Response requestAndResponse(String type,String path,DataTable table){
        Response response = null;
        Map<String,String> headers2 = Stash.getValue("headers");//дополнительные headers
        Map<String,String> headers1 = new HashMap<>();//стандартные headers
//        headers1.put("Content-Type", "application/json");
        headers1.put("Content-Type", "application/json; charset=UTF-8");
//        headers1.put("charset","utf-8");
        if (headers2!=null){
            headers1.putAll(headers2);
        }
        Stash.put("headers",headers1);

        LOG.info("Отправляем запрос и запоминаем response");
        switch (type.toLowerCase()){
            case "post":
                response = reguestPOST(path,table.asMap(String.class,String.class));
                break;
            case "get":
                response = reguestGET(path);
                break;
        }
        LOG.info(response.getBody().asString());
        return  response;
    }

    /**
     * проверка что response содержит подстроки
     * @param keyStash - ключ, по которому в стэше хранится response
     * @param table - ожидаемые подстроки
     * @param memory - ожидаемые подстроки берем из памяти или нет
     */
    public static void checkResponse(String keyStash, DataTable table,boolean memory){
        Response response = Stash.getValue(keyStash);
        Map<String,String> bodyResponse = table.asMap(String.class,String.class);
        List<String> messages = new ArrayList<>();
        for (Map.Entry<String,String> entery:bodyResponse.entrySet()){
            messages.add(memory? Stash.getValue(entery.getValue()):entery.getValue());
        }
        for (int i=0;i<bodyResponse.size();i++){
            Assert.assertTrue("В теле ответа нет искомой подстроки '" + messages.get(i) + "'. Весь ответ: " + response.getBody().asString(),
                    response.getBody().asString().replaceAll(" ","").toLowerCase().contains(messages.get(i).replaceAll(" ","").toLowerCase()));
            LOG.info("В теле ответа есть подстрока " + messages.get(i));
        }
    }

    public static void checkResponse(String keyStash, DataTable table){
        checkResponse(keyStash,table,false);
    }

    public static void checkResponceByEmpty(String keyStash, String isEmpty) throws java.text.ParseException {
        Response response = Stash.getValue(keyStash);
        String actual = response.getBody().asString();
        actual = actual.replace("{\"code\":0,\"data\":", "").replace("}", "");
        boolean expectedEmpty = isEmpty.equals("пустой");
        Assert.assertTrue("Ожидалось что результат будет " + isEmpty + ", но это не так.\n" + actual,
                actual.equals("[]") == expectedEmpty);
        LOG.info("Да, RESPONCE действительно " + isEmpty);
    }

    public static Object collectParams(DataTable dataTable){
        // JSONArray authArray = new JSONArray();
        // Object params = new Object();
        JSONObject authparam = new JSONObject();
        String paramValue = new String();
        Map<String,String> table = dataTable.asMap(String.class,String.class);
        for (Map.Entry<String,String> entery:table.entrySet()){
            paramValue=entery.getValue();
            if (paramValue.matches("[A-Z]+")){
                if (Stash.getValue(entery.getValue())!=null &&  Stash.getValue(entery.getValue()).getClass().getName().contains("Object")){
                    paramValue=Stash.getValue(entery.getValue()).toString();
                }
                else {
                    paramValue=Stash.getValue(entery.getValue());
                }
            }
            if (entery.getValue().isEmpty()){
                paramValue="";
            }
            if (entery.getValue().equals("null")){
                paramValue=null;
            }
            authparam.put(entery.getKey(),paramValue);
        }
        //authArray.add(authparam);
        return authparam;
    }



}

