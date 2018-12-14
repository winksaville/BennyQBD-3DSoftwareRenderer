public class TestVector4f
{
	static boolean DBG = true;

	public static void test() {
		testConstructor();
		testEqlApproxEql();
		testSub();
		testDot();
		testCross();
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

	static void testConstructor() {
		Dbg.p("testConstructor\n");
		Vector4f v1 = new Vector4f(1, 2, 3, 4);
		Dbg.prtV4f("v1=", v1); Dbg.p("\n");
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

	static void testSub() {
		Dbg.p("testSub\n");
		Vector4f v1 = new Vector4f(1.5f, 2.5f, 3.5f, 4.5f);
		Vector4f v2 = new Vector4f(4.5f, 3.5f, 2.5f, 1.5f);
		Vector4f v = v1.Sub(v2);
		Dbg.prtV4f("v=", v); Dbg.p("\n");
		assert eql(v, new Vector4f(v1.GetX() - v2.GetX(), v1.GetY() - v2.GetY(), v1.GetZ() - v2.GetZ(), v1.GetW() - v2.GetW()));
	}

	static void testDot() {
		Dbg.p("testDot\n");
		Vector4f v1 = new Vector4f(1.5f, 2.5f, 3.5f, 4.5f);
		Vector4f v2 = new Vector4f(4.5f, 3.5f, 2.5f, 1.5f);
		float d = v1.Dot(v2);
		System.out.printf("d=%4.3f\n", d);
		assert d == v1.GetX() * v2.GetX() + v1.GetY() * v2.GetY() + v1.GetZ() * v2.GetZ() + v1.GetW() * v2.GetW();

		v1 = new Vector4f(3, 2, 1, 0);
		v2 = new Vector4f(1, 2, 3, 0);
		d = v1.Dot(v2);
		System.out.printf("d=%4.3f\n", d);
		assert d == v1.GetX() * v2.GetX() + v1.GetY() * v2.GetY() + v1.GetZ() * v2.GetZ() + v1.GetW() * v2.GetW();
	}

	static void testCross() {
		Dbg.p("testCross\n");
		Vector4f v1 = new Vector4f(1, 0, 0);
		Vector4f v2 = new Vector4f(0, 1, 0);
		Vector4f v;

		// Cross product of two unit vectors on X,Y yields unit vector Z
		v = v1.Cross(v2);
		assert v.GetX() == 0;
		assert v.GetY() == 0;
		assert v.GetZ() == 1;

		v1 = new Vector4f(1.5f, 2.5f, 3.5f);
		v2 = new Vector4f(4.5f, 3.5f, 2.5f);
		v = v1.Cross(v2);
		Dbg.prtV4f("v=", v); Dbg.p("\n");

		assert eql(v, new Vector4f(
					(v1.GetY() * v2.GetZ()) - (v1.GetZ() * v2.GetY()),
					(v1.GetZ() * v2.GetX()) - (v1.GetX() * v2.GetZ()),
					(v1.GetX() * v2.GetY()) - (v1.GetY() * v2.GetX()),
					0));
	}
}
