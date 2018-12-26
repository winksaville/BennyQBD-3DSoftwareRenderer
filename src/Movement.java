import java.awt.event.KeyEvent;

class Movement {

	final boolean DBG = false;
	final boolean DBG1 = false;

	private class KeyInfo {
		long timeInNsBothPressed;
		long repeatDelay;
		long repeatSpeed;
		long repeatOffset;
		int i;
		boolean key1Pressed;
		int key1;
		boolean key2Pressed;
		int key2;

		KeyInfo(int i, int key1, int key2, long repeatDelay, long repeatSpeed) {
			this.timeInNsBothPressed = 0;
			this.repeatDelay = repeatDelay;
			this.repeatSpeed = repeatSpeed;
			this.repeatOffset = 0;
			this.i = i;
			this.key1Pressed = false;
			this.key1 = key1;
			this.key2Pressed = false;
			this.key2 = key2;
		}
	};

	private KeyInfo m_keys[] = new KeyInfo[12];
	private boolean m_changed;
	private Transform m_transform;
	private float m_h;

	private final int X_TRANS_PLUS = 0;
	private final int Y_TRANS_PLUS = 1;
	private final int Z_TRANS_PLUS = 2;
	private final int X_TRANS_NEG  = 3;
	private final int Y_TRANS_NEG  = 4;
	private final int Z_TRANS_NEG  = 5;

	private final int X_ROTATE_PLUS = 6;
	private final int Y_ROTATE_PLUS = 7;
	private final int Z_ROTATE_PLUS = 8;
	private final int X_ROTATE_NEG  = 9;
	private final int Y_ROTATE_NEG  = 10;
	private final int Z_ROTATE_NEG  = 11;

	// Rotation and Translation Input
	//
	// rotationKey
	// translationKey
	//
	//                 (yPlusKey)
	//                 +y     (zNegKey)
	//                  |    -z
	//                  |    /
	//                  |   /
	//                  |  /
	//                  | /
	// (xNegKey)        |/
	//     -x ----------o---------- +x (yPlusKey)
	//                 /|
	//                / |
	//               /  |
	//              /   |
	//             /    |
	//           +z     |
	//    (zPlusKey)   -y
	//                 (yNegKey)
	//
	public Movement(Vector4f position, Vector4f lookAtPoint, Vector4f up,
			int xPlusKey, int yPlusKey, int zPlusKey,
			int xNegKey,  int yNegKey,  int zNegKey,
			int translateKey, int rotationKey)
	{
		Transform transform = new Transform(position);
		Quaternion quarternion = transform.GetLookAtRotation(lookAtPoint, up);
		this.m_transform = transform.Rotate(quarternion);
		long repeatDelay = 150 * 1000000;
		long repeatSpeed = 50 * 1000000;
		m_keys[X_TRANS_PLUS] = new KeyInfo(X_TRANS_PLUS, xPlusKey, translateKey, repeatDelay, repeatSpeed);
		m_keys[Y_TRANS_PLUS] = new KeyInfo(Y_TRANS_PLUS, yPlusKey, translateKey, repeatDelay, repeatSpeed);
		m_keys[Z_TRANS_PLUS] = new KeyInfo(Z_TRANS_PLUS, zPlusKey, translateKey, repeatDelay, repeatSpeed);
		m_keys[X_TRANS_NEG ] = new KeyInfo(X_TRANS_NEG, xNegKey,  translateKey, repeatDelay, repeatSpeed);
		m_keys[Y_TRANS_NEG ] = new KeyInfo(Y_TRANS_NEG, yNegKey,  translateKey, repeatDelay, repeatSpeed);
		m_keys[Z_TRANS_NEG ] = new KeyInfo(Z_TRANS_NEG, zNegKey,  translateKey, repeatDelay, repeatSpeed);

		m_keys[X_ROTATE_PLUS] = new KeyInfo(X_ROTATE_PLUS, xPlusKey, rotationKey, repeatDelay, repeatSpeed);
		m_keys[Y_ROTATE_PLUS] = new KeyInfo(Y_ROTATE_PLUS, yPlusKey, rotationKey, repeatDelay, repeatSpeed);
		m_keys[Z_ROTATE_PLUS] = new KeyInfo(Z_ROTATE_PLUS, zPlusKey, rotationKey, repeatDelay, repeatSpeed);
		m_keys[X_ROTATE_NEG ] = new KeyInfo(X_ROTATE_NEG, xNegKey,  rotationKey, repeatDelay, repeatSpeed);
		m_keys[Y_ROTATE_NEG ] = new KeyInfo(Y_ROTATE_NEG, yNegKey,  rotationKey, repeatDelay, repeatSpeed);
		m_keys[Z_ROTATE_NEG ] = new KeyInfo(Z_ROTATE_NEG, zNegKey,  rotationKey, repeatDelay, repeatSpeed);

		// Change m_h to -1 to get counter-clockwise rotation, i.e. "left-handed" rotation.
		m_h = 1;
	}

	private boolean KeysReleased(Input input, KeyInfo ki) {
		boolean released = ki.key1Pressed && ki.key2Pressed && (!input.GetKey(ki.key1) || !input.GetKey(ki.key2));
		if(DBG1 && released) Dbg.p(String.format("i=%d key1=%d key2=%d KeysReleased\n", ki.i, ki.key1, ki.key2));
		return released;
	}

	private boolean KeysRepeat(long timeInNs, KeyInfo ki) {
		// Mark time when both are first pressed.
		boolean repeat = false;
		if(ki.timeInNsBothPressed > 0) {
			repeat = (timeInNs >= (ki.timeInNsBothPressed + ki.repeatDelay + ki.repeatOffset));
			if(DBG1 && repeat) Dbg.p(String.format("i=%d key1=%d key2=%d t=%d ki.t=%d, ki.rd=%d ki.ro=%d KeysRepeat RRR\n", ki.i, ki.key1, ki.key2, timeInNs, ki.timeInNsBothPressed, ki.repeatDelay, ki.repeatOffset));
		}
		return repeat;
	}

	public void Update(Input input, long timeInNs, float translationDelta, float rotationDelta)
	{
		for (int i = 0; i < m_keys.length; i++) {
			KeyInfo ki = m_keys[i];

			// Mark time when both are first pressed.
			if(ki.key1Pressed && ki.key2Pressed && (ki.timeInNsBothPressed == 0)) {
				if(DBG) Dbg.p(String.format("i=%d key1=%d key2=%d, t=%d BOTH PRESSED\n", ki.i, ki.key1, ki.key2, timeInNs));
				ki.timeInNsBothPressed = timeInNs;
				ki.repeatOffset = 0;
			}

			if(this.KeysReleased(input, ki) || this.KeysRepeat(timeInNs, ki)) {
				ki.repeatOffset = ki.repeatOffset + ki.repeatSpeed;
				this.m_changed = true;
				translationDelta = translationDelta;
				rotationDelta = rotationDelta;
				if(DBG1) Dbg.p(String.format("i=%d key1=%d key2=%d release|repeating\n", i, ki.key1, ki.key2));
				switch (i) {
					case X_TRANS_PLUS: {
						if(DBG) Dbg.p(String.format("i=%d trans x+\n", i));
						Translate(m_transform.GetRot().GetXaxis(), translationDelta);
						break;
					}
					case Y_TRANS_PLUS: {
						if(DBG) Dbg.p(String.format("i=%d trans y+\n", i));
						Translate(m_transform.GetRot().GetYaxis(), translationDelta);
						break;
					}
					case Z_TRANS_PLUS: {
						if(DBG) Dbg.p(String.format("i=%d trans z+\n", i));
						Translate(m_transform.GetRot().GetZaxis(), translationDelta);
						break;
					}
					case X_TRANS_NEG: {
						if(DBG) Dbg.p(String.format("i=%d trans x-\n", i));
						Translate(m_transform.GetRot().GetXaxis(), -translationDelta);
						break;
					}
					case Y_TRANS_NEG: {
						if(DBG) Dbg.p(String.format("i=%d trans y-\n", i));
						Translate(m_transform.GetRot().GetYaxis(), -translationDelta);
						break;
					}
					case Z_TRANS_NEG: {
						if(DBG) Dbg.p(String.format("i=%d trans z-\n", i));
						Translate(m_transform.GetRot().GetZaxis(), -translationDelta);
						break;
					}
					case X_ROTATE_PLUS: {
						if(DBG) Dbg.p(String.format("i=%d rotate x+\n", i));
						Rotate(m_transform.GetRot().GetXaxis(), m_h * rotationDelta);
						break;
					}
					case Y_ROTATE_PLUS: {
						if(DBG) Dbg.p(String.format("i=%d rotate y+\n", i));
						Rotate(m_transform.GetRot().GetYaxis(), m_h * rotationDelta);
						break;
					}
					case Z_ROTATE_PLUS: {
						if(DBG) Dbg.p(String.format("i=%d rotate z+\n", i));
						Rotate(m_transform.GetRot().GetZaxis(), m_h * rotationDelta);
						break;
					}
					case X_ROTATE_NEG: {
						if(DBG) Dbg.p(String.format("i=%d rotate x-\n", i));
						Rotate(m_transform.GetRot().GetXaxis(), m_h * -rotationDelta);
						break;
					}
					case Y_ROTATE_NEG: {
						if(DBG) Dbg.p(String.format("i=%d rotate y-\n", i));
						Rotate(m_transform.GetRot().GetYaxis(), m_h * -rotationDelta);
						break;
					}
					case Z_ROTATE_NEG: {
						if(DBG) Dbg.p(String.format("i=%d rotate z-\n", i));
						Rotate(m_transform.GetRot().GetZaxis(), m_h * -rotationDelta);
						break;
					}
				}
			}

			if(!ki.key1Pressed && input.GetKey(ki.key1)) {
				ki.key1Pressed = true;
			}

			if(!ki.key2Pressed && input.GetKey(ki.key2)) {
				ki.key2Pressed = true;
			}

			if(ki.key1Pressed && !input.GetKey(ki.key1)) {
				if(DBG) Dbg.p(String.format("i=%d: key1=%d released\n", i, ki.key1));
				ki.key1Pressed = false;
				ki.timeInNsBothPressed = 0;
				ki.repeatOffset = 0;
			}
			if(ki.key2Pressed && !input.GetKey(ki.key2)) {
				if(DBG) Dbg.p(String.format("i=%d: key2=%d released\n", i, ki.key2));
				ki.key2Pressed = false;
				ki.timeInNsBothPressed = 0;
				ki.repeatOffset = 0;
			}
		}
	}

	public void Translate(Vector4f dir, float amt)
	{
		m_transform = m_transform.SetPos(m_transform.GetPos().Add(dir.Mul(amt)));
	}

	public void Rotate(Vector4f axis, float angle)
	{
		m_transform = m_transform.Rotate(new Quaternion(axis, angle));
	}

	boolean m_changed()
	{
		return this.m_changed;
	}

	Transform GetTransform()
	{
		this.m_changed = false;
		return m_transform;
	}
}
