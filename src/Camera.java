import java.awt.event.KeyEvent;

public class Camera
{
	private static final Vector4f Y_AXIS = new Vector4f(0,1,0);

	private Movement m_movement;
	private Matrix4f m_projection;

	private Transform GetTransform()
	{
		return m_movement.GetTransform();
	}

	public Camera(Matrix4f projection)
	{
		Vector4f position = new Vector4f(0, 0, 0);
		Vector4f lookAtPoint = new Vector4f(0, 0, 1);
		this.Init(projection, position, lookAtPoint, true);
	}

	public Camera(Matrix4f projection, Vector4f position, Vector4f lookAtPoint, boolean clockwise)
	{
		this.Init(projection, position, lookAtPoint, clockwise);
	}

	public void Init(Matrix4f projection, Vector4f position, Vector4f lookAtPoint, boolean clockwise)
	{
		this.m_projection = projection;
		Vector4f upAxis = new Vector4f(0, 1, 0);
		this.m_movement = new Movement(position, lookAtPoint, upAxis, clockwise,
			KeyEvent.VK_L, KeyEvent.VK_K, KeyEvent.VK_U,
			KeyEvent.VK_H, KeyEvent.VK_J, KeyEvent.VK_N,
			KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL);
	}

	public Matrix4f GetViewProjection()
	{
		Matrix4f cameraRotation = GetTransform().GetTransformedRot().Conjugate().ToRotationMatrix();
		Vector4f cameraPos = GetTransform().GetTransformedPos().Mul(-1);

		Matrix4f cameraTranslation = new Matrix4f().InitTranslation(cameraPos.GetX(), cameraPos.GetY(), cameraPos.GetZ());

		return m_projection.Mul(cameraRotation.Mul(cameraTranslation));

		// Neither of these work the correctly, they flip the z-axis but
		// other things too :(

		// From: https://docs.microsoft.com/en-us/windows/uwp/graphics-concepts/coordinate-systems
		// I didn't "flip order of triangle vertices" as they seem OK but maybe not.
		// Scale "world space by -1 in the z-direction"
		//Matrix4f view = m_projection.Mul(cameraRotation.Mul(cameraTranslation));
		//view.Set(2, 0, -view.Get(2, 0));
		//view.Set(2, 1, -view.Get(2, 1));
		//view.Set(2, 2, -view.Get(2, 2));
		//view.Set(2, 3, -view.Get(2, 3));
		//return view;

		// From: http://www.techart3d.com/2016/02/convert-left-handed-to-right-handed-coordinates/
		//Matrix4f view = m_projection.Mul(cameraRotation.Mul(cameraTranslation));
		////Matrix4f identityMinusZ = new Matrix4f();
		//return  identityMinusZ.Mul(view.Mul(identityMinusZ));
		//return  view.Mul(identityMinusZ.Mul(view));
		//return  view.Mul(identityMinusZ);
		//return  identityMinusZ.Mul(view);
	}

	public void Update(Input input, long timeInNs, float delta)
	{
		m_movement.Update(input, timeInNs, delta * 2, delta * 2);
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
