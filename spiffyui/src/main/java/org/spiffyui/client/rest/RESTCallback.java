/*******************************************************************************
 * 
 * Copyright 2011 Spiffy UI Team   
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.spiffyui.client.rest;

import com.google.gwt.json.client.JSONValue;

/**
 * <p>
 *  This interface is used when calling REST APIs in conjunction with RESTility.
 * </p>
 * 
 * <p>
 * RESTCallback is the low level interface for handling JSON and HTTP.  This
 * interface gets access to errors generated by the HTTP call and the raw
 * JSON values the REST call returns.  This interface is typically used
 * in conjunction with a RESTObjectCallBack.  The RESTCallback handles converting
 * the JSON data structure into an object and then returns those data structures
 * to the RESTObjectCallBack.
 * </p>
 * 
 * @see RESTObjectCallBack
 * @see RESTility
 */
public interface RESTCallback
{
    
    /**
     * <p>
     * Called when the REST call completes successfully.
     * </p>
     * 
     * <p>
     * A successful REST request is one that returned a value from the server
     * containing well formed JSON which did not contain an NCAC error message
     * or an authentication request.  Successful REST requests may return any
     * HTTP response code and are not limited to 200.
     * </p>
     * 
     * @param val    The JSON value from the REST call
     */
    void onSuccess(JSONValue val);
    
    /**
     * <p>
     * Called if there is an unexpected error calling the REST API.
     * </p>
     * 
     * <p>
     * This method is called for unexpected errors including network 
     * failures, data loss, and server responses which aren't well
     * formed JSON.  
     * </p>
     * 
     * @param statusCode the HTTP status code with the error
     * @param errorResponse
     *                   the error message response from the REST endpoint
     */
    void onError(int statusCode, String errorResponse);
    
    /**
     * <p>
     * Called if the REST endpoint returns a valid response with an 
     * error message following in the 
     * <a href="http://www.w3.org/TR/soap12-part1/#soapfault">SOAP error 
     * format</a> encoded in JSON.
     * </p>
     * 
     * <p>
     * REST calls in this framework have built-in error handling to parse
     * a strict format of error message in JSON and represent it as a Java
     * object.  These message represente well know errors and are often
     * part of the public API for a REST interface.
     * </p>
     * 
     * <p>
     * These error messages follow a JSON format of the standard SOAP error
     * message.  The basic structure looks like this:
     * </p>
     * 
     * <pre>
     * {
     *     "Fault": {
     *         "Code": {
     *             "Value": "Sender",
     *             "Subcode": {
     *                 "Value": "MessageTimeout" 
     *             } 
     *         },
     *         "Reason": {
     *             "Text": "Sender Timeout" 
     *         },
     *         "Detail": {
     *             "MaxTime": "P5M" 
     *         } 
     *     }
     * }
     * </pre>
     * 
     * <p>
     * The detail section is reserved for additional properties of 
     * the specific error message.
     * </p>
     * 
     * @param e      the RESTException
     */
    void onError(RESTException e);
    
}
