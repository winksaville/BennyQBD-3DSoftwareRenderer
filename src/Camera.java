import java.awt.event.KeyEvent;

public class Camera
{
	private static final Vector4f Y_AXIS = new Vector4f(0,1,0);

	private Transform m_transform;
	private Matrix4f m_projection;

	private Transform GetTransform()
	{
		return m_transform;
	}

	public Camera(Matrix4f projection)
	{
		this.m_projection = projection;
		this.m_transform = new Transform();
	}

	public Camera(Matrix4f projection, Vector4f position, Vector4f lookAtPoint)
	{
		this.m_projection = projection;
		Transform transform = new Transform(position);
		Quaternion quarternion = transform.GetLookAtRotation(lookAtPoint, new Vector4f(0, 1, 0));
		this.m_transform = transform.Rotate(quarternion);
	}

	public Matrix4f GetViewProjection()
	{
		Matrix4f cameraRotation = GetTransform().GetTransformedRot().Conjugate().ToRotationMatrix();
		Vector4f cameraPos = GetTransform().GetTransformedPos().Mul(-1);

		Matrix4f cameraTranslation = new Matrix4f().InitTranslation(cameraPos.GetX(), cameraPos.GetY(), cameraPos.GetZ());

		return m_projection.Mul(cameraRotation.Mul(cameraTranslation));
	}

	public void Update(Input input, float delta)
	{
		// Speed and rotation amounts are hardcoded here.
		// In a more general system, you might want to have them as variables.
		final float sensitivityX = 2.66f * delta;
		final float sensitivityY = 2.0f * delta;
		final float movAmt = 5.0f * delta;

		// Similarly, input keys are hardcoded here.
		// As before, in a more general system, you might want to have these as variables.

		// Translate/Move the camera
		if(input.GetKey(KeyEvent.VK_E)) // Forward
			Move(GetTransform().GetRot().GetForward(), movAmt);
		if(input.GetKey(KeyEvent.VK_D)) // Backward
			Move(GetTransform().GetRot().GetForward(), -movAmt);
		if(input.GetKey(KeyEvent.VK_S)) // Left
			Move(GetTransform().GetRot().GetLeft(), movAmt);
		if(input.GetKey(KeyEvent.VK_F)) // Right
			Move(GetTransform().GetRot().GetRight(), movAmt);
		
		// Rotate camera
		if(input.GetKey(KeyEvent.VK_L)) // Right
			Rotate(Y_AXIS, sensitivityX);
		if(input.GetKey(KeyEvent.VK_J)) // Left
			Rotate(Y_AXIS, -sensitivityX);
		if(input.GetKey(KeyEvent.VK_K)) // Down
			Rotate(GetTransform().GetRot().GetRight(), sensitivityY);
		if(input.GetKey(KeyEvent.VK_I)) // Up
			Rotate(GetTransform().GetRot().GetRight(), -sensitivityY);
	}

	private void Move(Vector4f dir, float amt)
	{
		m_transform = GetTransform().SetPos(GetTransform().GetPos().Add(dir.Mul(amt)));
	}

	private void Rotate(Vector4f axis, float angle)
	{
		m_transform = GetTransform().Rotate(new Quaternion(axis, angle));
	}
}
