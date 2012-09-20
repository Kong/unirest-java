package com.mashape.client.test.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.File;
import org.json.JSONObject;
import org.json.JSONArray;

import com.mashape.client.authentication.Authentication;
import com.mashape.client.authentication.AuthenticationParameter;
import com.mashape.client.authentication.MashapeAuthentication;
import com.mashape.client.authentication.QueryAuthentication;
import com.mashape.client.authentication.HeaderAuthentication;
import com.mashape.client.authentication.BasicAuthentication;
import com.mashape.client.http.ContentType;
import com.mashape.client.http.HttpClient;
import com.mashape.client.http.HttpMethod;
import com.mashape.client.http.MashapeCallback;
import com.mashape.client.http.MashapeResponse;
import com.mashape.client.http.ResponseType;
import com.mashape.client.http.utils.HttpUtils;
import java.util.AbstractMap;
public class Test {

	private final static String PUBLIC_DNS = "pippo-test.p.mashape.com";
    private List<Authentication> authenticationHandlers;

    public Test (String publicKey, String privateKey, String apikey, String password) {
        authenticationHandlers = new LinkedList<Authentication>();
        authenticationHandlers.add(new MashapeAuthentication(publicKey, privateKey));
        authenticationHandlers.add(new QueryAuthentication(new AuthenticationParameter("apikey", apikey), new AuthenticationParameter("password", password)));
        
    }
    
    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> herro(String name, String nick) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (name != null && !name.equals("")) {
	parameters.put("name", name);
        }
        
        
        if (nick != null && !nick.equals("")) {
	parameters.put("nick", nick);
        }
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/hello/" + HttpUtils.encodeUrl(parameters.get("name").toString()) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Synchronous call without optional parameters.
     */
    public MashapeResponse<JSONObject> herro(String name) {
        return herro(name, "");
    }


    /**
     * Asynchronous call with optional parameters.
     */
    public Thread herro(String name, String nick, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (name != null && !name.equals("")) {
        
            parameters.put("name", name);
        }
        
        
        if (nick != null && !nick.equals("")) {
        
            parameters.put("nick", nick);
        }
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/hello/" + HttpUtils.encodeUrl(parameters.get("name").toString()) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Asynchronous call without optional parameters.
     */
    public Thread herro(String name, MashapeCallback<JSONObject> callback) {
        return herro(name, "", callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> touch() {
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/touch",
                    null,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread touch(MashapeCallback<JSONObject> callback) {
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/touch",
                    null,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }
}