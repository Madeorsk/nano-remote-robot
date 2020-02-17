package com.madeorsk.nrr.ws;

import org.json.simple.JSONObject;

/**
 * Class of the WebSocket Data, the data to be sent in response of a command.
 */
public class WSData
{
	/**
	 * Object / Command type.
	 */
	private String objectType;
	/**
	 * Object data to be sent.
	 */
	private JSONObject object;

	/**
	 * Create a WebSocket Data for the given object with its type.
	 * @param objectType - String of the object type.
	 * @param object - Object data.
	 */
	public WSData(String objectType, JSONObject object)
	{
		this.objectType = objectType;
		this.object = object;
	}

	@Override
	public String toString()
	{
		return objectType + ": " + this.object.toJSONString();
	}
}
