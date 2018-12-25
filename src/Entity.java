import java.awt.event.KeyEvent;

public class Entity
{
	private Mesh m_mesh;
	private Movement m_movement;

	// Entity with Rotation and Translation Input
	//
	// translationKey = Shift
	// rotationKey = control
	//
	//                 (k)
	//                 +y     (u)
	//                  |    +z
	//                  |    /
	//                  |   /
	//                  |  /
	//                  | /
	//                  |/
	// (h) -x ----------o---------- +x (l)
	//                 /|
	//                / |
	//               /  |
	//              /   |
	//             /    |
	//           -z     |
	//          (n)    -y
	//                 (j)
	// 
	public Entity(Mesh mesh, Vector4f position, Vector4f lookAtPoint, Vector4f up)
	{
		this(mesh, position, lookAtPoint, up,
			KeyEvent.VK_L, KeyEvent.VK_K, KeyEvent.VK_U,
			KeyEvent.VK_H, KeyEvent.VK_J, KeyEvent.VK_N,
			KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL);
	}

	// Entity with Rotation and Translation Input
	//
	// rotationKey
	// translationKey
	//
	//                 (yPlusKey)
	//                 +y     (zPlusKey)
	//                  |    +z
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
	//           -z     |
	//     (zNegKey)   -y
	//                 (yNegKey)
	// 
	public Entity(Mesh mesh, Vector4f position, Vector4f lookAtPoint, Vector4f up,
			int xPlusKey, int yPlusKey, int zPlusKey,
			int xNegKey,  int yNegKey,  int zNegKey,
			int translateKey, int rotateKey)
	{
		Dbg.p(String.format("x+=%d y+=%d z+=%d\n", xPlusKey, yPlusKey, zPlusKey));
		Dbg.p(String.format("x-=%d y-=%d z-=%d\n", xNegKey, yNegKey, zNegKey));
		Dbg.p(String.format("tr=%d ro=%d\n", translateKey, rotateKey));
		m_mesh = mesh;
		m_movement = new Movement(position, lookAtPoint, up,
			xPlusKey, yPlusKey, zPlusKey,
			xNegKey, yNegKey, zNegKey,
			translateKey, rotateKey);
	}

	public void Update(Input input, float translationDelta, float rotationDelta)
	{
		m_movement.Update(input, translationDelta, rotationDelta);
	}

	public Mesh GetMesh()
	{
		return m_mesh;
	}

	public Transform GetTransform()
	{
		return m_movement.GetTransform();
	}

	public void Translate(Vector4f dir, float amt)
	{
		m_movement.Translate(dir, amt);
	}

	public void Rotate(Vector4f axis, float angle)
	{
		m_movement.Rotate(axis, angle);
	}
}
