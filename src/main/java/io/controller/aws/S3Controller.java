package io.controller.aws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.common.S3FileManager;
import io.common.Const;
import io.dto.csv.CsvExportDto;
import io.service.csv.CsvExportService;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Controller
@RequestMapping("/s3")
public class S3Controller {

	/**
	 * HTMLテンプレートパス
	 */
	private static final String LIST_HTML_TEMPLATE_FILE_PATH = "aws/s3";

	/**
	 * CSV用セッション
	 */
	private static final String SESSION_CSV_SAMPLE_LIST = "SESSION_CSV_SAMPLE_LIST";

	/**
	 *  CSV出力サンプルサービス
	 */
	@Autowired
	private CsvExportService csvSampleService;

	/**
	 * ATM取引明細S3ファイル管理クラス
	 */
	@Autowired
	private S3FileManager s3FileManager;

	/**
	 * 初期表示
	 * 
	 * @param session 	セッション
	 * @param model   	モデル
	 * @return 			HTMLテンプレートファイルパス
	 */
	@GetMapping()
	public String view(HttpSession session, Model model) {
		List<CsvExportDto> list = csvSampleService.createDummyList();
		session.setAttribute(SESSION_CSV_SAMPLE_LIST, list);
		model.addAttribute("list", list);

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * CSV出力（ローカル / S3）
	 * 
	 * @param csvOutputType 出力先種別（ローカル / S3）
	 * @param session       セッション
	 * @param response      レスポンス
	 */
	@PostMapping("/export")
	public void exportCsv(@RequestParam String csvOutputType, HttpSession session, HttpServletResponse response) {

		PrintWriter pw = null;
		OutputStreamWriter osw = null;
		ByteArrayOutputStream baos = null;

		try {
			// セッションから出力対象データ取得
			@SuppressWarnings("unchecked")
			List<CsvExportDto> list = (List<CsvExportDto>) session.getAttribute(SESSION_CSV_SAMPLE_LIST);

			if (list == null || list.isEmpty()) {
				throw new IllegalStateException("CSV出力対象データが存在しません");
			}

			// CSV生成
			baos = new ByteArrayOutputStream();
			// UTF-8 BOM を付与
			baos.write(0xEF);
			baos.write(0xBB);
			baos.write(0xBF);
			osw = new OutputStreamWriter(baos, Const.UTF_8);
			pw = new PrintWriter(osw);

			// ヘッダ
			setTitle(pw);

			// データ
			setData(pw, list);

			pw.flush();
			byte[] csvBytes = baos.toByteArray();

			String prefix = Const.AWS_S3.equals(csvOutputType) ? Const.EXPORT : Const.LOCAL;
			String fileName = createCsvFileName(prefix);

			// 出力先分岐
			if (Const.AWS_S3.equals(csvOutputType)) {

				s3FileManager.uploadCsv(fileName, csvBytes);
				response.setStatus(HttpServletResponse.SC_OK);
				return;

			} else {

				response.setContentType("text/csv; charset=UTF-8");
				response.setHeader(
						"Content-Disposition",
						"attachment; filename=\"" + fileName + "\"");
				response.getOutputStream().write(csvBytes);
			}

		} catch (Exception e) {

			log.error("CSV export failed", e);

			String message;

			// AWS S3関連例外処理
			if (e instanceof S3Exception) {
				int status = ((S3Exception) e).statusCode();

				// ステータスコードに応じたメッセージ設定
				if (status == 401 || status == 403 || status == 404 || status == 301) {
					message = "アップロードに失敗しました。システム管理者に連絡してください。";
				} else {
					// その他のS3例外
					message = "システム障害が発生しました。";
				}

			} else if (e instanceof SdkClientException) {
				// ネットワークエラー等のクライアント例外
				message = "アップロードに失敗しました。時間をおいて再度実行してください。";

			} else {
				// その他の例外
				message = "システム障害が発生しました。";
			}

			try {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter()
						.write("{\"message\":\"" + message + "\"}");
			} catch (IOException ioEx) {
				log.error("response write failed", ioEx);
			}

		} finally {
			if (pw != null)
				pw.close();
			try {
				if (osw != null)
					osw.close();
			} catch (IOException ignore) {
			}
			try {
				if (baos != null)
					baos.close();
			} catch (IOException ignore) {
			}
		}
	}

	/**
	 * CSVタイトル行設定
	 * 
	 * @param pw PrintWriter
	 */
	private void setTitle(PrintWriter pw) {
		pw.println("ID,名前,有効,生年月日,作成日時,スコア,備考");
	}

	/**
	 * CSVデータ行設定
	 * 
	 * @param pw   PrintWriter
	 * @param list データリスト
	 */
	private void setData(PrintWriter pw, List<CsvExportDto> list) {
		for (CsvExportDto dto : list) {
			pw.printf("%d,%s,%s,%s,%s,%f,%s%n",
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
	 * CSVファイル名作成
	 * 
	 * @param prefix プレフィックス
	 * @return ファイル名
	 */
	private String createCsvFileName(String prefix) {
		DateTimeFormatter formatter = Const.DT_FMT_YYYYMMDD_HHMMSS;
		String timestamp = LocalDateTime.now().format(formatter);
		return prefix + "_" + timestamp + ".csv";
	}

}
