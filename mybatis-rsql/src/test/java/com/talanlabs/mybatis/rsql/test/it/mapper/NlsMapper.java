package com.talanlabs.mybatis.rsql.test.it.mapper;

import com.talanlabs.entity.IId;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface NlsMapper {

    @Select("select NVL((select meaning from t_nls where table_name = #{tableName} and column_name = #{columnName} and language_code = #{languageCode} and table_id = #{id}), (select #{defaultValue} from (VALUES(0)))) from (VALUES(0))")
    String selectNlsColumn(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("languageCode") String languageCode, @Param("id") IId id,
            @Param("defaultValue") String defaultValue);

    @Update("MERGE INTO t_nls B\n"
            + "USING (VALUES #{tableName}, #{columnName}, #{languageCode}, #{id}, #{meaning,javaType=java.lang.String}) I (TABLE_NAME, COLUMN_NAME, LANGUAGE_CODE, TABLE_ID, MEANING)\n"
            + "ON (B.TABLE_NAME = I.TABLE_NAME and B.COLUMN_NAME = I.COLUMN_NAME and B.LANGUAGE_CODE = I.LANGUAGE_CODE and B.TABLE_ID = I.TABLE_ID)\n"
            + "WHEN MATCHED THEN UPDATE SET B.MEANING = I.MEANING\n"
            + "WHEN NOT MATCHED THEN INSERT (TABLE_NAME, COLUMN_NAME, LANGUAGE_CODE, TABLE_ID, MEANING) VALUES (I.TABLE_NAME, I.COLUMN_NAME, I.LANGUAGE_CODE, I.TABLE_ID, I.MEANING)")
    int mergeNlsColumn(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("languageCode") String languageCode, @Param("id") IId id, @Param("meaning") String meaning);

    @Delete("delete from t_nls\n" + "        WHERE TABLE_NAME = #{tableName} and TABLE_ID = #{id}")
    int deleteNlsColumns(@Param("tableName") String tableName, @Param("id") IId id);
}
