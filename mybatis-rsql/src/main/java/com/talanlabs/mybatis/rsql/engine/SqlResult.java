package com.talanlabs.mybatis.rsql.engine;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collector;

public class SqlResult {

    public final List<Join> joins;
    public final String sql;
    public final Map<String, Object> parameterMap;

    private SqlResult(List<Join> joins, String sql, Map<String, Object> parameterMap) {
        super();

        this.joins = joins;
        this.sql = sql;
        this.parameterMap = parameterMap;
    }

    public static SqlResult of(List<Join> joins, String sql, Map<String, Object> parameterMap) {
        return new SqlResult(joins, sql, parameterMap);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("joins", joins).append("sql", sql).append("parameterMap", parameterMap).toString();
    }

    public static class Join {

        public final String sql;
        public final Type type;

        private Join(Type type, String sql) {
            super();

            this.type = type;
            this.sql = sql;
        }

        public static Join of(Type type, String sql) {
            return new Join(type, sql);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("type", type).append("sql", sql).toString();
        }

        public enum Type {
            Inner, Outer, LeftOuter, RightOuter
        }
    }

    public static class SqlResultAppender {

        private List<SqlResult> sqlResults = new ArrayList<>();

        public SqlResultAppender add(SqlResult sqlResult) {
            sqlResults.add(sqlResult);
            return this;
        }

        public SqlResult toSqlResult() {
            List<SqlResult.Join> joins = new ArrayList<>();
            Map<String, Object> parameterMap = new HashMap<>();
            StringJoiner sj = new StringJoiner(" AND ", "(", ")");
            sj.setEmptyValue("");
            for (SqlResult sqlResult : sqlResults) {
                if (sqlResult.joins != null) {
                    joins.addAll(sqlResult.joins);
                }
                if (sqlResult.sql != null) {
                    sj.add(sqlResult.sql);
                }
                if (sqlResult.parameterMap != null) {
                    parameterMap.putAll(sqlResult.parameterMap);
                }
            }
            return SqlResult.of(joins, sj.toString(), parameterMap);
        }
    }

    public static class SqlResultJoiner {

        final List<SqlResult.Join> joins;
        final StringJoiner whereSqlJoiner;
        final Map<String, Object> paramMap;

        private SqlResultJoiner(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
            super();

            this.joins = new ArrayList<>();
            this.whereSqlJoiner = new StringJoiner(delimiter, prefix, suffix);
            this.whereSqlJoiner.setEmptyValue("");
            this.paramMap = new HashMap<>();
        }

        public static Collector<SqlResult, SqlResultJoiner, SqlResult> joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
            return Collector.of(() -> new SqlResultJoiner(delimiter, prefix, suffix), SqlResultJoiner::accumulater, SqlResultJoiner::combiner, SqlResultJoiner::finisher);
        }

        private static void accumulater(SqlResultJoiner a, SqlResult t) {
            a.joins.addAll(t.joins);
            a.whereSqlJoiner.add(t.sql);
            a.paramMap.putAll(t.parameterMap);
        }

        private static SqlResultJoiner combiner(SqlResultJoiner a1, SqlResultJoiner a2) {
            a1.joins.addAll(a2.joins);
            a1.whereSqlJoiner.merge(a2.whereSqlJoiner);
            a1.paramMap.putAll(a2.paramMap);
            return a1;
        }

        private static SqlResult finisher(SqlResultJoiner a) {
            return SqlResult.of(a.joins, a.whereSqlJoiner.toString(), a.paramMap);
        }
    }
}
