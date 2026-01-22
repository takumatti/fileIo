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
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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

		try (InputStream stream = excelFile.getInputStream();
				XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(stream)) {

			// 1シート目
			XSSFSheet sheet = workbook.getSheetAt(0);

			for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				// 1行データの取得
				XSSFRow row = sheet.getRow(rowIndex);
				
				// 見た目は空行でも行番号のみ飛ぶこともあることを考慮
				if (row == null) {
					continue;
				}

				List<String> rowValues = new ArrayList<>();

				for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
					XSSFCell cell = row.getCell(colIndex);
					String colItem = getCellValueAsString(cell);
					rowValues.add(colItem);
				}

				result.add(new ExcelRowDto(rowValues));
			}
		}

		return result;
	}

	/**
	 * Excelファイル取込値取得 数値・日付・文字列・指数表記を含め、Excel上の見た目通りに返却する
	 * 
	 * @param cell	セルの値
	 * @return		変換した文字列
	 */
	private String getCellValueAsString(XSSFCell cell) {

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
