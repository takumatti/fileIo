package io.controller.pdfSample;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.service.pdfSample.PdfSampleService;

/**
 * PDF出力サンプルコントローラー
 */
@Controller
@RequestMapping("/pdfSample")
public class PdfSampleController {

	/**
	 * HTMLテンプレートパス
	 */
	private static final String LIST_HTML_TEMPLATE_FILE_PATH = "pdfSample/pdfSampleView";

	/**
	 * PDF出力サンプルサービス
	 */
	@Autowired
	private PdfSampleService pdfSampleService;

	/**
	 * 初期表示
	 * 
	 * @param model		モデル情報
	 * @return			HTML情報
	 */
	@GetMapping
	public String view(Model model) {

		model.addAttribute("data", sampleData());

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * PDF出力
	 * 
	 * @return				PDF情報
	 * @throws Exception	例外情報
	 */
	@PostMapping("/output")
	public ResponseEntity<byte[]> outputPdf() throws Exception {
		byte[] pdf = pdfSampleService.createPdf(sampleData());

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdf);
	}

	/**
	 * ダミーデータ作成
	 * 
	 * @return	ダミーデータ情報
	 */
	private Map<String, Object> sampleData() {
		return Map.of(
				"id", "001",
				"name", "テスト太郎",
				"status", "有効",
				"createdDate", "2026/01/23");
	}
}
