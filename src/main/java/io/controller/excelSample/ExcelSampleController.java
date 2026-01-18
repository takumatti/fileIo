package io.controller.excelSample;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.dto.excelSample.ExcelSampleDto;
import io.service.excelSample.ExcelSampleService;

/**
 * Excel出力サンプルコントローラー
 */
@Controller
@RequestMapping("/excelExportSample")
public class ExcelSampleController {

	/**
	 * HTMLテンプレートパス
	 */
	private static final String LIST_HTML_TEMPLATE_FILE_PATH = "excelExportSample/excelExportSample";

	/**
	 * Excel用セッション
	 */
	private static final String SESSION_EXCEL_SAMPLE_LIST = "SESSION_EXCEL_SAMPLE_LIST";

	/**
	 *  Excel出力サンプルサービス
	 */
	@Autowired
	private ExcelSampleService excelSampleService;

	/**
	 * 初期表示
	 * 
	 * @param 	model	モデル情報
	 * @return	HTML情報
	 */
	@GetMapping()
	public String view(Model model) {

		List<ExcelSampleDto> list = excelSampleService.createDummyList();
		model.addAttribute("list", list);
		model.addAttribute("autoDownload", false);

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * 画面の値更新
	 * 
	 * @param session		セッション情報
	 * @param model			リダイレクト情報
	 * @return				HTML情報
	 */
	@PostMapping("/update")
	public String update(HttpSession session, RedirectAttributes ra) {

		List<ExcelSampleDto> list = excelSampleService.createAndUpdate();

		session.setAttribute(SESSION_EXCEL_SAMPLE_LIST, list);

		// リロードでExcelが再DLされるのを防止
		ra.addFlashAttribute("list", list);
		ra.addFlashAttribute("autoDownload", true);

		return "redirect:/excelExportSample/result";
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
		List<ExcelSampleDto> list = (List<ExcelSampleDto>) session.getAttribute(SESSION_EXCEL_SAMPLE_LIST);

		model.addAttribute("list", list);

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * Excel出力
	 * 
	 * @param session		セッション情報
	 * @param response		レスポンス情報
	 * @throws Exception	例外情報
	 */
	@PostMapping("/download")
	public void downloadExcel(HttpSession session, HttpServletResponse response) throws Exception {

		@SuppressWarnings("unchecked")
		List<ExcelSampleDto> list = (List<ExcelSampleDto>) session.getAttribute(SESSION_EXCEL_SAMPLE_LIST);

		if (list == null || list.isEmpty()) {
			throw new IllegalStateException("Excelデータが存在しません");
		}

		excelSampleService.exportExcel(list, response);
	}

}
