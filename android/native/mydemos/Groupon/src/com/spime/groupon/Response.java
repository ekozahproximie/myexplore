package com.spime.groupon;

public class Response {

	public String stMessage=null;
	public String stHttpCode=null; 
	public String stRateLimit=null;
	public String stRateRemaining=null;
	public String stClientRateRemaining=null;
	public String stIPRateResetsAt=null;
	/**
	 * X-RateLimit-IP-RateResetsAt: 2011-04-21T23:28:22Z
X-RateLimit-Client-RateRemaining: 2499 
X-RateLimit-IP-RateRemaining: 599
X-RateLimit-IP-RateLimit: 600
200 OK: Success!
400 Bad Request: The request was invalid for reasons other than authentication.
401 Unauthorized: Authentication credentials were missing or incorrect.
    Returned from POST /oauth/access_token if client_id, username, password are missing or incorrect
    Returned from any authenticated request that is missing the access_token
403 Forbidden: The request is understood, but was refused because the requestor does not have the correct rights to perform the action requested.
404 Not Found: A specified instance of a resource that does not exist has been requested. Note: incorrect parameters for a request will result in a 400 response.
500 Internal Server Error: Something unexpected happened on the server. Contact support.

503 Service Unavailable: Groupon is currently unavailable to fulfill your request.


	 */
}
