package kong.unirest.core;

class RequestFactory {
    public static <R extends BaseRequest> R copy(HttpRequest baseRequest) {
        if(baseRequest instanceof HttpRequestNoBody){
            return (R) new HttpRequestNoBody((HttpRequestNoBody)baseRequest);
        }
        if(baseRequest instanceof HttpRequestBody){
            return (R) new HttpRequestBody((HttpRequestBody)baseRequest);
        }
        if(baseRequest instanceof HttpRequestUniBody){
            return (R) new HttpRequestUniBody((HttpRequestUniBody)baseRequest);
        }
        if(baseRequest instanceof HttpRequestMultiPart) {
            return (R) new HttpRequestMultiPart((HttpRequestMultiPart)baseRequest);
        }

        throw new UnirestException("Cannot find matching type: " + baseRequest.getClass());
    }
}
