package io.dto.dbView;

import lombok.Data;

/**
 * カラム情報DTO
 */
@Data
public class ColumnInfo {
	/** カラム名 */
	private String columnName;

	/** 型 */
	private String columnType;

	/** Null許可判定 */
	private boolean isNullable;

	/** PK判定 */
	private boolean primaryKey;
}