public class TestVector4f
{
	static boolean DBG = true;

	public static void test() {
		testConstructor();
		testEqlApproxEql();
	}

	/// Return true if l approximately eqls r with a difference < Math.pow(1, -(digits
	public static boolean approxEql(Vector4f l, Vector4f r, int digits)
	{
		if (!Dbg.approxEql(l.GetX(), r.GetX(), digits)) return false;
		if (!Dbg.approxEql(l.GetY(), r.GetY(), digits)) return false;
		if (!Dbg.approxEql(l.GetZ(), r.GetZ(), digits)) return false;
		if (!Dbg.approxEql(l.GetW(), r.GetW(), digits)) return false;
		return true;
	}

	/// Return true of pSelf.data == pOther.data
	public static boolean eql(Vector4f l, Vector4f r)
	{
		if (l.GetX() != r.GetX()) return false;
		if (l.GetY() != r.GetY()) return false;
		if (l.GetZ() != r.GetZ()) return false;
		if (l.GetW() != r.GetW()) return false;
		return true;
	}

	/// Return true of pSelf.data == pOther.data
	public static void prtV4f(String s, Vector4f v)
	{
		Dbg.p(String.format("%s{ %4.3f, %4.3f, %4.3f, %4.3f }", s, v.GetX(), v.GetY(), v.GetZ(), v.GetW()));
	}

	static void testConstructor() {
		Dbg.p("testConstructor\n");
		Vector4f v1 = new Vector4f(1, 2, 3, 4);
		prtV4f("v1=", v1); Dbg.p("\n");
		assert v1.GetX() == 1;
		assert v1.GetY() == 2;
		assert v1.GetZ() == 3;
		assert v1.GetW() == 4;
		v1 = new Vector4f(4, 3, 2);
		assert v1.GetX() == 4;
		assert v1.GetY() == 3;
		assert v1.GetZ() == 2;
		assert v1.GetW() == 1;
	}

	static void testEqlApproxEql() {
		Dbg.p("testEqlAndApproxEql\n");
		Vector4f v1 = new Vector4f(1, 2, 3, 4);
		Vector4f v2 = new Vector4f(1, 2, 3, 4);
		assert eql(v1, v2);
		assert approxEql(v1, v2, 6);
	}
}
