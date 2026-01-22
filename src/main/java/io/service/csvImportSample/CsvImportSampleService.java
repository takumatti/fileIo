package io.service.csvImportSample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.dto.csvImportSample.CsvRowDto;

/**
 * CSV取込サンプルサービス
 */
@Service
public class CsvImportSampleService {

	/**
	 * 取込処理
	 * 
	 * @param csvFile		取込ファイル
	 * @throws IOException	例外情報
	 */
	public List<CsvRowDto> importCsv(MultipartFile csvFile) throws IOException {

		List<CsvRowDto> result = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8))) {

			String line;
			while ((line = br.readLine()) != null) {

				// カンマ区切り（空欄保持）
				String[] values = line.split(",", -1);

				List<String> row = Arrays.stream(values)
						.map(this::normalize)
						.collect(Collectors.toList());

				result.add(new CsvRowDto(row));
			}

		} catch (IOException e) {
			throw new RuntimeException("CSV読込に失敗しました。", e);
		}

		return result;
	}

	/**
	 * 正規表現処理
	 * 
	 * @param value		値
	 * @return			正規表現後の文字列
	 */
	private String normalize(String value) {

		if (value == null) {
			return "";
		}

		// 前後空白除去
		String v = value.trim();

		// ダブルクォート除去
		if (v.startsWith("\"") && v.endsWith("\"")) {
			v = v.substring(1, v.length() - 1);
		}

		return v;
	}

}
