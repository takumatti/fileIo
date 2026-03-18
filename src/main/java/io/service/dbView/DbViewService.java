package io.service.dbView;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.dto.dbView.ColumnInfo;
import io.mapper.dbView.DbViewMapper;

/**
 * DBViewサービス
 */
@Service
public class DbViewService {

	/**
	 * DBViewMapper
	 */
	@Autowired
	private DbViewMapper dbViewMapper;

	/**
	 * スキーマ名取得
	 * 
	 * @return	スキーマ名情報リスト
	 */
	public List<String> getSchemas() {
		return dbViewMapper.selectSchemas();
	}

	/**
	 * テーブル名取得
	 * 
	 * @param schemaName	スキーマ名
	 * @return				テーブル名情報リスト
	 */
	public List<String> getTables(String schemaName) {
		return dbViewMapper.selectTables(schemaName);
	}

	/**
	 * スキーマ名、テーブル名に紐づく情報を取得
	 * 
	 * @param schemaName	スキーマ名取得
	 * @param tableName		テーブル名取得
	 * @return
	 */
	public List<Map<String, Object>> getTableData(String schemaName, String tableName) {
		// テーブル名の簡易バリデーション（重要）
		if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
			throw new IllegalArgumentException("不正なテーブル名です");
		}
		return dbViewMapper.selectTableData(schemaName, tableName);
	}

	/**
	 * カラムの型を取得
	 * 
	 * @param schemaName	スキーマ名
	 * @param tableName		テーブル名
	 * @return				カラム名と型のマップ
	 */
	public List<ColumnInfo> getColumnInfos(String schemaName, String tableName) {
		 return dbViewMapper.selectColumnInfos(schemaName, tableName);
	}

}