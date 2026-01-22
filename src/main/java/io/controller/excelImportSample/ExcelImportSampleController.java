package io.controller.excelImportSample;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import io.dto.excelImportSample.ExcelImportForm;
import io.dto.excelImportSample.ExcelRowDto;
import io.service.excelImportSample.ExcelImportSampleService;

/**
 * Excel取込サンプルコントローラー
 */
@Controller
@RequestMapping("/excelImportSample")
public class ExcelImportSampleController {
	
	/**
	 * HTMLテンプレートパス
	 */
	private static final String LIST_HTML_TEMPLATE_FILE_PATH = "excelImportSample/excelImportSample";

	/**
	 * Excel取込サンプルサービス
	 */
	@Autowired
    private ExcelImportSampleService excelImportSampleService;
	

    /**
	 * Excel取込画面遷移
	 * 
	 * @return	HTML情報
	 */
    @GetMapping()
    public String view(Model model) {
    	model.addAttribute("excelImportForm", new ExcelImportForm());
        return LIST_HTML_TEMPLATE_FILE_PATH;
    }

    /**
     * Excelファイル取込処理
     * 
     * @param form		フォーム情報
     * @param model		モデル情報
     * @return			HTML情報
     */
    @PostMapping("/upload")
    public String upload( @ModelAttribute ExcelImportForm form, Model model) {
    	
    	MultipartFile file = form.getExcelFile();
    	
        // ファイル未選択
        if (file == null || file.isEmpty()) {
            model.addAttribute("message", "Excelファイルを選択してください。");
            return LIST_HTML_TEMPLATE_FILE_PATH;
        }

        // 拡張子チェック
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".xlsx")) {
            model.addAttribute("message", ".xlsx形式のExcelのみ対応しています。");
            return "excelImport";
        }

        try {
        	List<ExcelRowDto> list = excelImportSampleService.importExcel(file);
        	model.addAttribute("excelList", list);
            model.addAttribute("message", "Excel取込が完了しました。");
        } catch (Exception e) {
            model.addAttribute("message", "取込中にエラーが発生しました。");
            e.printStackTrace();
        }

        return LIST_HTML_TEMPLATE_FILE_PATH;
    }
}
