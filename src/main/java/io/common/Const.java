package io.common;

import java.time.format.DateTimeFormatter;

/**
 * 共通定数
 */
public class Const {
	
	/** SHIFT_JIS */
	public static final String SHIFT_JIS = "SHIFT_JIS";
	
	/** SHIFT_JIS */
	public static final String UTF_8 = "UTF-8";
	
	/** yyyy/MM/dd HH:mm:ss形式 */
	public static final DateTimeFormatter DT_FMT_YYYY_MM_DD_HH_MM_SS =
    		DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	
	/** yyyy/MM/dd形式 */
	public static final DateTimeFormatter DT_FMT_YYYY_MM_DD =
    		DateTimeFormatter.ofPattern("yyyy/MM/dd");

}
