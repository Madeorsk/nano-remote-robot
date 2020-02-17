package com.madeorsk.nrr.ws;

import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Class which handles a client of the WebSocket
 */
public class WSClient extends AbstractReceiveListener
{
	/**
	 * WebSocket router, containing the commands.
	 */
	private WSRouter router;
	/**
	 * WebSocket HTTP Exchange, to verify and identify the request.
	 */
	private WebSocketHttpExchange httpExchange;
	/**
	 * WebSocket channel.
	 */
	private WebSocketChannel channel;

	/**
	 * JSON parser.
	 */
	private JSONParser parser;

	/**
	 * Create a WebSocket client with a router, for the given HTTP exchange and channel.
	 * @param router - Router of the client.
	 * @param httpExchange - HTTP exchange data.
	 * @param channel - Channel of the WebSocket.
	 */
	public WSClient(WSRouter router, WebSocketHttpExchange httpExchange, WebSocketChannel channel)
	{
		this.router = router;
		this.httpExchange = httpExchange;
		this.channel = channel;

		this.parser = new JSONParser();
	}

	@Override
	protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException
	{
		try
		{
			String fullCommand = message.getData();
			int colonPos = fullCommand.indexOf(':');
			String commandName = fullCommand.substring(0, colonPos);
			String rawArgs = fullCommand.substring(colonPos + 1);
			JSONObject args = (JSONObject) this.parser.parse(rawArgs);

			if (this.router.hasCommand(commandName))
				this.send(this.router.getCommand(commandName).on(args));
			else
			{ // Error: unknown command.
				this.sendError("Unknown command.", "UnknownCommand");
			}
		}
		catch (ParseException e)
		{ // Error: invalid command.
			this.sendError("Invalid command.", "Protocol");
		}
	}

	/**
	 * Send raw string through the WebSocket channel.
	 * @param raw - Raw string to send.
	 */
	protected void send(String raw)
	{
		WebSockets.sendText(raw, this.channel, null);
	}
	/**
	 * Send a WebSocket Data through the WebSocket.
	 * @param data - Data to send.
	 */
	public void send(WSData data)
	{
		if (data != null)
			this.send(data.toString());
	}

	/**
	 * Send a WebSocket Error through the WebSocket.
	 * @param errorMessage - Error message (optional).
	 * @param errorType - Error type (optional).
	 */
	public void sendError(String errorMessage, String errorType)
	{
		if (errorType == null)
			errorType = "SYS";
		if (errorMessage == null || errorMessage.isEmpty())
			errorMessage = "Internal error.";

		this.send((new WSErrorData(errorType, errorMessage)).toString());
	}
	/**
	 * Send a WebSocket Error through the WebSocket.
	 * @param errorMessage - Error message (optional).
	 */
	public void sendError(String errorMessage)
	{
		this.sendError(errorMessage, null);
	}
	/**
	 * Send a WebSocket Error through the WebSocket.
	 */
	public void sendError()
	{
		this.sendError(null);
	}
}
