package io.dto.excelSample;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * Excel出力用のサンプルデータ格納DTO
 */
@Data
public class ExcelSampleDto {
	/** 数字 */
	private Integer id;
	/** 文字 */
	private String name;
	/** 真偽値 */
	private Boolean active;
	/** 日付 */
	private LocalDate birthDate;
	/** 日時 */
	private LocalDateTime updatedAt;
	/** 小数 */
	private Double score;
	/** null */
	private String note;
}
