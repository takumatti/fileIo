package io.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ホーム画面コントローラー
 */
@Controller
public class HomeController {
	
	/**
	 * HTMLテンプレートパス
	 */
	private static final String LIST_HTML_TEMPLATE_FILE_PATH = "home/home";

	/**
	 * ホーム画面遷移
	 * 
	 * @return	HTML情報
	 */
    @GetMapping("/")
    public String home() {
        return LIST_HTML_TEMPLATE_FILE_PATH;
    }

    /**
	 * CSV取込画面遷移
	 * 
	 * @return	HTML情報
	 */
    @GetMapping("/import/csv")
    public String importCsvPage() {
        return "import-csv";
    }

    /**
	 * Excel取込画面遷移
	 * 
	 * @return	HTML情報
	 */
    @GetMapping("/import/excel")
    public String importExcelPage() {
        return "import-excel";
    }
}
