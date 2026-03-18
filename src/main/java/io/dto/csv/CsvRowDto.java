package io.dto.csv;

import java.util.List;

import lombok.Data;

/**
 * CSV取込データ格納用DTO
 */
@Data
public class CsvRowDto {
	/**
	 * 1行データ格納リスト
	 */
	private List<String> values;
	
	/**
	 * コンストラクタ
	 * 
	 * @param values	1行データ格納リスト
	 */
	public CsvRowDto(List<String> values) {
        this.values = values;
    }

}
