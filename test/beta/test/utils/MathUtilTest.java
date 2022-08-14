package beta.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.lang.RRelationalOperator;
import alpha.rulp.utils.MathUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;

class MathUtilTest extends RulpTestBase {

	@Test
	void test_computeRelationalExpression_1() {

		_setup();

		try {

			MathUtil.computeRelationalExpression(RRelationalOperator.EQ, RulpFactory.createInteger(1),
					RulpFactory.createAtom("abc"));
			fail("should fail");
		} catch (RException e) {
//			e.printStackTrace();
			assertEquals("alpha.rulp.lang.RException: Invalid rational expression: (EQ 1 abc)", e.toString());
		}
	}
	
//	@Test
//	void test_computeRelationalExpression_1() {
//		
//	}

}
