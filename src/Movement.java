import java.awt.event.KeyEvent;

class Movement {

	final boolean DBG = false;

	private Vector4f X_AXIS = new Vector4f(1, 0, 0);
	private Vector4f Y_AXIS = new Vector4f(0, 1, 0);
	private Vector4f Z_AXIS = new Vector4f(0, 0, 1);

	private class KeyInfo {
		boolean key1Pressed;
		int key1;
		boolean key2Pressed;
		int key2;

		KeyInfo(int key1, int key2) {
			this.key1Pressed = false;
			this.key1 = key1;
			this.key2Pressed = false;
			this.key2 = key2;
		}
	};

	private KeyInfo m_keys[] = new KeyInfo[12];
	private boolean m_changed;
	private Transform m_transform;

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
		m_keys[X_TRANS_PLUS] = new KeyInfo(xPlusKey, translateKey);
		m_keys[Y_TRANS_PLUS] = new KeyInfo(yPlusKey, translateKey);
		m_keys[Z_TRANS_PLUS] = new KeyInfo(zPlusKey, translateKey);
		m_keys[X_TRANS_NEG ] = new KeyInfo(xNegKey,  translateKey);
		m_keys[Y_TRANS_NEG ] = new KeyInfo(yNegKey,  translateKey);
		m_keys[Z_TRANS_NEG ] = new KeyInfo(zNegKey,  translateKey);

		m_keys[X_ROTATE_PLUS] = new KeyInfo(xPlusKey, rotationKey);
		m_keys[Y_ROTATE_PLUS] = new KeyInfo(yPlusKey, rotationKey);
		m_keys[Z_ROTATE_PLUS] = new KeyInfo(zPlusKey, rotationKey);
		m_keys[X_ROTATE_NEG ] = new KeyInfo(xNegKey,  rotationKey);
		m_keys[Y_ROTATE_NEG ] = new KeyInfo(yNegKey,  rotationKey);
		m_keys[Z_ROTATE_NEG ] = new KeyInfo(zNegKey,  rotationKey);
	}

	public void Update(Input input, float translationDelta, float rotationDelta)
	{
		for (int i = 0; i < m_keys.length; i++) {
			KeyInfo ki = m_keys[i];

			if(ki.key1Pressed && ki.key2Pressed && (!input.GetKey(ki.key1) || !input.GetKey(ki.key2))) {
				Dbg.p(String.format("i=%d key1=%d key2=%d pressed\n", i, ki.key1, ki.key2));
				this.m_changed = true;
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
						Rotate(m_transform.GetRot().GetXaxis(), rotationDelta);
						break;
					}
					case Y_ROTATE_PLUS: {
						if(DBG) Dbg.p("rotate y+\n");
						Rotate(m_transform.GetRot().GetYaxis(), rotationDelta);
						break;
					}
					case Z_ROTATE_PLUS: {
						if(DBG) Dbg.p("rotate z+\n");
						Rotate(m_transform.GetRot().GetZaxis(), rotationDelta);
						break;
					}
					case X_ROTATE_NEG: {
						if(DBG) Dbg.p("rotate x-\n");
						Rotate(m_transform.GetRot().GetXaxis(), -rotationDelta);
						break;
					}
					case Y_ROTATE_NEG: {
						if(DBG) Dbg.p("rotate y-\n");
						Rotate(m_transform.GetRot().GetYaxis(), -rotationDelta);
						break;
					}
					case Z_ROTATE_NEG: {
						if(DBG) Dbg.p("rotate z-\n");
						Rotate(m_transform.GetRot().GetZaxis(), -rotationDelta);
						break;
					}
				}
			}

			if(!ki.key1Pressed && input.GetKey(ki.key1)) {
				if(DBG) Dbg.p(String.format("key1=%d pressed\n", ki.key1));
				ki.key1Pressed = true;
			}
			if(!ki.key2Pressed && input.GetKey(ki.key2)) {
				if(DBG) Dbg.p(String.format("key2=%d pressed\n", ki.key2));
				ki.key2Pressed = true;
			}

			if(ki.key1Pressed && !input.GetKey(ki.key1)) {
				if(DBG) Dbg.p(String.format("key1=%d released\n", ki.key1));
				ki.key1Pressed = false;
			}
			if(ki.key2Pressed && !input.GetKey(ki.key2)) {
				if(DBG) Dbg.p(String.format("key2=%d released\n", ki.key2));
				ki.key2Pressed = false;
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
