from nrr import NRR


def on_translation(orientation):
	"""
	Translation callback.
	:param orientation: Orientation of the translation (forward / backward)
	:type orientation: int
	"""
	if orientation == 1:
		print("Moving forward.")
	elif orientation == -1:
		print("Moving backward.")


def on_rotation(orientation):
	"""
	Rotation callback.
	:param orientation: Orientation of the rotation (left / right)
	:type orientation: int
	"""
	if orientation == 1:
		print("Left rotation.")
	elif orientation == -1:
		print("Right rotation.")


if __name__ == "__main__":
	# Initializing connection to NRR server.
	nrr = NRR("root", "toor580", "localhost", 8056)
	# Setting test callbacks.
	nrr.on_translation = on_translation
	nrr.on_rotation = on_rotation
	nrr.connect()  # Connection to the server.
