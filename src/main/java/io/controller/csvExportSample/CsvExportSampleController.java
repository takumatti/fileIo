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
		model.addAttribute("autoDownload", false);

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * 画面の値更新
	 * 
	 * @param encoding		エンコードタイプ
	 * @param quoteMode		クォーテーションの有無
	 * @param session		セッション情報
	 * @param model			リダイレクト情報
	 * @return				HTML情報
	 */
	@PostMapping("/update")
	public String update(@RequestParam String encoding, @RequestParam QuoteMode quoteMode,
			HttpSession session, RedirectAttributes ra) {

		List<CsvSampleDto> list = csvSampleService.createAndUpdate();

		session.setAttribute(SESSION_CSV_SAMPLE_LIST, list);

		// リロードでCSVが再DLされるのを防止
		ra.addFlashAttribute("list", list);
		ra.addFlashAttribute("encoding", encoding);
		ra.addFlashAttribute("quoteMode", quoteMode);
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
	public String result(
			HttpSession session,
			Model model) {

		@SuppressWarnings("unchecked")
		List<CsvSampleDto> list = (List<CsvSampleDto>) session.getAttribute(SESSION_CSV_SAMPLE_LIST);

		model.addAttribute("list", list);

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * CSV出力
	 * ブラウザがレスポンスを受け取った瞬間にCSV出力
	 * 
	 * @param encoding		エンコードタイプ
	 * @param quoteMode		クォーテーションの有無
	 * @param session		セッション情報
	 * @param response		レスポンス情報
	 * @throws Exception	例外情報
	 */
	@PostMapping("/download")
	public void download(@RequestParam String encoding, @RequestParam QuoteMode quoteMode,
			HttpSession session, HttpServletResponse response) throws Exception {

		@SuppressWarnings("unchecked")
		List<CsvSampleDto> list = (List<CsvSampleDto>) session.getAttribute(SESSION_CSV_SAMPLE_LIST);

		if (list == null || list.isEmpty()) {
			throw new IllegalStateException("CSVデータが存在しません");
		}

		csvSampleService.exportCsv(list, encoding, quoteMode, response);
	}
}
