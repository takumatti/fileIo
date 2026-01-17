package io.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ホーム画面コントローラー
 */
@Controller
public class HomeController {

	/**
	 * ホーム画面遷移
	 * 
	 * @return	HTML情報
	 */
    @GetMapping("/")
    public String home() {
        return "home/home";
    }

    /**
	 * CSV出力画面遷移
	 * 
	 * @return	HTML情報
	 */
    @GetMapping("/export/csv")
    public String exportCsvPage() {
        return "export-csv";
    }

    /**
	 * Excel出力画面遷移
	 * 
	 * @return	HTML情報
	 */
    @GetMapping("/export/excel")
    public String exportExcelPage() {
        return "export-excel";
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
