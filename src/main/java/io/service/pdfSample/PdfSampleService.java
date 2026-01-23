package io.service.pdfSample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

/**
 * PDF出力サンプルサービス
 */
@Service
public class PdfSampleService {

	/**
	 * テンプレートエンジン
	 */
	@Autowired
	private SpringTemplateEngine templateEngine;

	/**
	 * PDF作成
	 * 
	 * @param data			PDF出力情報
	 * @return				PDF情報
	 * @throws Exception	例外情報
	 */
	public byte[] createPdf(Map<String, Object> data) throws Exception {

		// 固定データ
		Context context = new Context();
		context.setVariable("data", data);

		// HTML生成
		String html = templateEngine.process("pdfSample/pdfSample", context);

		// PDF生成
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {

			PdfRendererBuilder builder = new PdfRendererBuilder();

			builder.useFont(() -> {
				try {
					return new ClassPathResource("fonts/ipaexg.ttf").getInputStream();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}, "IPAexGothic");

			builder.withHtmlContent(html, null);
			builder.toStream(stream);
			builder.run();

			return stream.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
