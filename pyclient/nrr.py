import websocket
import json
import requests


def do_nothing(orientation):
	"""
	Default translation / rotation callback.
	:param orientation: Orientation of the translation / rotation ((forward / backward) / (left / right)).
	:type orientation: int
	"""
	pass


class NRR:
	"""
	nRemoteRobots client class for Python 2.7.
	"""

	def __init__(self, username, password, host, port=8056, secure=False):
		"""
		Constructor of the object.
		:param username: Username of nRR server.
		:type username: string

		:param password: Password of nRR server.
		:type password: string

		:param host: Host of nRR server.
		:type host: string

		:param port: Port of nRR server. Default: 8056.
		:type port: int

		:param secure: Specify if the connection is secured or not (wss if True, ws otherwise).
		:type secure: bool
		"""
		# Assigning basic fields.
		self.username = username
		self.password = password
		self.url = ("s" if secure else "") + "://" + host + ":" + str(port)

		# Initializing callbacks.
		self.on_translation = do_nothing
		self.on_rotation = do_nothing

		# Currently no WebSocket. Created in `connect` method.
		self.ws = None

	def connect(self):
		"""
		Open connection to nRR server.
		"""
		# Authenticate to get the authorization token.
		response = requests.post("http" + self.url + "/auth/login",
		                         json={"username": self.username, "password": self.password},
		                         headers={"Content-Type": "application/json"}).json()

		if "auth" in response.keys():  # If authentication is successful.
			websocket.enableTrace(False)  # No logs in console.
			# Initialize WebSocket connection.
			self.ws = websocket.WebSocketApp("ws" + self.url, header=["Sec-WebSocket-Protocol: " + response["auth"]],
			                                 on_open=self._on_connect, on_message=self._on_message, on_error=self._on_error)
			self.ws.run_forever()  # Run WebSocket.
		else:  # Authentication is not successful.
			raise ValueError("Invalid credentials")

	def _on_connect(self):
		"""
		WebSocket event: when connection is opened.
		"""
		self.ws.send("i am a robot:{}")  # Sending that we are a robot.

	def _on_message(self, message):
		"""
		WebSocket event: when a message is received.
		:param message: Sent message.
		:type message: string
		"""
		colon_pos = message.find(':')  # Preparing message / args cutting.
		orientation = json.loads(message[(colon_pos+1):])["orientation"]  # Getting argument for translation / rotation.
		if message[:colon_pos] == "translation":  # If the message is for translation information, ...
			self.on_translation(orientation)  # ... then call the translation callback with the orientation got in argument.
		elif message[:colon_pos] == "rotation":  # If the message is for translation information, ...
			self.on_rotation(orientation)  # ... then call the rotation callback with the orientation got in argument.

	def _on_error(self, error):
		"""
		WebSocket event: when an error happen.
		:param error: Error details.
		:type error: string
		:return:
		"""
		print("WS_ERROR: " + error)  # Print for debugging.
