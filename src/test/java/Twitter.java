
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.client.authentication.Authentication;
import com.mashape.client.authentication.MashapeAuthentication;
import com.mashape.client.authentication.OAuth10aAuthentication;
import com.mashape.client.authentication.OAuthAuthentication;
import com.mashape.client.http.ContentType;
import com.mashape.client.http.HttpClient;
import com.mashape.client.http.HttpMethod;
import com.mashape.client.http.MashapeCallback;
import com.mashape.client.http.MashapeResponse;
import com.mashape.client.http.ResponseType;

public class Twitter {

	private final static String PUBLIC_DNS = "mashaper-twitter.p.mashape.com";
    private List<Authentication> authenticationHandlers;

    public Twitter (String publicKey, String privateKey, String consumerKey, String consumerSecret, String redirectUrl) {
        authenticationHandlers = new LinkedList<Authentication>();
        authenticationHandlers.add(new MashapeAuthentication(publicKey, privateKey));
        authenticationHandlers.add(new OAuth10aAuthentication(consumerKey, consumerSecret, redirectUrl));
        
    }
    
    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> mentions() {
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/statuses/mentions_timeline.json",
                    null,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread mentions(MashapeCallback<JSONObject> callback) {
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/statuses/mentions_timeline.json",
                    null,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> updateStatus(String status) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (status != null && !status.equals("")) {
	parameters.put("status", status);
        }
        
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/statuses/update.json",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers);
    }

    /**
     * Asynchronous call with optional parameters.
     */
    public Thread updateStatus(String status, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        if (status != null && !status.equals("")) {
        
            parameters.put("status", status);
        }
        
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/statuses/update.json",
                    parameters,
                    ContentType.FORM,
                    ResponseType.STRING,
                    authenticationHandlers,
                    callback);
    }


	public MashapeResponse<JSONObject> getOAuthUrl() {
		return getOAuthUrl(null);
	}
	
	public MashapeResponse<JSONObject> getOAuthUrl(String scope) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (authenticationHandlers != null) {
			for (Authentication authentication : authenticationHandlers) {
				if (authentication instanceof OAuthAuthentication) {
					Map<String, String> queryParameters = authentication.getQueryParameters();
					parameters.put(OAuthAuthentication.CONSUMER_KEY, queryParameters.get(OAuthAuthentication.CONSUMER_KEY));
					parameters.put(OAuthAuthentication.CONSUMER_SECRET, queryParameters.get(OAuthAuthentication.CONSUMER_SECRET));
					parameters.put(OAuthAuthentication.REDIRECT_URL, queryParameters.get(OAuthAuthentication.REDIRECT_URL));
				}
			}
		}
		parameters.put("scope", scope);
		return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.GET,
                    "https://" + PUBLIC_DNS + "/oauth_url",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
	}
	
	public Twitter authorize(String accessToken, String accessSecret) {
		if (authenticationHandlers != null) {
			for (Authentication authentication : authenticationHandlers) {
				if (authentication instanceof OAuthAuthentication) {
					((OAuthAuthentication) authentication).setAccessToken(accessToken);
					((OAuth10aAuthentication) authentication).setAccessSecret(accessSecret);
				}
			}
		}
		return this;
	}
}