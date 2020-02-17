package com.madeorsk.nrr.ws;

import org.json.simple.JSONObject;

/**
 * WebSocket Data for errors.
 */
public class WSErrorData extends WSData
{
	/**
	 * Function to create the message object of an error.
	 * @param message - Message of the error.
	 * @return The JSON object containing the given message.
	 */
	public static JSONObject produceMessageObject(String message)
	{
		JSONObject json = new JSONObject();
		json.put("message", message);
		return json;
	}

	/**
	 * Create the WebSocket Data for the given error.
	 * @param errorType - Type of the error.
	 * @param errorMessage - Message of the error.
	 */
	public WSErrorData(String errorType, String errorMessage)
	{
		super("(Error)(" + errorType + ")", WSErrorData.produceMessageObject(errorMessage));
	}
}
