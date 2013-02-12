
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.client.authentication.Authentication;
import com.mashape.client.authentication.AuthenticationParameter;
import com.mashape.client.authentication.HeaderAuthentication;
import com.mashape.client.authentication.MashapeAuthentication;
import com.mashape.client.http.ContentType;
import com.mashape.client.http.HttpClient;
import com.mashape.client.http.HttpMethod;
import com.mashape.client.http.MashapeCallback;
import com.mashape.client.http.MashapeResponse;
import com.mashape.client.http.ResponseType;
import com.mashape.client.http.utils.HttpUtils;

public class APIv3 {

	private final static String PUBLIC_DNS = "imgur-apiv3.p.mashape.com";
    private List<Authentication> authenticationHandlers;

    public APIv3 (String mashapeKey, String authorizationValue) {
        authenticationHandlers = new LinkedList<Authentication>();
        authenticationHandlers.add(new MashapeAuthentication(mashapeKey));
        authenticationHandlers.add(new HeaderAuthentication(new AuthenticationParameter("Authorization", authorizationValue)));
    }
    
    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> account(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread account(String username, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountAlbumCount(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/albums/count",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountAlbumCount(String username, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/albums/count",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountAlbumDeletion(String username, String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountAlbumDeletion(String username, String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountAlbumIds(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/albums/ids",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountAlbumIds(String username, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/albums/ids",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountAlbumInformation(String username, String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountAlbumInformation(String username, String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountAlbums(String username, String page) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (page != null && !page.equals("")) {
	parameters.put("page", page);
        }
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/albums",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Synchronous call without optional parameters.
     */
    public MashapeResponse<JSONObject> accountAlbums(String username) {
        return accountAlbums(username, "");
    }


    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountAlbums(String username, String page, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        if (page != null && !page.equals("")) {
        
            parameters.put("page", page);
        }
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/albums",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Asynchronous call without optional parameters.
     */
    public Thread accountAlbums(String username, MashapeCallback<JSONObject> callback) {
        return accountAlbums(username, "", callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<String> accountCommentDeletion(String username, String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return (MashapeResponse<String>) HttpClient.doRequest(String.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountCommentDeletion(String username, String _id, MashapeCallback<String> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return HttpClient.doRequest(String.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountComments(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/comments",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountComments(String username, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/comments",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<String> accountCreation(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<String>) HttpClient.doRequest(String.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/https://api.imgur.com/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountCreation(String username, MashapeCallback<String> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(String.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/https://api.imgur.com/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountDeletion(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountDeletion(String username, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountImageInformation(String username, String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountImageInformation(String username, String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountImages(String username, String page) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/images/" + ((HttpUtils.encodeUrl(page) == null) ? "" : HttpUtils.encodeUrl(page)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountImages(String username, String page, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/images/" + ((HttpUtils.encodeUrl(page) == null) ? "" : HttpUtils.encodeUrl(page)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountImagesCount(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/images/count",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountImagesCount(String username, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/images/count",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountImagesIds(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/images/ids",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountImagesIds(String username, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/images/ids",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountLikes(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/likes",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountLikes(String username, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/likes",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<String> accountMessages(String username, String _new) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (_new != null && !_new.equals("")) {
	parameters.put("new", _new);
        }
        
        
        
        return (MashapeResponse<String>) HttpClient.doRequest(String.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/notifications/messages",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers);
    }

    /**
     * Synchronous call without optional parameters.
     */
    public MashapeResponse<String> accountMessages(String username) {
        return accountMessages(username, "");
    }


    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountMessages(String username, String _new, MashapeCallback<String> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        if (_new != null && !_new.equals("")) {
        
            parameters.put("new", _new);
        }
        
        
        return HttpClient.doRequest(String.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/notifications/messages",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Asynchronous call without optional parameters.
     */
    public Thread accountMessages(String username, MashapeCallback<String> callback) {
        return accountMessages(username, "", callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountNotifications(String username, String type) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/notifications/" + ((HttpUtils.encodeUrl(type) == null) ? "" : HttpUtils.encodeUrl(type)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountNotifications(String username, String type, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/notifications/" + ((HttpUtils.encodeUrl(type) == null) ? "" : HttpUtils.encodeUrl(type)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<String> accountSendMessage(String username, String body, String subject, String parentid) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (body != null && !body.equals("")) {
	parameters.put("body", body);
        }
        
        
        
        if (subject != null && !subject.equals("")) {
	parameters.put("subject", subject);
        }
        
        
        
        if (parentid != null && !parentid.equals("")) {
	parameters.put("parent_id", parentid);
        }
        
        
        
        return (MashapeResponse<String>) HttpClient.doRequest(String.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/message",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers);
    }

    /**
     * Synchronous call without optional parameters.
     */
    public MashapeResponse<String> accountSendMessage(String username, String body) {
        return accountSendMessage(username, body, "", "");
    }


    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountSendMessage(String username, String body, String subject, String parentid, MashapeCallback<String> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        if (body != null && !body.equals("")) {
        
            parameters.put("body", body);
        }
        
        
        
        if (subject != null && !subject.equals("")) {
        
            parameters.put("subject", subject);
        }
        
        
        
        if (parentid != null && !parentid.equals("")) {
        
            parameters.put("parent_id", parentid);
        }
        
        
        return HttpClient.doRequest(String.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/message",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Asynchronous call without optional parameters.
     */
    public Thread accountSendMessage(String username, String body, MashapeCallback<String> callback) {
        return accountSendMessage(username, body, "", "", callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountSettings() {
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/Returns the account settings, only accessible if you're logged in as the user.",
                    null,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountSettings(MashapeCallback<JSONObject> callback) {
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/Returns the account settings, only accessible if you're logged in as the user.",
                    null,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> accountStatistics(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/stats",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread accountStatistics(String username, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/stats",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> addAlbumImages(String ids, String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (ids != null && !ids.equals("")) {
	parameters.put("ids[]", ids);
        }
        
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread addAlbumImages(String ids, String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (ids != null && !ids.equals("")) {
        
            parameters.put("ids[]", ids);
        }
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> album(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread album(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<String> albumImage() {
        
        return (MashapeResponse<String>) HttpClient.doRequest(String.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/",
                    null,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread albumImage(MashapeCallback<String> callback) {
        
        return HttpClient.doRequest(String.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/",
                    null,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> albumImages(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/images",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread albumImages(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/images",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> albumUpload(String ids) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (ids != null && !ids.equals("")) {
	parameters.put("ids[]", ids);
        }
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/album",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Synchronous call without optional parameters.
     */
    public MashapeResponse<JSONObject> albumUpload() {
        return albumUpload("");
    }


    /**
     * Asynchronous call with optional parameters.
     */
    public Thread albumUpload(String ids, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (ids != null && !ids.equals("")) {
        
            parameters.put("ids[]", ids);
        }
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/album",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Asynchronous call without optional parameters.
     */
    public Thread albumUpload(MashapeCallback<JSONObject> callback) {
        return albumUpload("", callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> albumDelete(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread albumDelete(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/album/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> comment(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread comment(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<String> commentCount(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<String>) HttpClient.doRequest(String.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/comments/count",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread commentCount(String username, MashapeCallback<String> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(String.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/comments/count",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> commentDelete(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread commentDelete(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<String> commentIds(String username) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<String>) HttpClient.doRequest(String.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/comments/ids",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread commentIds(String username, MashapeCallback<String> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(String.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/comments/ids",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> commentReplies(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/replies",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread commentReplies(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/replies",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> commentVote(String _id, String vote) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/vote/" + ((HttpUtils.encodeUrl(vote) == null) ? "" : HttpUtils.encodeUrl(vote)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread commentVote(String _id, String vote, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/vote/" + ((HttpUtils.encodeUrl(vote) == null) ? "" : HttpUtils.encodeUrl(vote)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> deleteAccountImage(String username, String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread deleteAccountImage(String username, String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> gallery(String sort, String page) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/" + ((HttpUtils.encodeUrl(sort) == null) ? "" : HttpUtils.encodeUrl(sort)) + "/" + ((HttpUtils.encodeUrl(page) == null) ? "" : HttpUtils.encodeUrl(page)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread gallery(String sort, String page, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/" + ((HttpUtils.encodeUrl(sort) == null) ? "" : HttpUtils.encodeUrl(sort)) + "/" + ((HttpUtils.encodeUrl(page) == null) ? "" : HttpUtils.encodeUrl(page)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> galleryCommentCreation(String comment, String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (comment != null && !comment.equals("")) {
	parameters.put("comment", comment);
        }
        
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comment",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread galleryCommentCreation(String comment, String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (comment != null && !comment.equals("")) {
        
            parameters.put("comment", comment);
        }
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comment",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> galleryCommentReply(String comment, String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (comment != null && !comment.equals("")) {
	parameters.put("comment", comment);
        }
        
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread galleryCommentReply(String comment, String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (comment != null && !comment.equals("")) {
        
            parameters.put("comment", comment);
        }
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> galleryImage(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread galleryImage(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> galleryImageCommentCount(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comments/count",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread galleryImageCommentCount(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comments/count",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> galleryImageCommentDelete(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread galleryImageCommentDelete(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> galleryImageCommentIds(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comments/ids",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread galleryImageCommentIds(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comments/ids",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> galleryImageComments(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comments",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread galleryImageComments(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/comments",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> galleryImageVote(String _id, String vote) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/vote/" + ((HttpUtils.encodeUrl(vote) == null) ? "" : HttpUtils.encodeUrl(vote)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread galleryImageVote(String _id, String vote, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/vote/" + ((HttpUtils.encodeUrl(vote) == null) ? "" : HttpUtils.encodeUrl(vote)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> galleryImageVotes(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/votes",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread galleryImageVotes(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/votes",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> gallerySearch(String q) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (q != null && !q.equals("")) {
	parameters.put("q", q);
        }
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/gallery/search",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread gallerySearch(String q, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (q != null && !q.equals("")) {
        
            parameters.put("q", q);
        }
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/gallery/search",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> image(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread image(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<String> imageDelete(String deletehash) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<String>) HttpClient.doRequest(String.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/3/image/" + ((HttpUtils.encodeUrl(deletehash) == null) ? "" : HttpUtils.encodeUrl(deletehash)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread imageDelete(String deletehash, MashapeCallback<String> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(String.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/3/image/" + ((HttpUtils.encodeUrl(deletehash) == null) ? "" : HttpUtils.encodeUrl(deletehash)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> imageUpload(String image, String albumid, String type, String title, String description) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (image != null && !image.equals("")) {
	parameters.put("image", image);
        }
        
        
        
        if (albumid != null && !albumid.equals("")) {
	parameters.put("album_id", albumid);
        }
        
        
        
        if (type != null && !type.equals("")) {
	parameters.put("type", type);
        }
        
        
        
        if (title != null && !title.equals("")) {
	parameters.put("title", title);
        }
        
        
        
        if (description != null && !description.equals("")) {
	parameters.put("description", description);
        }
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/image",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Synchronous call without optional parameters.
     */
    public MashapeResponse<JSONObject> imageUpload(String image) {
        return imageUpload(image, "", "", "", "");
    }


    /**
     * Asynchronous call with optional parameters.
     */
    public Thread imageUpload(String image, String albumid, String type, String title, String description, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (image != null && !image.equals("")) {
        
            parameters.put("image", image);
        }
        
        
        
        if (albumid != null && !albumid.equals("")) {
        
            parameters.put("album_id", albumid);
        }
        
        
        
        if (type != null && !type.equals("")) {
        
            parameters.put("type", type);
        }
        
        
        
        if (title != null && !title.equals("")) {
        
            parameters.put("title", title);
        }
        
        
        
        if (description != null && !description.equals("")) {
        
            parameters.put("description", description);
        }
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/image",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Asynchronous call without optional parameters.
     */
    public Thread imageUpload(String image, MashapeCallback<JSONObject> callback) {
        return imageUpload(image, "", "", "", "", callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> imageUploadAlternative(String image, String type, String name, String title, String description, String albumid) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (image != null && !image.equals("")) {
	parameters.put("image", image);
        }
        
        
        
        if (albumid != null && !albumid.equals("")) {
	parameters.put("album_id", albumid);
        }
        
        
        
        if (type != null && !type.equals("")) {
	parameters.put("type", type);
        }
        
        
        
        if (name != null && !name.equals("")) {
	parameters.put("name", name);
        }
        
        
        
        if (title != null && !title.equals("")) {
	parameters.put("title", title);
        }
        
        
        
        if (description != null && !description.equals("")) {
	parameters.put("description", description);
        }
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/upload",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Synchronous call without optional parameters.
     */
    public MashapeResponse<JSONObject> imageUploadAlternative(String image, String type, String name, String title, String description) {
        return imageUploadAlternative(image, type, name, title, description, "");
    }


    /**
     * Asynchronous call with optional parameters.
     */
    public Thread imageUploadAlternative(String image, String type, String name, String title, String description, String albumid, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (image != null && !image.equals("")) {
        
            parameters.put("image", image);
        }
        
        
        
        if (albumid != null && !albumid.equals("")) {
        
            parameters.put("album_id", albumid);
        }
        
        
        
        if (type != null && !type.equals("")) {
        
            parameters.put("type", type);
        }
        
        
        
        if (name != null && !name.equals("")) {
        
            parameters.put("name", name);
        }
        
        
        
        if (title != null && !title.equals("")) {
        
            parameters.put("title", title);
        }
        
        
        
        if (description != null && !description.equals("")) {
        
            parameters.put("description", description);
        }
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/upload",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Asynchronous call without optional parameters.
     */
    public Thread imageUploadAlternative(String image, String type, String name, String title, String description, MashapeCallback<JSONObject> callback) {
        return imageUploadAlternative(image, type, name, title, description, "", callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> notification(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/notification/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread notification(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/notification/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> notificationViewed(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/notification/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread notificationViewed(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.DELETE,
                    "https://" + PUBLIC_DNS + "/notification/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> notifications() {
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/notification",
                    null,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread notifications(MashapeCallback<JSONObject> callback) {
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/notification",
                    null,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> reportComment(String _id) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/3/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/report",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread reportComment(String _id, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/3/comment/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "/report",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> subredditGalleries(String subreddit, String sort, String page) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/gallery/r/" + ((HttpUtils.encodeUrl(subreddit) == null) ? "" : HttpUtils.encodeUrl(subreddit)) + "/" + ((HttpUtils.encodeUrl(sort) == null) ? "" : HttpUtils.encodeUrl(sort)) + "/" + ((HttpUtils.encodeUrl(page) == null) ? "" : HttpUtils.encodeUrl(page)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread subredditGalleries(String subreddit, String sort, String page, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/3/gallery/r/" + ((HttpUtils.encodeUrl(subreddit) == null) ? "" : HttpUtils.encodeUrl(subreddit)) + "/" + ((HttpUtils.encodeUrl(sort) == null) ? "" : HttpUtils.encodeUrl(sort)) + "/" + ((HttpUtils.encodeUrl(page) == null) ? "" : HttpUtils.encodeUrl(page)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> updateAccountSettings(String username, String email, String publicimages, String albumprivacy, String messagingenabled) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (email != null && !email.equals("")) {
	parameters.put("email", email);
        }
        
        
        
        if (publicimages != null && !publicimages.equals("")) {
	parameters.put("public_images", publicimages);
        }
        
        
        
        if (albumprivacy != null && !albumprivacy.equals("")) {
	parameters.put("album_privacy", albumprivacy);
        }
        
        
        
        if (messagingenabled != null && !messagingenabled.equals("")) {
	parameters.put("messaging_enabled", messagingenabled);
        }
        
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/settings",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Synchronous call without optional parameters.
     */
    public MashapeResponse<JSONObject> updateAccountSettings(String username) {
        return updateAccountSettings(username, "", "", "", "");
    }


    /**
     * Asynchronous call with optional parameters.
     */
    public Thread updateAccountSettings(String username, String email, String publicimages, String albumprivacy, String messagingenabled, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (email != null && !email.equals("")) {
        
            parameters.put("email", email);
        }
        
        
        
        if (publicimages != null && !publicimages.equals("")) {
        
            parameters.put("public_images", publicimages);
        }
        
        
        
        if (albumprivacy != null && !albumprivacy.equals("")) {
        
            parameters.put("album_privacy", albumprivacy);
        }
        
        
        
        if (messagingenabled != null && !messagingenabled.equals("")) {
        
            parameters.put("messaging_enabled", messagingenabled);
        }
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/3/account/" + ((HttpUtils.encodeUrl(username) == null) ? "" : HttpUtils.encodeUrl(username)) + "/settings",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Asynchronous call without optional parameters.
     */
    public Thread updateAccountSettings(String username, MashapeCallback<JSONObject> callback) {
        return updateAccountSettings(username, "", "", "", "", callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> uploadImageToGallery(String title, String _id, String terms) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (title != null && !title.equals("")) {
	parameters.put("title", title);
        }
        
        
        
        if (terms != null && !terms.equals("")) {
	parameters.put("terms", terms);
        }
        
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/3/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Synchronous call without optional parameters.
     */
    public MashapeResponse<JSONObject> uploadImageToGallery(String title, String _id) {
        return uploadImageToGallery(title, _id, "");
    }


    /**
     * Asynchronous call with optional parameters.
     */
    public Thread uploadImageToGallery(String title, String _id, String terms, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (title != null && !title.equals("")) {
        
            parameters.put("title", title);
        }
        
        
        
        if (terms != null && !terms.equals("")) {
        
            parameters.put("terms", terms);
        }
        
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/3/gallery/image/" + ((HttpUtils.encodeUrl(_id) == null) ? "" : HttpUtils.encodeUrl(_id)) + "",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Asynchronous call without optional parameters.
     */
    public Thread uploadImageToGallery(String title, String _id, MashapeCallback<JSONObject> callback) {
        return uploadImageToGallery(title, _id, "", callback);
    }

}