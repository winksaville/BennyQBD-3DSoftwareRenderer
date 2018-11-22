public class TestMatrix4f
{
	public static void test() {
		identity();
	}

	static void identity()
	{
		Matrix4f m = new Matrix4f();
		m.InitIdentity();
		for(int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (i == j) assert m.Get(i, j) == 1;
				else assert m.Get(i, j) == 0;

	}
}
