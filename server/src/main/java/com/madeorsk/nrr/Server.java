package com.madeorsk.nrr;

import com.madeorsk.nrr.ws.WSClient;
import com.madeorsk.nrr.ws.WSRouter;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.websocket;

/**
 * Main server class.
 */
public class Server
{
	/**
	 * Main function.
	 * @param args - Program arguments.
	 */
	public static void main(String[] args)
	{
		// WebSocket router initialization (command handling).
		WSRouter router = new WSRouter();

		// Undertow web server setup.
		Undertow server = Undertow.builder()
				// Open HTTP listener with the port of the properties and the host of the properties.
				.addHttpListener(Integer.parseInt(ApplicationProperties.get().getProperty("port")), ApplicationProperties.get().getProperty("host"))
				.setHandler(
						path()
								// Websocket endpoint.
								.addExactPath("/", websocket(
										(webSocketHttpExchange, webSocketChannel) -> {
											webSocketChannel.getReceiveSetter().set(new WSClient(router, webSocketHttpExchange, webSocketChannel));
										})
								)

								// Authentication endpoint.
								.addExactPath("/auth/login", httpServerExchange -> {
									httpServerExchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");

									if ("application/json".equals(httpServerExchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE)))
									{ // Request is properly formed.
										// Receiving string body...
										httpServerExchange.getRequestReceiver().receiveFullString((HttpServerExchange httpSE, String body) -> {
											// String body is received, trying to parse it as a JSON object.
											try
											{
												// Get JSON object from request body.
												JSONObject bodyJson = (JSONObject) (new JSONParser()).parse(body);

												// Get sent username and password from request body.
												String username = (String) bodyJson.get("username");
												String password = (String) bodyJson.get("password");

												if (ApplicationProperties.get().getProperty("username").equals(username)
														&& ApplicationProperties.get().getProperty("password").equals(password))
												{ // Send success response with authentication token.
													JSONObject json = new JSONObject();
													json.put("auth", Utils.makeAuthenticationToken()); // Generate authentication token and put in in response JSON.
													httpServerExchange.setStatusCode(StatusCodes.OK); // Success code.
													httpServerExchange.getResponseSender().send(json.toJSONString());
												}
												else
												{ // Send error response.
													JSONObject json = new JSONObject();
													json.put("message", "Invalid credentials."); // Error message.
													httpServerExchange.setStatusCode(StatusCodes.UNAUTHORIZED); // Error code.
													httpServerExchange.getResponseSender().send(json.toJSONString());
												}
											} catch (ParseException e)
											{
												e.printStackTrace();
											}
										});
									}
									else
									{ // Send error response.
										JSONObject json = new JSONObject();
										json.put("message", "Bad request."); // Error message.
										httpServerExchange.setStatusCode(StatusCodes.BAD_REQUEST); // Error code.
										httpServerExchange.getResponseSender().send(json.toJSONString());
									}
								})
				)
				.build();

		server.start();
	}
}
