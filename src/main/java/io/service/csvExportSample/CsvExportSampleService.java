package io.service.csvExportSample;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import io.common.Const;
import io.common.QuoteMode;
import io.dto.csvExportSample.CsvSampleDto;

/**
 * CSV出力サンプルサービス
 */
@Service
public class CsvExportSampleService {

	/**
	 * ダミーデータ生成
	 * 
	 * @return ダミーデータ情報リスト
	 */
	public List<CsvSampleDto> createDummyList() {

		List<CsvSampleDto> list = new ArrayList<>();

		CsvSampleDto dto1 = new CsvSampleDto();
		dto1.setId(1);
		dto1.setName("山田太郎");
		dto1.setActive(true);
		dto1.setBirthDate(LocalDate.of(1995, 5, 20));
		dto1.setUpdatedAt(LocalDateTime.now());
		dto1.setScore(98.5);
		dto1.setNote(null);

		CsvSampleDto dto2 = new CsvSampleDto();
		dto2.setId(2);
		dto2.setName("Suzuki Hanako");
		dto2.setActive(false);
		dto2.setBirthDate(LocalDate.of(2000, 1, 1));
		dto2.setUpdatedAt(LocalDateTime.now().minusDays(1));
		dto2.setScore(75.0);
		dto2.setNote("備考あり");

		CsvSampleDto dto3 = new CsvSampleDto();
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
	public List<CsvSampleDto> createAndUpdate() {

		List<CsvSampleDto> list = createDummyList();
		LocalDateTime now = LocalDateTime.now();

		list.forEach(dto -> dto.setUpdatedAt(now));

		return list;
	}

	/**
	 * CSV出力処理
	 * 
	 * @param list			CSV出力用データリスト
	 * @param encoding		エンコードタイプ
	 * @param quoteMode		クォーテーションの有無
	 * @param response		レスポンス情報
	 * @throws Exception	例外情報
	 */
	public void exportCsv(List<CsvSampleDto> list, String encoding, QuoteMode quoteMode,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/csv");
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"sample.csv\"");

		OutputStream os = response.getOutputStream();

		// UTF-8 の場合のみ BOM を付与
		if (Const.UTF_8.equalsIgnoreCase(encoding)) {
			os.write(0xEF);
			os.write(0xBB);
			os.write(0xBF);
		}

		// try-with-resourcesの終了時に"write.close()が自動で呼ばれる
		//close()の中でflush()も必ず実行される
		try (PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(os, encoding))) {

			// ヘッダ
			writeLine(writer, quoteMode,
					"ID", "名前", "有効", "生年月日", "作成日時", "スコア", "備考");

			// 明細
			for (CsvSampleDto dto : list) {
				writeLine(writer, quoteMode,
						dto.getId(),
						dto.getName(),
						dto.getActive(),
						dto.getBirthDate(),
						dto.getUpdatedAt(),
						dto.getScore(),
						dto.getNote());
			}
		}
	}

	/**
	 * データ書き込み処理
	 * 
	 * @param writer		PrintWriter
	 * @param quoteMode		クォーテーションの有無
	 * @param values		1行分のCSV出力用データ
	 */
	private void writeLine(PrintWriter writer, QuoteMode quoteMode, Object... values) {

		String line = Arrays.stream(values)
				.map(this::toCsvString)
				.map(v -> formatValue(v, quoteMode))
				.collect(Collectors.joining(","));

		writer.println(String.join(",", line));
	}

	/**
	 * CSV用文字列に変換
	 * 
	 * @param obj	変換前値
	 * @return		文字列に変換した値
	 */
	private String toCsvString(Object value) {
		if (value == null) {
			return "";
		}

		if (value instanceof LocalDateTime ldt) {
			return ldt.format(Const.DT_FMT_YYYY_MM_DD_HH_MM_SS);
		}

		if (value instanceof LocalDate ld) {
			return ld.format(Const.DT_FMT_YYYY_MM_DD);
		}

		return value.toString();
	}

	/**
	 * クォート制御
	 * 
	 * @param value			値
	 * @param quoteMode		クォーテーションの有無
	 * @return				文字列
	 */
	private String formatValue(String value, QuoteMode quoteMode) {

		String str = value == null ? "" : value.toString();

		if (quoteMode.isQuote()) {
			// ダブルクォートをエスケープ
			str = str.replace("\"", "\"\"");
			return "\"" + str + "\"";
		}

		return str;
	}

}
