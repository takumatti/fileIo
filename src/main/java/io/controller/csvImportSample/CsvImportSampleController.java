package io.controller.csvImportSample;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import io.dto.csvImportSample.CsvImportForm;
import io.dto.csvImportSample.CsvRowDto;
import io.service.csvImportSample.CsvImportSampleService;

/**
 * CSV取込サンプルコントローラー
 */
@Controller
@RequestMapping("/csvImportSample")
public class CsvImportSampleController {
	
	/**
	 * HTMLテンプレートパス
	 */
	private static final String LIST_HTML_TEMPLATE_FILE_PATH = "csvImportSample/csvImportSample";

	/**
	 * CSV取込サンプルサービス
	 */
	@Autowired
    private CsvImportSampleService csvImportSampleService;
	

    /**
	 * CSV取込画面遷移
	 * 
	 * @return	HTML情報
	 */
    @GetMapping()
    public String view(Model model) {
    	model.addAttribute("csvImportForm", new CsvImportForm());
        return LIST_HTML_TEMPLATE_FILE_PATH;
    }

    /**
     * CSVファイル取込処理
     * 
     * @param form		フォーム情報
     * @param model		モデル情報
     * @return			HTML情報
     */
    @PostMapping("/upload")
    public String upload( @ModelAttribute CsvImportForm form, Model model) {
    	
    	MultipartFile file = form.getCsvFile();
    	
        // ファイル未選択
        if (file == null || file.isEmpty()) {
            model.addAttribute("message", "CSVファイルを選択してください。");
            return LIST_HTML_TEMPLATE_FILE_PATH;
        }

        // 拡張子チェック
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".csv")) {
            model.addAttribute("message", ".CSVのみ対応しています。");
            return "excelImport";
        }

        try {
        	List<CsvRowDto> list = csvImportSampleService.importCsv(file);
        	model.addAttribute("csvList", list);
            model.addAttribute("message", "CSV取込が完了しました。");
        } catch (Exception e) {
            model.addAttribute("message", "取込中にエラーが発生しました。");
            e.printStackTrace();
        }

        return LIST_HTML_TEMPLATE_FILE_PATH;
    }
}
