package io.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/export/csv")
    public String exportCsvPage() {
        return "export-csv";
    }

    @GetMapping("/export/excel")
    public String exportExcelPage() {
        return "export-excel";
    }

    @GetMapping("/import/csv")
    public String importCsvPage() {
        return "import-csv";
    }

    @GetMapping("/import/excel")
    public String importExcelPage() {
        return "import-excel";
    }
}
