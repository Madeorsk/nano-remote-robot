import {__NRR__CONFIG} from "./config"

document.addEventListener("DOMContentLoaded", () => {
	// Initializing pressable buttons.
	let forward_button = new PressableButton(document.getElementById("forward"));
	let backward_button = new PressableButton(document.getElementById("backward"));
	let left_button = new PressableButton(document.getElementById("left"));
	let right_button = new PressableButton(document.getElementById("right"));

	// Authentication to get the authorization token.
	fetch(`http${__NRR__CONFIG.server.secure ? "s" : ""}://${__NRR__CONFIG.server.host}:${__NRR__CONFIG.server.port}/auth/login`, {
		method: "post",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			username: __NRR__CONFIG.username,
			password: __NRR__CONFIG.password,
		}),
	}).then((res) => { // When response is received.
		if (res.ok)
			return res.json();
		else
			return null;
	}).then((json) => { // When JSON is decoded.
		if (typeof json.auth === "string")
		{
			// Obtaining token.
			let token = json.auth;

			// Connection to the websocket of the nRR server.
			// Build WebSocket URL as defined in config.js.
			let ws = new WebSocket(`ws${__NRR__CONFIG.server.secure ? "s" : ""}://${__NRR__CONFIG.server.host}:${__NRR__CONFIG.server.port}`, token);

			// Regularly, send a message about which movement to make.
			window.setInterval(() => {
				// Sending +1 for moving forward, -1 for backward and 0 for no movement.
				if (forward_button.isPressed())
					ws.send("translation:" + JSON.stringify({ "orientation": +1 }));
				else if (backward_button.isPressed())
					ws.send("translation:" + JSON.stringify({ "orientation": -1 }));
				else
					ws.send("translation:" + JSON.stringify({ "orientation": 0 }));

				// Sending +1 for moving left, -1 for right and 0 for no movement.
				if (left_button.isPressed())
					ws.send("rotation:" + JSON.stringify({ "orientation": +1 }));
				else if (right_button.isPressed())
					ws.send("rotation:" + JSON.stringify({ "orientation": -1 }));
				else
					ws.send("rotation:" + JSON.stringify({ "orientation": 0 }));
			}, __NRR__CONFIG.interval); // Interval defined in config.js.
		}
		else
			return null;
	});
});

/**
 * PressableButton class.
 * Takes a button and handle events to determine at any time if a button is pressed or not.
 */
class PressableButton
{
	/**
	 * Button HTML element.
	 */
	private button: HTMLElement;
	/**
	 * `true` if the button is pressed, `false` otherwise.
	 */
	private pressed: boolean;

	/**
	 * Construct a PressableButton object from the HTML element of a button.
	 * @param btn - HTML element of the button.
	 */
	public constructor(btn: HTMLElement)
	{
		// Assigning fields.
		this.button = btn;
		this.pressed = false;

		this.init(); // Initialize events.
	}

	/**
	 * Initialize events to update `pressed` field.
	 */
	private init()
	{
		// Mobile devices.
		this.button.addEventListener("touchstart", () => { this.pressed = true; });
		this.button.addEventListener("touchend", () => { this.pressed = false; });
		// Computer devices.
		this.button.addEventListener("mousedown", () => { this.pressed = true; });
		this.button.addEventListener("mouseup", () => { this.pressed = false; });
	}

	/**
	 * Return `true` if the button is pressed, `false` otherwise.
	 */
	public isPressed(): boolean
	{
		return this.pressed;
	}
}