package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_OPT_LCO;
import static alpha.rulp.lang.Constant.T_Expr;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.AttrUtil;

public class LCOUtil {

	protected static AtomicInteger argCount = new AtomicInteger(0);

	protected static AtomicInteger hitCount = new AtomicInteger(0);

	protected static AtomicInteger passCount = new AtomicInteger(0);

	protected static AtomicInteger rebuildCount = new AtomicInteger(0);

	public static int getArgCount() {
		return argCount.get();
	}

	public static int getHitCount() {
		return hitCount.get();
	}

	public static int getPassCount() {
		return passCount.get();
	}

	public static int getRebuildCount() {
		return rebuildCount.get();
	}

	public static void incHitCount() {
		hitCount.incrementAndGet();
	}

	public static void incPassCount() {
		passCount.incrementAndGet();
	}

	public static boolean rebuild(List<IRParaAttr> paras) throws RException {

		rebuildCount.incrementAndGet();

		int update = 0;
		for (IRParaAttr attr : paras) {
			if (attr.getParaType() != T_Expr) {
				AttrUtil.addAttribute(attr, A_OPT_LCO);
				argCount.incrementAndGet();
				update++;
			}
		}

		return update > 0;
	}

	public static void reset() {

		rebuildCount.set(0);
		argCount.set(0);
		passCount.set(0);
		hitCount.set(0);
	}

}
