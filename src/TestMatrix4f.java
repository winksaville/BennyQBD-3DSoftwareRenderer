public class TestMatrix4f
{
	static boolean DBG = true;

	public static void test() {
		identity();
		matrix_4x4_mul_4x4();
	}

	/// Return true if l approximately eqls r with a difference < Math.pow(1, -(digits
	public static boolean approxEql(Matrix4f l, Matrix4f r, int digits)
	{
		for (int i = 0; i < 4; i += 1)
		{
			for (int j = 0; j < 4; j += 1)
			{
				if (!Dbg.approxEql(l.Get(i, j), r.Get(i, j), digits)) return false;
			}
		}
		return true;
	}

	/// Return true of pSelf.data == pOther.data
	public static boolean eql(Matrix4f l, Matrix4f r)
	{
		for (int i = 0; i < 4; i += 1)
		{
			for (int j = 0; j < 4; j += 1)
			{
				if (l.Get(i, j) != r.Get(i, j)) return false;
			}
		}
		return true;
	}

	/// Return true of pSelf.data == pOther.data
	public static void pM44f(String s, Matrix4f m)
	{
		Dbg.p(String.format("%sfloat [][]{\n", s));
		for (int i = 0; i < 4; i += 1)
		{
			Dbg.p("  {");
			for (int j = 0; j < 4; j += 1)
			{
				Dbg.p(String.format("%f", m.Get(i,j)));
				if (j < 3) Dbg.p(", ");
			}
			Dbg.p("}");
			if (i < 3) Dbg.p(",");
			Dbg.p("\n");

		}
		Dbg.p("}\n");
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

		// Use approxEql and eql
		Matrix4f o = new Matrix4f();
		o.SetM(new float[][]{
			{ 1, 0, 0, 0 },
			{ 0, 1, 0, 0 },
			{ 0, 0, 1, 0 },
			{ 0, 0, 0, 1 }
		});

		// Assert eql and approxEql
		assert eql(m, o);
		assert approxEql(m, o, 7);
	}

	static void matrix_4x4_mul_4x4()
	{
		Matrix4f m1 = new Matrix4f();
		m1.SetM(new float [][]{
			{ 1, 2, 3, 4 },
			{ 5, 6, 7, 8 },
			{ 9, 10, 11, 12 },
			{ 13, 14, 15, 16 },
		});
		if (DBG) pM44f("matrix.4x4*4x4: m1\n", m1);

		Matrix4f m2 = new Matrix4f();
		m2.SetM(new float [][]{
			{ 13, 14, 15, 16 },
			{ 9, 10, 11, 12 },
			{ 5, 6, 7, 8 },
			{ 1, 2, 3, 4 },
		});
		if (DBG) pM44f("matrix.4x4*4x4: m2\n", m2);

		Matrix4f m3 = m1.Mul(m2);
		if (DBG) pM44f("matrix.4x4*4x4: m3\n", m3);

		Matrix4f expected = new Matrix4f();
		expected.SetM(new float[][] {
			{
				(m1.Get(0, 0) * m2.Get(0, 0)) + (m1.Get(0, 1) * m2.Get(1, 0)) + (m1.Get(0, 2) * m2.Get(2, 0)) + (m1.Get(0, 3) * m2.Get(3, 0)),
				(m1.Get(0, 0) * m2.Get(0, 1)) + (m1.Get(0, 1) * m2.Get(1, 1)) + (m1.Get(0, 2) * m2.Get(2, 1)) + (m1.Get(0, 3) * m2.Get(3, 1)),
				(m1.Get(0, 0) * m2.Get(0, 2)) + (m1.Get(0, 1) * m2.Get(1, 2)) + (m1.Get(0, 2) * m2.Get(2, 2)) + (m1.Get(0, 3) * m2.Get(3, 2)),
				(m1.Get(0, 0) * m2.Get(0, 3)) + (m1.Get(0, 1) * m2.Get(1, 3)) + (m1.Get(0, 2) * m2.Get(2, 3)) + (m1.Get(0, 3) * m2.Get(3, 3))
			},
			{
				(m1.Get(1, 0) * m2.Get(0, 0)) + (m1.Get(1, 1) * m2.Get(1, 0)) + (m1.Get(1, 2) * m2.Get(2, 0)) + (m1.Get(1, 3) * m2.Get(3, 0)),
				(m1.Get(1, 0) * m2.Get(0, 1)) + (m1.Get(1, 1) * m2.Get(1, 1)) + (m1.Get(1, 2) * m2.Get(2, 1)) + (m1.Get(1, 3) * m2.Get(3, 1)),
				(m1.Get(1, 0) * m2.Get(0, 2)) + (m1.Get(1, 1) * m2.Get(1, 2)) + (m1.Get(1, 2) * m2.Get(2, 2)) + (m1.Get(1, 3) * m2.Get(3, 2)),
				(m1.Get(1, 0) * m2.Get(0, 3)) + (m1.Get(1, 1) * m2.Get(1, 3)) + (m1.Get(1, 2) * m2.Get(2, 3)) + (m1.Get(1, 3) * m2.Get(3, 3))
			},
			{
				(m1.Get(2, 0) * m2.Get(0, 0)) + (m1.Get(2, 1) * m2.Get(1, 0)) + (m1.Get(2, 2) * m2.Get(2, 0)) + (m1.Get(2, 3) * m2.Get(3, 0)),
				(m1.Get(2, 0) * m2.Get(0, 1)) + (m1.Get(2, 1) * m2.Get(1, 1)) + (m1.Get(2, 2) * m2.Get(2, 1)) + (m1.Get(2, 3) * m2.Get(3, 1)),
				(m1.Get(2, 0) * m2.Get(0, 2)) + (m1.Get(2, 1) * m2.Get(1, 2)) + (m1.Get(2, 2) * m2.Get(2, 2)) + (m1.Get(2, 3) * m2.Get(3, 2)),
				(m1.Get(2, 0) * m2.Get(0, 3)) + (m1.Get(2, 1) * m2.Get(1, 3)) + (m1.Get(2, 2) * m2.Get(2, 3)) + (m1.Get(2, 3) * m2.Get(3, 3))
			},
			{
				(m1.Get(3, 0) * m2.Get(0, 0)) + (m1.Get(3, 1) * m2.Get(1, 0)) + (m1.Get(3, 2) * m2.Get(2, 0)) + (m1.Get(3, 3) * m2.Get(3, 0)),
				(m1.Get(3, 0) * m2.Get(0, 1)) + (m1.Get(3, 1) * m2.Get(1, 1)) + (m1.Get(3, 2) * m2.Get(2, 1)) + (m1.Get(3, 3) * m2.Get(3, 1)),
				(m1.Get(3, 0) * m2.Get(0, 2)) + (m1.Get(3, 1) * m2.Get(1, 2)) + (m1.Get(3, 2) * m2.Get(2, 2)) + (m1.Get(3, 3) * m2.Get(3, 2)),
				(m1.Get(3, 0) * m2.Get(0, 3)) + (m1.Get(3, 1) * m2.Get(1, 3)) + (m1.Get(3, 2) * m2.Get(2, 3)) + (m1.Get(3, 3) * m2.Get(3, 3))
			}
		});
		if (DBG) pM44f("matrix.4x4*4x4: expected\n", expected);
		assert eql(m3, expected);
	}
}
