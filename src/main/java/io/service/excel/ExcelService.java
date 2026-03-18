package io.service.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.service.dbView.DbViewService;

/**
 * Excel処理サービス
 */
@Service
public class ExcelService {

	/**
	 * DBViewサービス
	 */
	@Autowired
	private DbViewService dbViewService;

	/**
	 * Excel出力
	 * 
	 * @param schemaName	スキーマ名
	 * @param tableName		テーブル名
	 * @param response		レスポンス情報
	 * @throws IOException	例外情報
	 */
	public void exportExcel(String schemaName, String tableName,
			HttpServletResponse response) throws Exception {

		byte[] excel = createExcel(schemaName, tableName);

		response.setContentType(
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"" + tableName + ".xlsx\"");

		response.getOutputStream().write(excel);
	}

	/**
	 * Excelデータ作成
	 * 
	 * @param schemaName	スキーマ名
	 * @param tableName		テーブル名
	 * @return				Excel出力データ
	 * @throws Exception	例外情報
	 */
	public byte[] createExcel(String schemaName, String tableName) throws Exception {

		List<Map<String, Object>> tabledataList = dbViewService.getTableData(schemaName, tableName);

		try (Workbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet(tableName);

			// ヘッダ行
			Row header = sheet.createRow(0);
			List<String> columns = new ArrayList<>(tabledataList.get(0).keySet());

			for (int i = 0; i < columns.size(); i++) {
				header.createCell(i).setCellValue(columns.get(i));
			}

			// データ行
			for (int r = 0; r < tabledataList.size(); r++) {
				Row row = sheet.createRow(r + 1);
				Map<String, Object> data = tabledataList.get(r);

				for (int c = 0; c < columns.size(); c++) {
					Object val = data.get(columns.get(c));
					row.createCell(c)
							.setCellValue(val == null ? "" : val.toString());
				}
			}

			workbook.write(out);
			return out.toByteArray();
		}
	}
}
