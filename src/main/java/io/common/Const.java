package io.common;

import java.time.format.DateTimeFormatter;

/**
 * 共通定数
 */
public class Const {
	
	/** SHIFT_JIS */
	public static final String SHIFT_JIS = "SHIFT_JIS";
	
	/** UTF-8 */
	public static final String UTF_8 = "UTF-8";
	
	/** AWS S3 */
	public static final String AWS_S3 = "s3";
	
	/** ローカル */
	public static final String LOCAL = "local";
	
	/** 出力 */
	public static final String EXPORT = "export";
	
	/** yyyy/MM/dd HH:mm:ss形式 */
	public static final DateTimeFormatter DT_FMT_YYYY_MM_DD_HH_MM_SS =
    		DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	
	/** yyyy/MM/dd形式 */
	public static final DateTimeFormatter DT_FMT_YYYY_MM_DD =
    		DateTimeFormatter.ofPattern("yyyy/MM/dd");
	
	/** yyyyMMdd_HHmmss形式 */
	public static final DateTimeFormatter DT_FMT_YYYYMMDD_HHMMSS =
    		DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

}
