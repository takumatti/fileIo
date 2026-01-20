package io.service.excelImportSample;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.dto.excelImportSample.ExcelRowDto;

/**
 * Excel取込サンプルサービス
 */
@Service
public class ExcelImportSampleService {

	/**
	 * 取込処理
	 * 
	 * @param excelFile		取込ファイル
	 * @throws IOException	例外情報
	 */
	public List<ExcelRowDto> importExcel(MultipartFile excelFile) throws IOException {

		List<ExcelRowDto> result = new ArrayList<>();

		try (InputStream is = excelFile.getInputStream();
				Workbook workbook = new XSSFWorkbook(is)) {

			// 1シート目
			Sheet sheet = workbook.getSheetAt(0);

			for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null) {
					continue;
				}

				List<String> rowValues = new ArrayList<>();

				for (int col = 0; col < row.getLastCellNum(); col++) {
					Cell cell = row.getCell(col);
					rowValues.add(getCellValueAsString(cell));
				}

				result.add(new ExcelRowDto(rowValues));
			}
		}

		return result;
	}

	/**
	 * 取り込んだセルの値を文字列に変換
	 * 
	 * @param cell	セルの値
	 * @return		変換した文字列
	 */
	private String getCellValueAsString(Cell cell) {

		if (cell == null) {
			return null;
		}

		switch (cell.getCellType()) {
		// 文字
		case STRING:
			return cell.getStringCellValue().trim();
		// 数値
		case NUMERIC:
			// 日付判定
			if (DateUtil.isCellDateFormatted(cell)) {
				return formatDate(cell.getDateCellValue());
			}
			// 数値はそのまま文字列
			return formatNumeric(cell.getNumericCellValue());
		// 真偽値
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		// 数式
		case FORMULA:
			return getFormulaValue(cell);
		// 空
		case BLANK:
			return null;
		// それ以外
		default:
			return cell.toString().trim();
		}
	}

	/**
	 * 数値の文字列化（指数表記防止）
	 * 
	 * @param value		変換前の値
	 * @return			変換後文字列
	 */
	private String formatNumeric(double value) {
		BigDecimal bd = BigDecimal.valueOf(value);
		return bd.stripTrailingZeros().toPlainString();
	}

	/**
	 * 日付の文字列化
	 * 
	 * @param date		変換する日付
	 * @return			変換後文字列
	 */
	private String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(date);
	}

	/**
	 * 数式セルの評価
	 * 
	 * @param cell		セルの値
	 * @return			変換後文字列
	 */
	private String getFormulaValue(Cell cell) {

		FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();

		CellValue evaluated = evaluator.evaluate(cell);

		if (evaluated == null) {
			return null;
		}

		switch (evaluated.getCellType()) {
		case STRING:
			return evaluated.getStringValue().trim();
		case NUMERIC:
			return formatNumeric(evaluated.getNumberValue());
		case BOOLEAN:
			return String.valueOf(evaluated.getBooleanValue());
		default:
			return null;
		}
	}
}
