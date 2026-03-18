package io.dto.excel;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * Excel取込用Dto
 */
@Data
public class ExcelImportForm {
	/**
	 * 取込ファイル
	 */
	private MultipartFile excelFile;
	
}
