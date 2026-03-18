package io.dto.excel;

import java.util.List;

import lombok.Data;

/**
 * Excel取込データ格納用DTO
 */
@Data
public class ExcelRowDto {
	/**
	 * 1行データ格納リスト
	 */
	private List<String> values;
	/**
	 * コンストラクタ
	 * 
	 * @param values	1行データ格納リスト
	 */
	public ExcelRowDto(List<String> values) {
        this.values = values;
    }

}
