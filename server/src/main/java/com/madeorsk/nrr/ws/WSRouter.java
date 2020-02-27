package com.madeorsk.nrr.ws;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket router class.
 */
public class WSRouter
{
	/**
	 * Map of registered commands.
	 */
	private Map<String, Command> commands;

	public WSRouter()
	{
		this.commands = new HashMap<>();
	}

	/**
	 * Add a command to the router.
	 * @param c - The command to add.
	 */
	public void addCommand(Command c)
	{
		this.commands.put(c.getName(), c);
	}
	/**
	 * Get existence of the command by its name.
	 * @param name - Name of the command to check.
	 * @return true if command exists, false otherwise.
	 */
	public boolean hasCommand(String name)
	{
		return this.commands.containsKey(name);
	}
	/**
	 * Get command with the given name.
	 * @param name - Name of the command to get.
	 * @return The command with the given name.
	 */
	public Command getCommand(String name)
	{
		return this.commands.get(name);
	}

	/**
	 * Class of the command.
	 */
	public abstract static class Command
	{
		/**
		 * Command name.
		 */
		private String name;

		/**
		 * Create a command for a given name.
		 * @param name - Name of the command.
		 */
		public Command(String name)
		{
			this.name = name;
		}

		/**
		 * Get the name of the command.
		 * @return The name of the command.
		 */
		public String getName()
		{ return this.name; }

		/**
		 * Called when the command is executed.
		 * @param client - Client who sent the command.
		 * @param args - Arguments of the command (a JSON object).
		 * @return The WSData of the response to send.
		 */
		public abstract WSData on(WSClient client, JSONObject args);
	}
}
