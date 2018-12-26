import java.awt.event.KeyEvent;

class Movement {

	final boolean DBG = true;

	private class KeyInfo {
		long timeInNsBothPressed;
		long repeatDelay;
		long repeatSpeed;
		int i;
		int key1Pressed;
		int key1;
		int key2Pressed;
		int key2;

		KeyInfo(int i, int key1, int key2, long repeatDelay, long repeatSpeed) {
			this.timeInNsBothPressed = 0;
			this.repeatDelay = repeatDelay;
			this.repeatSpeed = repeatSpeed;
			this.i = i;
			this.key1Pressed = 0;
			this.key1 = key1;
			this.key2Pressed = 0;
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
		long repeatDelay = 350 * 1000000;
		long repeatSpeed = 100 * 1000000;
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
		m_h = -1;
	}

	private boolean KeysReleased(Input input, KeyInfo ki) {
		boolean released = (ki.key1Pressed > 0) && (ki.key2Pressed > 0) && (!input.GetKey(ki.key1) || !input.GetKey(ki.key2));
		if(DBG && released) Dbg.p(String.format("\ni=%d key1=%d KeysReleased ^^^\n", ki.i, ki.key1));
		return released;
	}

	public void Update(Input input, long timeInNs, float translationDelta, float rotationDelta)
	{
		for (int i = 0; i < m_keys.length; i++) {
			KeyInfo ki = m_keys[i];

			int cnt = input.GetKeyCnt(ki.key1);
			if(ki.key1Pressed < cnt) {
				ki.key1Pressed = cnt;
				if(DBG) Dbg.p(String.format("i=%d: key1=%d:%d pressed\n", i, ki.key1, ki.key1Pressed));
			}
			cnt = input.GetKeyCnt(ki.key2);
			if(ki.key2Pressed < cnt) {
				ki.key2Pressed = cnt;
				if(DBG) Dbg.p(String.format("i=%d: key2=%d:%d pressed\n", i, ki.key2, ki.key2Pressed));
			}

			// Mark time when both are first pressed.
			if((ki.key1Pressed > 0) && (ki.key2Pressed >0) && (ki.timeInNsBothPressed == 0)) {
				ki.timeInNsBothPressed = timeInNs;
			}

			if(this.KeysReleased(input, ki) || ((ki.key1Pressed > 1) && (ki.key2Pressed >= 1))) {
				this.m_changed = true;
				cnt = ki.key1Pressed;
				if (cnt > 1) cnt--;
				translationDelta = translationDelta * cnt;
				rotationDelta = rotationDelta * cnt;
				Dbg.p(String.format("i=%d key1=%d:%d key2=%d:%d pressed\n", i, ki.key1, ki.key1Pressed, ki.key2, ki.key2Pressed));
				ki.key1Pressed = input.KeyNormalize(ki.key1);
				ki.key2Pressed = input.KeyNormalize(ki.key2);
				switch (i) {
					case X_TRANS_PLUS: {
						if(DBG) Dbg.p("trans x+\n");
						Translate(m_transform.GetRot().GetXaxis(), translationDelta);
						break;
					}
					case Y_TRANS_PLUS: {
						if(DBG) Dbg.p("trans y+\n");
						Translate(m_transform.GetRot().GetYaxis(), translationDelta);
						break;
					}
					case Z_TRANS_PLUS: {
						if(DBG) Dbg.p("trans z+\n");
						Translate(m_transform.GetRot().GetZaxis(), translationDelta);
						break;
					}
					case X_TRANS_NEG: {
						if(DBG) Dbg.p("trans x-\n");
						Translate(m_transform.GetRot().GetXaxis(), -translationDelta);
						break;
					}
					case Y_TRANS_NEG: {
						if(DBG) Dbg.p("trans y-\n");
						Translate(m_transform.GetRot().GetYaxis(), -translationDelta);
						break;
					}
					case Z_TRANS_NEG: {
						if(DBG) Dbg.p("trans z-\n");
						Translate(m_transform.GetRot().GetZaxis(), -translationDelta);
						break;
					}
					case X_ROTATE_PLUS: {
						if(DBG) Dbg.p("rotate x+\n");
						Rotate(m_transform.GetRot().GetXaxis(), m_h * rotationDelta);
						break;
					}
					case Y_ROTATE_PLUS: {
						if(DBG) Dbg.p("rotate y+\n");
						Rotate(m_transform.GetRot().GetYaxis(), m_h * rotationDelta);
						break;
					}
					case Z_ROTATE_PLUS: {
						if(DBG) Dbg.p("rotate z+\n");
						Rotate(m_transform.GetRot().GetZaxis(), m_h * rotationDelta);
						break;
					}
					case X_ROTATE_NEG: {
						if(DBG) Dbg.p("rotate x-\n");
						Rotate(m_transform.GetRot().GetXaxis(), m_h * -rotationDelta);
						break;
					}
					case Y_ROTATE_NEG: {
						if(DBG) Dbg.p("rotate y-\n");
						Rotate(m_transform.GetRot().GetYaxis(), m_h * -rotationDelta);
						break;
					}
					case Z_ROTATE_NEG: {
						if(DBG) Dbg.p("rotate z-\n");
						Rotate(m_transform.GetRot().GetZaxis(), m_h * -rotationDelta);
						break;
					}
				}
			}

			if((ki.key1Pressed > 0) && !input.GetKey(ki.key1)) {
				if(DBG) Dbg.p(String.format("i=%d: key1=%d released\n", i, ki.key1));
				ki.key1Pressed = 0;
				ki.timeInNsBothPressed = 0;
			}
			if((ki.key2Pressed > 0) && !input.GetKey(ki.key2)) {
				if(DBG) Dbg.p(String.format("i=%d: key2=%d released\n", i, ki.key2));
				ki.key2Pressed = 0;
				ki.timeInNsBothPressed = 0;
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
