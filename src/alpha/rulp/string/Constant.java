/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.string;

public interface Constant {

	char CN_CHAR_COLON = 0xff1a; // CN ':'

	char CN_CHAR_COLON2 = 0x2236; // CN '¡Ã'

	char CN_CHAR_COMMNA = 0xff0c; // CN ','

	char CN_CHAR_DUN_HAO = 0x3001; // CN '¡¢'

	char CN_CHAR_EXCLAMATION = 0xff01; // CN '£¡'

	char CN_CHAR_LEFT_BRACKET = 0x3010; // CN '['

	char CN_CHAR_LEFT_KUOHU = 0x300c; // CN '¡¸'

	char CN_CHAR_LEFT_PARENTHESIS = 0xff08; // CN '('

	char CN_CHAR_LEFT_PIE = 0x2018; // CN '¡®'

	char CN_CHAR_LEFT_QUOTE = 0x201c; // CN '¡°'

	char CN_CHAR_LEFT_SHU_MING = 0x300a; // CN '<'

	char CN_CHAR_LONG_HORIZONTAL_LINE = 0x2501; // CN '©¥'

	char CN_CHAR_LONG_HORIZONTAL_LINE2 = 0x2500; // CN '©¤'

	char CN_CHAR_PERIOD = 0x3002; // CN '.'

	char CN_CHAR_QUESTION_MARK = 0xff1f; // CN '?'

	char CN_CHAR_RIGHT_BRACKET = 0x3011; // CN ']'

	char CN_CHAR_RIGHT_PARENTHESIS = 0xff09; // CN ')'

	char CN_CHAR_RIGHT_QUOTE = 0x201d; // CN '¡±'

	char CN_CHAR_RIGHT_SHU_MIN = 0x300b; // CN '>'

	char CN_CHAR_RIGHTT_KUOHU = 0x300d; // CN '¡¹'

	char CN_CHAR_RIGHTT_PIE = 0x2019; // CN '¡¯'

	char CN_CHAR_SEMICOLON = 0xff1b; // CN ';'

	char CN_CHAR_SPACE = 0x3000; // CN ' '

	char CN_CHAR_UNAME_1 = 0x25cb; // CN '¡ð'

	char CN_CHAR_UNAME_10 = 0x2299; // CN '¡Ñ'

	char CN_CHAR_UNAME_11 = 0x25b2; // CN '¡ø'

	char CN_CHAR_UNAME_12 = 0x25bc; // CN '¨‹'

	char CN_CHAR_UNAME_13 = 0x03a7; // CN '¦¶'

	char CN_CHAR_UNAME_14 = 0x2605; // CN '¡ï'

	char CN_CHAR_UNAME_15 = 0x043e; // CN '§à'

	char CN_CHAR_UNAME_16 = 0x2013; // CN '¨C'

	char CN_CHAR_UNAME_17 = 0x2014; // CN '¡ª'

	char CN_CHAR_UNAME_18 = 0xff0d; // CN '£­'

	char CN_CHAR_UNAME_19 = 0x2026; // CN '¡­'

	char CN_CHAR_UNAME_2 = 0x00d7; // CN '¡Á'

	char CN_CHAR_UNAME_21 = 0x3008; // CN '¡´'

	char CN_CHAR_UNAME_22 = 0x3009; // CN '¡µ'

	char CN_CHAR_UNAME_23 = 0x300e; // CN '¡º'

	char CN_CHAR_UNAME_24 = 0x300f; // CN '¡»'

	char CN_CHAR_UNAME_25 = 0x2015; // CN '¨D'

	char CN_CHAR_UNAME_26 = 0x3014; // CN '¡²'

	char CN_CHAR_UNAME_27 = 0x3015; // CN '¡³'

	char CN_CHAR_UNAME_28 = 0x301d; // CN '¨”'

	char CN_CHAR_UNAME_29 = 0x301e; // CN '¨•'

	char CN_CHAR_UNAME_30 = 0x203b; // CN '¡ù'

	char CN_CHAR_UNAME_31 = 0xff0e; // CN '£®'

	char CN_CHAR_UNAME_32 = 0xff0f; // CN '£¯'

	char CN_CHAR_UNAME_4 = 0x25a1; // CN '¡õ'

	char CN_CHAR_UNAME_5 = 0x007e; // CN '~'

	char CN_CHAR_UNAME_6 = 0x25c6; // CN '¡ô'

	char CN_CHAR_UNAME_7 = 0x25cf; // CN '¡ñ'

	char CN_CHAR_UNAME_8 = 0x25a0; // CN '¡ö'

	char CN_CHAR_UNAME_9 = 0x2103; // CN '¡æ'

	char EN_CHAR_ENTER = 0x0D;// '\r';

	char EN_CHAR_NEWLINE = 0x0A; // '\n';

	char EN_CHAR_SPACE = 0x20;

	char EN_CHAR_TAB = 0x09;

	char EN_SEPARATION_DOT = 0xb7; // '¡¤'

	String GBK = "GBK"; // Chinese

	String UTF_8 = "UTF-8";

	String EN_STR_SPACE = "" + EN_CHAR_SPACE;

	String EN_STR_TAB = "" + EN_CHAR_TAB;

	String GB2312 = "GB2312";

	String ISO_8859_1 = "ISO-8859-1"; // ISO-LATIN-1

	char TXT_BOM = 65279;

	String US_ASCII = "US-ASCII";

	String UTF_16 = "UTF-16";

	String UTF_16BE = "UTF-16BE"; // BE: Big Endian

	String UTF_16LE = "UTF-16LE"; // LE: Little-endian

}
