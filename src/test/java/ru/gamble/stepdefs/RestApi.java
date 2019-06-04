package ru.gamble.stepdefs;

import com.google.gson.JsonArray;
import cucumber.api.DataTable;
import cucumber.api.java.ru.Когда;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.junit.Assert;
import org.openqa.selenium.remote.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.utility.JsonLoader;
import ru.sbtqa.tag.datajack.Stash;
import ru.sbtqa.tag.datajack.exceptions.DataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static ru.gamble.utility.Constants.STARTING_URL;

public class RestApi {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRequest.class);

    public static Response reguestPOST(String path, Map<String,String> body) {
        JSONObject requestBody = new JSONObject();
        RestAssured.useRelaxedHTTPSValidation();
        Object paramValue=new Object();
        for (String name:body.keySet()){
            paramValue=body.get(name).matches("[_A-Z]*")?Stash.getValue(body.get(name)):body.get(name);
            if (body.get(name).isEmpty()){
                paramValue="";
            }
            requestBody.put(name,paramValue);
        }

        LOG.info("отправляем запрос :" + path + "\nтело:\n" + requestBody.toString());
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("charset","utf-8");
        request.body(requestBody.toString());
        Response response = request.post(path);
        return response;
    }

    /**
     * тот же запрос post, только с указанными header
     * @param header - собственно все header, записанные в Map
     * @return
     */
    public static Response reguestPOST(String path, Map<String,String> body, Map<String,String> header ){
        JSONObject requestBody = new JSONObject();
        // RestAssured.useRelaxedHTTPSValidation();

        for (String name:body.keySet()){
            requestBody.put(name,body.get(name));
        }

        RequestSpecification request = RestAssured.given();
        for (String name : header.keySet()){
            request.header(name,header.get(name));
        }
        request.body(requestBody.toString());
        Response response = request.post(path);
        return response;
    }

    public static Response reguestGET(String path){
        //JSONObject requestBody = new JSONObject();
        RestAssured.useRelaxedHTTPSValidation();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        // request.body(requestBody.toString());
        Response response = request.get(path);
        return response;
    }

    @Когда("^отправляем http-запрос \"([^\"]*)\" \"([^\"]*)\"$")
    public static Response requestAndResponse(String type,String path,DataTable table){
        Response response = null;
//        Map<String,String> body = table.asMap(String.class,String.class);
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
                    response.getBody().asString().replaceAll(" ","").contains(messages.get(i).replaceAll(" ","")));
            LOG.info("В теле ответа есть подстрока " + messages.get(i));
        }
    }
    public static void checkResponse(String keyStash, DataTable table){
        checkResponse(keyStash,table,false);
    }

    public static String createTaskWithParams(String taskName, String params) throws DataException {
        Map<String,String> bodyRequest = new HashMap<>();
        bodyRequest.put("tasktype",taskName);
        bodyRequest.put("param", params);
        Response response = reguestPOST(JsonLoader.getData().get(STARTING_URL).get("HTTP_URL").getValue()+"/system/create_task",bodyRequest);
        String id = response.getBody().asString().substring(11,response.getBody().asString().length()-2);
        return id;
    }

    public static Object collectParams(DataTable dataTable){
       // JSONArray authArray = new JSONArray();
       // Object params = new Object();
        JSONObject authparam = new JSONObject();
        String paramValue = new String();
        Map<String,String> table = dataTable.asMap(String.class,String.class);
        for (Map.Entry<String,String> entery:table.entrySet()){
            paramValue=entery.getValue().matches("[A-Z]*")?Stash.getValue(entery.getValue()):entery.getValue();
            if (entery.getValue().isEmpty()){
                paramValue="";
            }
            authparam.put(entery.getKey(),paramValue);
        }
        //authArray.add(authparam);
        return authparam;
    }



}

