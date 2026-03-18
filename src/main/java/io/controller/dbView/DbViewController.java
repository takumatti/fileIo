package io.controller.dbView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.dto.dbView.ColumnInfo;
import io.service.csv.CsvService;
import io.service.dbView.DbViewService;
import io.service.excel.ExcelService;

/**
 * DBViewコントローラー
 */
@Controller
@RequestMapping("/dbView")
public class DbViewController {

	/**
	 * HTMLテンプレートパス
	 */
	private static final String LIST_HTML_TEMPLATE_FILE_PATH = "dbView/dbView";

	/**
	 * DBViewサービス
	 */
	@Autowired
	private DbViewService dbViewService;
	
	/**
	 * CSVサービス
	 */
	@Autowired
	private CsvService csvService;
	
	/**
	 * Excelサービス
	 */
	@Autowired
	private ExcelService excelService;

	/**
	 * DBビュー初期表示
	 * 
	 * @param	model	モデル情報
	 * @return			HTML情報
	 */
	@RequestMapping()
	public String view(Model model) {
		model.addAttribute("schemaList", dbViewService.getSchemas());
		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * テーブル名取得
	 * 
	 * @param schemaName	スキーマ名
	 * @return				テーブル名情報リスト
	 */
	@GetMapping("/tables")
	@ResponseBody
	public List<String> tables(@RequestParam String schemaName) {
		return dbViewService.getTables(schemaName);
	}

	/**
	 * 検索（テーブル明細取得）
	 * 
	 * @param schemaName	スキーマ名
	 * @param tableName		テーブル名
	 * @param model			モデル情報
	 * @return				HTML情報
	 */
	@PostMapping("/search")
	public String search(@RequestParam String schemaName, @RequestParam String tableName, Model model) {

		List<Map<String, Object>> tabledataList = dbViewService.getTableData(schemaName, tableName);
		List<ColumnInfo> columnInfoList = dbViewService.getColumnInfos(schemaName, tableName);

		model.addAttribute("schemaList", dbViewService.getSchemas());
		model.addAttribute("tableList", dbViewService.getTables(schemaName));
		model.addAttribute("schemaName", schemaName);
		model.addAttribute("tableName", tableName);
		model.addAttribute("dataList", tabledataList);
		model.addAttribute("columnInfoList", columnInfoList);

		return LIST_HTML_TEMPLATE_FILE_PATH;
	}

	/**
	 * CSV出力
	 * 
	 * @param schemaName	スキーマ名
	 * @param tableName		テーブル名
	 * @param encoding		エンコードタイプ
	 * @param response		レスポンス情報
	 * @throws IOException	例外情報
	 */
	@PostMapping("/csvOutput")
	public void exportCsv(@RequestParam String schemaName, @RequestParam String tableName,
			@RequestParam(defaultValue = "UTF-8") String encoding, 
			HttpServletResponse response) throws IOException {

		csvService.exportCsv(schemaName, tableName, encoding, response);
	}
	
	/**
	 * Excel出力
	 * 
	 * @param schemaName	スキーマ名
	 * @param tableName		テーブル名
	 * @param encoding		エンコードタイプ
	 * @param response		レスポンス情報
	 * @throws IOException	例外情報
	 */
	@PostMapping("/excelOutput")
	public void excelOutput(@RequestParam String schemaName,
	        @RequestParam String tableName,
	        HttpServletResponse response) throws Exception {

		excelService.exportExcel(schemaName, tableName, response);
	}

}