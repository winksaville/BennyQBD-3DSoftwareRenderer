import java.lang.Math;

public class Dbg
{
	public static boolean D = true;

	public static void p(String s) {
		System.out.print(s);
	}

	public static void pl(String s) {
		p(s);
		p("\n");
	}

	public static boolean approxEql(float x, float y, int digits)
	{
		if (digits == 0) return true;

		if (x == y) return true;
		if ((Float.isNaN(x) && Float.isNaN(y)) || (Float.isNaN(-x) && Float.isNaN(-y))) return true;
		if ((Float.isInfinite(x) && Float.isInfinite(y)) || (Float.isInfinite(-x) && Float.isInfinite(-y))) return true;

		double abs_diff = Math.abs(x - y);
		if (D) Dbg.p(String.format("abs_diff=%f\n", abs_diff));
		if (Double.isNaN(abs_diff) || Double.isInfinite(abs_diff)) return false;

		double max_diff = Math.pow(10, -(digits - 1));
		if (D) Dbg.p(String.format("max_diff=%f\n", max_diff));
		if (abs_diff <= max_diff) return true;

		double largest = Math.max(Math.abs(x), Math.abs(y));
		double scaled_max_diff = largest * max_diff / 10;

		return abs_diff <= scaled_max_diff;
	}

	public static void testApproxEql()
	{
		assert Dbg.approxEql(2f, 1f, 0);
		test_993e12_4_digits();
		test_sub_near0_f();
	}

	static void test_993e12_4_digits() {
		assert !approxEql(992.9e12f, 993.0e12f, 4);
		assert approxEql(993.0e12f, 993.0e12f, 4);
		assert !approxEql(993.1e12f, 993.0e12f, 4);
	}

	static void test_sub_near0_f()
	{
		float x = 0;
		float end = sub(1f, x, 1000);
		if (D) p(String.format("x=%f end=%f diff=%f\n", x, end, Math.abs(x - end)));
		assert(x != end);

		// Either scaled_epsilon or scaled_max_diff worked
		assert approxEql(x, end, 0);
		assert approxEql(x, end, 1);
		assert approxEql(x, end, 2);
		assert approxEql(x, end, 3);
		assert approxEql(x, end, 4);
		assert approxEql(x, end, 5);
		assert approxEql(x, end, 6);
		assert !approxEql(x, end, 7);
		assert !approxEql(x, end, 8);
		assert !approxEql(x, end, 9);
		assert !approxEql(x, end, 10);
		assert !approxEql(x, end, 11);
		assert !approxEql(x, end, 12);
		assert !approxEql(x, end, 13);
		assert !approxEql(x, end, 14);
		assert !approxEql(x, end, 15);
		assert !approxEql(x, end, 16);
		assert !approxEql(x, end, 17);
	}

	static float sub(float start, float end, int count)
	{
		float step = (start - end) / count;
		float r = start;

		for (int j = 0; j < count; j += 1)
		{
			r -= step;
		}
		return r;
	}
}
