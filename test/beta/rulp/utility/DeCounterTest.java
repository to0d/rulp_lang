package beta.rulp.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.DeCounter;

class DeCounterTest {

	@Test
	void test_1() {

		DeCounter c = new DeCounter(16);
		assertEquals("total=0,size=16,unit=1,values=[]", c.toString());

		c.add(1);
		c.add(1);
		c.add(2);
		c.add(2);
		c.add(2);
		c.add(2);
		c.add(10);
		assertEquals("total=7,size=16,unit=1,values=[0,2,4,0,0,0,0,0,0,0,1]", c.toString());

		c.add(18);
		c.add(17);
		c.add(18);
		c.add(1);
		assertEquals("total=11,size=16,unit=2,values=[3,4,0,0,0,1,0,0,1,2]", c.toString());

		c.add(1024);
		assertEquals("total=12,size=16,unit=128,values=[11,0,0,0,0,0,0,0,1]", c.toString());
	}

}
