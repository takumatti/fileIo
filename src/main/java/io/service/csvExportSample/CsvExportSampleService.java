package io.service.csvExportSample;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
	 * CSV出力処理（単体用）
	 * ヘッダ+明細をCSVに出力する
	 * 
	 * @param list			CSV出力用データリスト
	 * @param encoding		エンコードタイプ
	 * @param quoteMode		クォーテーションの有無
	 * @param response		レスポンス情報
	 * @throws Exception	例外情報
	 */
	public void exportSingleCsv(List<CsvSampleDto> list, String encoding, QuoteMode quoteMode,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/csv");
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"sample.csv\"");

		OutputStream os = response.getOutputStream();

		// UTF-8 BOM
		if (Const.UTF_8.equalsIgnoreCase(encoding)) {
			os.write(0xEF);
			os.write(0xBB);
			os.write(0xBF);
		}

		try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, encoding))) {

			writeCsv(list, writer, quoteMode);
		}
	}

	/**
	 * CSV出力処理（複数、単体）
	 * ※1レスポンス = 1ダウンロードのため非推奨）
	 * 
	 * @param list			CSV出力用データリスト
	 * @param encoding		エンコードタイプ
	 * @param quoteMode		クォーテーションの有無
	 * @param response		レスポンス情報
	 */
	public void exportMultiCsvIndividual(List<CsvSampleDto> list, String encoding, QuoteMode quoteMode,
			HttpServletResponse response) {

		throw new UnsupportedOperationException(
				"HTTPレスポンスでは複数CSVの個別同時ダウンロードはできません。ZIPを使用してください。");
	}

	/**
	 * CSV出力処理（複数、ZIP）
	 * 
	 * @param list			CSV出力用データリスト
	 * @param encoding		エンコードタイプ
	 * @param quoteMode		クォーテーションの有無
	 * @param response		レスポンス情報
	 * @throws Exception	例外処理
	 */
	public void exportMultiCsvZip(List<CsvSampleDto> list, String encoding, QuoteMode quoteMode,
			HttpServletResponse response) throws Exception {

		response.setContentType("application/zip");
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"sample.zip\"");

		ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
		Charset charset = Charset.forName(encoding);

		// 例として3ファイル作成
		for (int i = 0; i < 3; i++) {

			ZipEntry entry = new ZipEntry("sample_" + i + ".csv");
			zipOut.putNextEntry(entry);

			// UTF-8 BOM（ZIP内CSV）
			if (Const.UTF_8.equalsIgnoreCase(encoding)) {
				zipOut.write(0xEF);
				zipOut.write(0xBB);
				zipOut.write(0xBF);
			}

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(zipOut, charset));

			// CSV書き込み
			writeCsv(list, writer, quoteMode);

			writer.flush();
			zipOut.closeEntry();
		}

		zipOut.finish();
		zipOut.flush();
	}

	/**
	 * CSV書き込み処理
	 * 
	 * @param list			CSV出力用データリスト
	 * @param writer		PrintWriter
	 * @param encoding		エンコードタイプ
	 * @throws Exception	例外処理
	 */
	private void writeCsv(List<CsvSampleDto> list, PrintWriter writer, QuoteMode quoteMode) throws Exception {

		writeLine(writer, quoteMode,
				"ID", "名前", "有効", "生年月日", "作成日時", "スコア", "備考");

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

	/**
	 * 1行データ書き込み処理
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
