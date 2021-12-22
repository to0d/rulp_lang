package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.F_RETURN_CPS;

import java.util.ArrayList;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public class SCOUtil {

	public static IRExpr rebuildCPS(IRExpr expr, IRFrame frame) throws RException {

		IRObject e0 = expr.get(0);
		if (e0.getType() != RType.ATOM) {
			return expr;
		}

		switch (RulpUtil.asAtom(e0).getName()) {
		case A_DO: {

			ArrayList<IRObject> newExpr = new ArrayList<>();
			newExpr.add(e0);

			IRIterator<? extends IRObject> it = expr.listIterator(1);
			while (it.hasNext()) {

				IRObject e = it.next();
				if (e.getType() == RType.EXPR) {
					e = rebuildCPS((IRExpr) e, frame);
				}

				newExpr.add(e);
			}

			return expr.isEarly() ? RulpFactory.createExpressionEarly(newExpr) : RulpFactory.createExpression(newExpr);
		}

		case F_IF: {

			ArrayList<IRObject> newExpr = new ArrayList<>();
			newExpr.add(e0);
			newExpr.add(expr.get(1));

			IRIterator<? extends IRObject> it = expr.listIterator(2);
			while (it.hasNext()) {
				IRObject e = it.next();
				if (e.getType() == RType.EXPR) {
					e = rebuildCPS((IRExpr) e, frame);
				}

				newExpr.add(e);
			}

			return expr.isEarly() ? RulpFactory.createExpressionEarly(newExpr) : RulpFactory.createExpression(newExpr);
		}

		case F_RETURN:
			if (expr.size() == 2) {
				IRObject e1 = expr.get(1);
				if (e1.getType() == RType.EXPR) {
					return RulpFactory.createExpression(RulpFactory.createAtom(F_RETURN_CPS), e1);
				}
			}

			break;

		default:

		}

		return expr;
	}
}
