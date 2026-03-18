package io.controller.invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import io.service.invoice.InvoicePdfService;

/**
 * 請求書出力コントローラー
 */
@Controller
@RequestMapping("/invoice")
public class InvoiceController {

	/**
	 * HTMLテンプレートパス
	 */
	private static final String LIST_HTML_TEMPLATE_FILE_PATH = "invoice/invoiceView";

	/**
	 * 請求書出力サービス
	 */
	@Autowired
	private InvoicePdfService pdfService;

	/**
	 * テンプレートエンジン
	 */
	@Autowired
	private SpringTemplateEngine templateEngine;

	/** 
	 * 初期表示
	 * 
	 * @param model		モデル情報
	 * @return			HTML情報
	 */
	@GetMapping
	public String view(Model model) {
		model.addAttribute("invoice", pdfService.createDummyInvoice());
		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * PDF出力
	 * 
	 * @return		PDF情報
	 */
	@PostMapping("/output")
	public ResponseEntity<byte[]> outputPdf() {

		Context context = new Context();
		context.setVariable("invoice", pdfService.createDummyInvoice());

		String html = templateEngine.process(
				"invoice/invoicePdf",
				context);

		byte[] pdf = pdfService.createPdf(html);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdf);
	}
}
