package io.dto.csvImportSample;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * CSV取込用Dto
 */
@Data
public class CsvImportForm {

	/**
	 * 取込ファイル
	 */
	private MultipartFile csvFile;
	
}
