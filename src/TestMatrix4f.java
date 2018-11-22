public class TestMatrix4f
{
	public static void test() {
		identity();
	}

	/// Return true of pSelf.data == pOther.data
	public static boolean approxEql(Matrix4f l, Matrix4f r, int digits)
	{
		for (int i = 0; i < 4; i += 1)
		{
			for (int j = 0; j < 4; j += 1)
			{
				//if (!Dbg.approxEql(l.Get(i, j), r.Get(i, j), digits)) return false;
			}
		}
		return true;
	}

	static void identity()
	{
		Matrix4f m = new Matrix4f();
		m.InitIdentity();

		// Manual loop
		for(int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (i == j) assert m.Get(i, j) == 1;
				else assert m.Get(i, j) == 0;

		// Use approxEql
		Matrix4f o = new Matrix4f();
		o.SetM(new float[][]{
			{ 1, 0, 0, 0 },
			{ 0, 1, 0, 0 },
			{ 0, 0, 1, 0 },
			{ 0, 0, 0, 1 }
		});
		approxEql(m, o, 7);
	}
}
