package io.mapper.dbView;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.dto.dbView.ColumnInfo;

@Mapper
public interface DbViewMapper {

	/**
	 * スキーマ名取得
	 * 
	 * @return	スキーマ名情報リスト
	 */
    List<String> selectSchemas();

    /**
	 * テーブル名取得
	 * 
	 * @param schemaName	スキーマ名
	 * @return				テーブル名情報リスト
	 */
    List<String> selectTables(@Param("schemaName") String schemaName);

    /**
	 * スキーマ名、テーブル名に紐づく情報を取得
	 * 
	 * @param schemaName	スキーマ名取得
	 * @param tableName		テーブル名取得
	 * @return				テーブルの明細情報リスト
	 */
    List<Map<String, Object>> selectTableData(
            @Param("schemaName") String schemaName,
            @Param("tableName") String tableName
    );
    
    /**
     * カラム情報取得
     * 
     * @param schemaName	スキーマ名取得
     * @param tableName		テーブル名取得
     * @return				カラム名と型の情報リスト
     */
    List<ColumnInfo> selectColumnInfos(
            @Param("schemaName") String schemaName,
            @Param("tableName") String tableName);
}
