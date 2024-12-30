# Unirest-Java Documentation

Welcome to Unirest-Java!

## About
Unirest is intended to be a simple and obvious library for HTTP requests. It provides a fluent interface that makes discovery of commands easy in a modern IDE. Some of the features it supports are:


* HTTP 1 and 2
* WebSockets
* JSON Patch (RFC-6902)
* Default Object mappers for both Jackson and GSON
* Easy request building including
    * Path parameters
    * Query param building
    * Headers including full suport for manipulating cookies
    * Multipart requests
    * Turning POJOs into string or binary bodies
    * Global request interceptors
* Easy response handling including
    * Global response interceptors
    * Turning bodies into POJOs 
    * Error handling
* Mocking library for testing
    

## History

Unirest-Java started off as just one of series of HTTP clients written in different languages which all conform to the same pattern. 

From the original Unirest-Java though Unirest-Java 3 the library was essentially a wrapper around the excellent Apache HTTP 
