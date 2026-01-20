package io.service.excelExportSample;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import io.common.Const;
import io.dto.excelSample.ExcelSampleDto;

/**
 * Excel出力サンプルサービス
 */
@Service
public class ExcelSampleService {

	/**
	 * ダミーデータ生成
	 * 
	 * @return ダミーデータ情報リスト
	 */
	public List<ExcelSampleDto> createDummyList() {

		List<ExcelSampleDto> list = new ArrayList<>();

		ExcelSampleDto dto1 = new ExcelSampleDto();
		dto1.setId(1);
		dto1.setName("山田太郎");
		dto1.setActive(true);
		dto1.setBirthDate(LocalDate.of(1995, 5, 20));
		dto1.setUpdatedAt(LocalDateTime.now());
		dto1.setScore(98.5);
		dto1.setNote(null);

		ExcelSampleDto dto2 = new ExcelSampleDto();
		dto2.setId(2);
		dto2.setName("Suzuki Hanako");
		dto2.setActive(false);
		dto2.setBirthDate(LocalDate.of(2000, 1, 1));
		dto2.setUpdatedAt(LocalDateTime.now().minusDays(1));
		dto2.setScore(75.0);
		dto2.setNote("備考あり");

		ExcelSampleDto dto3 = new ExcelSampleDto();
		dto3.setId(3);
		dto3.setName("てすと,たろう");
		dto3.setActive(false);
		dto3.setBirthDate(LocalDate.of(9999, 9, 9));
		dto3.setUpdatedAt(LocalDateTime.now().minusDays(10));
		dto3.setScore(75.0);
		dto3.setNote("\"てすと\"");

		list.add(dto1);
		list.add(dto2);
		list.add(dto3);

		return list;
	}

	/**
	 * ダミーデータ生成 + 最終更新日更新
	 * 
	 * @param	ダミーデータ情報
	 */
	public List<ExcelSampleDto> createAndUpdate() {

		List<ExcelSampleDto> list = createDummyList();
		LocalDateTime now = LocalDateTime.now();

		list.forEach(dto -> dto.setUpdatedAt(now));

		return list;
	}

	/**
	 * Excel出力処理（単体用）
	 * 
	 * @param list			Excel出力用データリスト
	 * @param response		レスポンス情報
	 * @throws Exception	例外情報
	 */
	public void exportSingleExcel(List<ExcelSampleDto> list,
			HttpServletResponse response) throws Exception {

		response.setContentType(
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"sample.xlsx\"");

		try (Workbook workbook = new XSSFWorkbook()) {

			createSheet(workbook, "サンプル", list);

			workbook.write(response.getOutputStream());
		}
	}

	/**
	 * Excel出力処理（複数シート）
	 * 
	 * @param list			Excel出力用データリスト
	 * @param response		レスポンス情報
	 * @throws Exception	例外情報
	 */
	public void exportMultiSheetExcel(List<ExcelSampleDto> list,
			HttpServletResponse response) throws Exception {

		response.setContentType(
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"sample_multi_sheet.xlsx\"");

		try (Workbook workbook = new XSSFWorkbook()) {

			// 例として3シート作成
			for (int i = 1; i <= 3; i++) {
				createSheet(workbook, "シート" + i, list);
			}

			workbook.write(response.getOutputStream());
		}
	}

	/**
	 * Excel出力処理（複数、ZIP）
	 * 
	 * @param list			Excel出力用データリスト
	 * @param response		レスポンス情報
	 * @throws Exception	例外情報
	 */
	public void exportMultiExcelZip(List<ExcelSampleDto> list,
			HttpServletResponse response) throws Exception {

		response.setContentType("application/zip");
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"sample_excel.zip\"");

		try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {

			for (int i = 1; i <= 3; i++) {

				ZipEntry entry = new ZipEntry("sample_" + i + ".xlsx");
				zipOut.putNextEntry(entry);

				try (Workbook workbook = new XSSFWorkbook();
						ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

					createSheet(workbook, "サンプル", list);
					workbook.write(baos);

					zipOut.write(baos.toByteArray());
				}

				zipOut.closeEntry();
			}
		}
	}

	/**
	 * シート作成
	 * 
	 * @param workbook		workbook
	 * @param sheetName		シート名
	 * @param list			Excel出力用データリスト
	 */
	private void createSheet(Workbook workbook, String sheetName,
			List<ExcelSampleDto> list) {

		Sheet sheet = workbook.createSheet(sheetName);
		int rowIndex = 0;

		// ヘッダ
		Row header = sheet.createRow(rowIndex++);
		createCell(header, 0, "ID");
		createCell(header, 1, "名前");
		createCell(header, 2, "有効");
		createCell(header, 3, "生年月日");
		createCell(header, 4, "最終更新日時");
		createCell(header, 5, "スコア");
		createCell(header, 6, "備考");

		// 明細
		for (ExcelSampleDto dto : list) {
			Row row = sheet.createRow(rowIndex++);
			createCell(row, 0, dto.getId());
			createCell(row, 1, dto.getName());
			createCell(row, 2, dto.getActive());
			createCell(row, 3, format(dto.getBirthDate()));
			createCell(row, 4, format(dto.getUpdatedAt()));
			createCell(row, 5, dto.getScore());
			createCell(row, 6, dto.getNote());
		}

		for (int i = 0; i <= 6; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	/**
	 * セル作成
	 * 
	 * @param row	行インデックス
	 * @param col	列インデックス
	 * @param value	値
	 */
	private void createCell(Row row, int col, Object value) {
		Cell cell = row.createCell(col);

		if (value == null) {
			cell.setCellValue("");
			return;
		}

		if (value instanceof Number n) {
			cell.setCellValue(n.doubleValue());
		} else if (value instanceof Boolean b) {
			cell.setCellValue(b);
		} else {
			cell.setCellValue(value.toString());
		}
	}

	/**
	 * 日付フォーマット
	 * 
	 * @param obj	値
	 */
	private String format(Object obj) {
		if (obj == null) {
			return "";
		}
		if (obj instanceof LocalDateTime ldt) {
			return ldt.format(Const.DT_FMT_YYYY_MM_DD_HH_MM_SS);
		}
		if (obj instanceof LocalDate ld) {
			return ld.format(Const.DT_FMT_YYYY_MM_DD);
		}
		return obj.toString();
	}
}
