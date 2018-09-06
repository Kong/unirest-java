package io.github.openunirest.request;

public enum JsonPatchOperation {
    add("value"),
    remove("value"),
    replace("value"),
    test("value"),
    move("from"),
    copy("from");

    private final String operationtype;

    JsonPatchOperation(String operationtype) {

        this.operationtype = operationtype;
    }

    public String getOperationtype() {
        return operationtype;
    }
}
