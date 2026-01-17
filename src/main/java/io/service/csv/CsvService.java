package io.service.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.common.Const;
import io.service.dbView.DbViewService;

/**
 * CSV処理サービス
 */
@Service
public class CsvService {

	/**
	 * DBViewサービス
	 */
	@Autowired
	private DbViewService dbViewService;

	/**
	 * CSV出力
	 * 
	 * @param schemaName	スキーマ名
	 * @param tableName		テーブル名
	 * @param response		レスポンス情報
	 * @throws IOException	例外情報
	 */
	public void exportCsv(String schemaName, String tableName, String encoding,
			HttpServletResponse response) throws IOException {

		List<Map<String, Object>> tabledataList = dbViewService.getTableData(schemaName, tableName);

		response.setContentType("text/csv");
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"" + tableName + ".csv\"");

		OutputStream out = response.getOutputStream();
		BufferedWriter writer;

		if (Const.SHIFT_JIS.equalsIgnoreCase(encoding)) {
			writer = new BufferedWriter(
					new OutputStreamWriter(out, Charset.forName(Const.SHIFT_JIS)));
		} else {
			// UTF-8 BOM（Excel対策）
			out.write(0xEF);
			out.write(0xBB);
			out.write(0xBF);
			writer = new BufferedWriter(
					new OutputStreamWriter(out, StandardCharsets.UTF_8));
		}

		writeCsv(tabledataList, writer);
	}

	/**
	 * CSV出力処理
	 * 
	 * @param tabledataList		データリスト
	 * @param writer			バッファ情報
	 * @throws IOException		例外情報
	 */
	private void writeCsv(List<Map<String, Object>> tabledataList,
			BufferedWriter writer) throws IOException {

		if (tabledataList == null || tabledataList.isEmpty()) {
			writer.flush();
			return;
		}

		List<String> headers = new ArrayList<>(tabledataList.get(0).keySet());
		writer.write(String.join(",", headers));
		writer.newLine();

		for (Map<String, Object> row : tabledataList) {
			List<String> values = new ArrayList<>();
			for (String key : headers) {
				values.add(escape(row.get(key)));
			}
			writer.write(String.join(",", values));
			writer.newLine();
		}

		writer.flush();
	}

	/**
	 * エスケープ処理（ダブルクォーテーション、カンマ対策）
	 * 
	 * @param value
	 * @return
	 */
	private String escape(Object val) {
		if (val == null)
			return "";
		String s = val.toString();
		if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
			s = s.replace("\"", "\"\"");
			return "\"" + s + "\"";
		}
		return s;
	}
}
