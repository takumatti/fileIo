package io.controller.csvExportSample;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.common.CsvFileMode;
import io.common.CsvMultiOutputMode;
import io.common.QuoteMode;
import io.dto.csvExportSample.CsvSampleDto;
import io.service.csvExportSample.CsvExportSampleService;

/**
 * CSV出力サンプルコントローラー
 */
@Controller
@RequestMapping("/csvExportSample")
public class CsvExportSampleController {

	/**
	 * HTMLテンプレートパス
	 */
	private static final String LIST_HTML_TEMPLATE_FILE_PATH = "csvExportSample/csvExportSample";

	/**
	 * CSV用セッション
	 */
	private static final String SESSION_CSV_SAMPLE_LIST = "SESSION_CSV_SAMPLE_LIST";

	/**
	 * 文字コード用セッション
	 */
	private static final String SESSION_ENCODING = "SESSION_ENCODING";

	/**
	 * ダブルクォート用セッション
	 */
	private static final String SESSION_QUOTE_MODE = "SESSION_QUOTE_MODE";

	/**
	 * 出力単位用セッション
	 */
	private static final String SESSION_FILE_MODE = "SESSION_FILE_MODE";

	/**
	 * 複数時の出力方法用のセッション
	 */
	private static final String SESSION_MULTI_MODE = "SESSION_MULTI_MODE";

	/**
	 *  CSV出力サンプルサービス
	 */
	@Autowired
	private CsvExportSampleService csvSampleService;

	/**
	 * 初期表示
	 * 
	 * @param 	model	モデル情報
	 * @return	HTML情報
	 */
	@GetMapping()
	public String view(Model model) {

		List<CsvSampleDto> list = csvSampleService.createDummyList();
		model.addAttribute("list", list);
		model.addAttribute("encoding", "UTF-8");
	    model.addAttribute("quoteMode", QuoteMode.ALL);
	    model.addAttribute("fileMode", CsvFileMode.SINGLE);
	    model.addAttribute("multiMode", CsvMultiOutputMode.ZIP);
		model.addAttribute("autoDownload", false);

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * 画面の値更新
	 * 
	 * @param encoding		エンコードタイプ
	 * @param quoteMode		クォーテーションの有無
	 * @param fileMode		ファイル出力単位
	 * @param multiMode		出力方法
	 * @param session		セッション情報
	 * @param model			リダイレクト情報
	 * @return				HTML情報
	 */
	@PostMapping("/update")
	public String update(@RequestParam String encoding, @RequestParam QuoteMode quoteMode,
			@RequestParam CsvFileMode fileMode, @RequestParam(required = false) CsvMultiOutputMode multiMode,
			HttpSession session, RedirectAttributes ra) {

		List<CsvSampleDto> list = csvSampleService.createAndUpdate();

		session.setAttribute(SESSION_CSV_SAMPLE_LIST, list);
		session.setAttribute(SESSION_ENCODING, encoding);
		session.setAttribute(SESSION_QUOTE_MODE, quoteMode);
		session.setAttribute(SESSION_FILE_MODE, fileMode);
		session.setAttribute(SESSION_MULTI_MODE, multiMode);

		// リロードでCSVが再DLされるのを防止
		ra.addFlashAttribute("list", list);
		ra.addFlashAttribute("encoding", encoding);
		ra.addFlashAttribute("quoteMode", quoteMode);
		ra.addFlashAttribute("fileMode", fileMode);
		ra.addFlashAttribute("multiMode", multiMode);
		ra.addFlashAttribute("autoDownload", true);

		return "redirect:/csvExportSample/result";
	}

	/**
	 * 更新後のリストをセットする
	 * 
	 * @param session	セッション情報
	 * @param model		モデル情報
	 * @return			HTML情報
	 */
	@GetMapping("/result")
	public String result(HttpSession session, Model model) {

		@SuppressWarnings("unchecked")
		List<CsvSampleDto> list = (List<CsvSampleDto>) session.getAttribute(SESSION_CSV_SAMPLE_LIST);
		String encoding = (String) session.getAttribute(SESSION_ENCODING);
		QuoteMode quoteMode = (QuoteMode) session.getAttribute(SESSION_QUOTE_MODE);
		CsvFileMode fileMode = (CsvFileMode) session.getAttribute(SESSION_FILE_MODE);
		CsvMultiOutputMode multiMode = (CsvMultiOutputMode) session.getAttribute(SESSION_MULTI_MODE);

		model.addAttribute("list", list);
		model.addAttribute("encoding", encoding);
		model.addAttribute("quoteMode", quoteMode);
		model.addAttribute("fileMode", fileMode);
		model.addAttribute("multiMode", multiMode);

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * CSV出力
	 * ブラウザがレスポンスを受け取った瞬間にCSV出力
	 * 
	 * @param encoding		エンコードタイプ
	 * @param quoteMode		クォーテーションの有無
	 * @param fileMode		ファイル出力単位
	 * @param multiMode		出力方法
	 * @param session		セッション情報
	 * @param response		レスポンス情報
	 * @throws Exception	例外情報
	 */
	@PostMapping("/download")
	public void download(@RequestParam String encoding, @RequestParam QuoteMode quoteMode,
			@RequestParam CsvFileMode fileMode, @RequestParam CsvMultiOutputMode multiMode,
			HttpSession session, HttpServletResponse response) throws Exception {

		@SuppressWarnings("unchecked")
		List<CsvSampleDto> list = (List<CsvSampleDto>) session.getAttribute(SESSION_CSV_SAMPLE_LIST);

		if (list == null || list.isEmpty()) {
			throw new IllegalStateException("CSVデータが存在しません");
		}

		switch (fileMode) {
		case SINGLE -> {
			csvSampleService.exportSingleCsv(list, encoding, quoteMode, response);
		}
		case MULTI -> {
			if (multiMode == null) {
				throw new IllegalArgumentException("複数出力方式が未指定です");
			}
			switch (multiMode) {
			case INDIVIDUAL -> csvSampleService.exportMultiCsvIndividual(list, encoding, quoteMode, response);
			case ZIP -> csvSampleService.exportMultiCsvZip(list, encoding, quoteMode, response);
			}
		}
		}
	}
}
