package io.controller.excelExportSample;

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

import io.common.ExcelFileMode;
import io.common.ExcelMultiOutputMode;
import io.dto.excelExportSample.ExcelExportSampleDto;
import io.service.excelExportSample.ExcelExportSampleService;

/**
 * Excel出力サンプルコントローラー
 */
@Controller
@RequestMapping("/excelExportSample")
public class ExcelExpotSampleController {

	/**
	 * HTMLテンプレートパス
	 */
	private static final String LIST_HTML_TEMPLATE_FILE_PATH = "excelExportSample/excelExportSample";

	/**
	 * Excel用セッション
	 */
	private static final String SESSION_EXCEL_SAMPLE_LIST = "SESSION_EXCEL_SAMPLE_LIST";
	
	/**
	 * 出力単位用セッション
	 */
	private static final String SESSION_FILE_MODE = "SESSION_FILE_MODE";

	/**
	 * 複数時の出力方法用のセッション
	 */
	private static final String SESSION_MULTI_MODE = "SESSION_MULTI_MODE";

	/**
	 *  Excel出力サンプルサービス
	 */
	@Autowired
	private ExcelExportSampleService excelExportSampleService;

	/**
	 * 初期表示
	 * 
	 * @param 	model	モデル情報
	 * @return	HTML情報
	 */
	@GetMapping()
	public String view(Model model) {

		List<ExcelExportSampleDto> list = excelExportSampleService.createDummyList();
		model.addAttribute("list", list);
		model.addAttribute("autoDownload", false);

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * 画面の値更新
	 * 
	 * @param fileMode		ファイル出力単位
	 * @param multiMode		出力方法
	 * @param session		セッション情報
	 * @param model			リダイレクト情報
	 * @return				HTML情報
	 */
	@PostMapping("/update")
	public String update(@RequestParam ExcelFileMode fileMode, @RequestParam ExcelMultiOutputMode multiMode,
			HttpSession session, RedirectAttributes ra) {

		List<ExcelExportSampleDto> list = excelExportSampleService.createAndUpdate();
		
		session.setAttribute(SESSION_FILE_MODE, fileMode);
		session.setAttribute(SESSION_MULTI_MODE, multiMode);

		session.setAttribute(SESSION_EXCEL_SAMPLE_LIST, list);

		// リロードでExcelが再DLされるのを防止
		ra.addFlashAttribute("list", list);
		ra.addFlashAttribute("fileMode", fileMode);
		ra.addFlashAttribute("multiMode", multiMode);
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
		List<ExcelExportSampleDto> list = (List<ExcelExportSampleDto>) session.getAttribute(SESSION_EXCEL_SAMPLE_LIST);
		ExcelFileMode fileMode = (ExcelFileMode) session.getAttribute(SESSION_FILE_MODE);
		ExcelMultiOutputMode multiMode = (ExcelMultiOutputMode) session.getAttribute(SESSION_MULTI_MODE);

		model.addAttribute("list", list);
		model.addAttribute("fileMode", fileMode);
		model.addAttribute("multiMode", multiMode);

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * Excel出力
	 * 
	 * @param fileMode		ファイル出力単位
	 * @param multiMode		出力方法
	 * @param session		セッション情報
	 * @param response		レスポンス情報
	 * @throws Exception	例外情報
	 */
	@PostMapping("/download")
	public void downloadExcel(@RequestParam ExcelFileMode fileMode, @RequestParam ExcelMultiOutputMode multiMode,
			HttpSession session, HttpServletResponse response) throws Exception {

		@SuppressWarnings("unchecked")
		List<ExcelExportSampleDto> list = (List<ExcelExportSampleDto>) session.getAttribute(SESSION_EXCEL_SAMPLE_LIST);

		if (list == null || list.isEmpty()) {
			throw new IllegalStateException("Excelデータが存在しません");
		}
		
		if (fileMode == ExcelFileMode.SINGLE) {
			excelExportSampleService.exportSingleExcel(list, response);
			return;
		}

		// MULTI
		if (multiMode == ExcelMultiOutputMode.MULTI_SHEET) {
			excelExportSampleService.exportMultiSheetExcel(list, response);
		} else {
			excelExportSampleService.exportMultiExcelZip(list, response);
		}
	}

}
