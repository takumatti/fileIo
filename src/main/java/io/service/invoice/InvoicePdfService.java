package io.service.invoice;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

/**
 * 請求書出力サービス
 */
@Service
public class InvoicePdfService {

	/**
	 * PDF作成
	 * @param html	HTML情報
	 * @return		PDF情報
	 */
	public byte[] createPdf(String html) {

		try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
				InputStream fontStream = new ClassPathResource("fonts/ipaexg.ttf").getInputStream()) {

			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.useFont(() -> fontStream, "IPAexGothic");
			builder.withHtmlContent(html, null);
			builder.toStream(stream);
			builder.run();

			return stream.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ダミーデータ作成
	 * 
	 * @return		ダミーデータ情報
	 */
	public Map<String, Object> createDummyInvoice() {
		return Map.of(
				"no", "INV-2026-001",
				"issueDate", "2026/01/23",
				"customerName", "株式会社テスト",
				"companyName", "サンプル株式会社",
				"items", List.of(
						Map.of("name", "商品A", "qty", 2, "price", 5000, "total", 10000),
						Map.of("name", "商品B", "qty", 1, "price", 3000, "total", 3000),
						Map.of("name", "商品C", "qty", 5, "price", 800, "total", 4000)),
				"grandTotal", 17000);
	}
}
