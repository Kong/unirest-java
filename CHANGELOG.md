# 2.1.00
* JSON parsing errors no longer just throw out of ```asJson``` but are captured and can be inspected via ```HttpResponse::getParsingError```. This resolves the fact that most API's do not return valid JSON for non-200 status codes.
* Return CompletableFuture rather that boring old Futures for all async methods.


# 2.0.02
* UnirestException is no longer checked
* Namespaces have been migrated to new **io.gitgub.openunirest** namespace
* Thread leak in Options fixed
