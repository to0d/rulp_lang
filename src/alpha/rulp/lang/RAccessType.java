package alpha.rulp.lang;

import static alpha.rulp.lang.Constant.A_DEFAULT;
import static alpha.rulp.lang.Constant.A_PRIVATE;
import static alpha.rulp.lang.Constant.A_PUBLIC;
import static alpha.rulp.lang.Constant.O_Default;
import static alpha.rulp.lang.Constant.O_Nan;
import static alpha.rulp.lang.Constant.O_Private;
import static alpha.rulp.lang.Constant.O_Public;

public enum RAccessType {

	DEFAULT, PUBLIC, PRIVATE;

	public static RAccessType toType(String name) throws RException {

		switch (name) {
		case A_PUBLIC:
			return PUBLIC;

		case A_PRIVATE:
			return PRIVATE;

		case A_DEFAULT:
			return DEFAULT;

		default:
			throw new RException("Unknow RAccessType:" + name);
		}
	}

	public static IRAtom toObject(RAccessType type) {

		switch (type) {
		case PUBLIC:
			return O_Public;

		case PRIVATE:
			return O_Private;

		case DEFAULT:
			return O_Default;

		default:
			return O_Nan;
		}
	}

}
