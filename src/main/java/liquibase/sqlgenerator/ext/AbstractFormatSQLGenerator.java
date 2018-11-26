package liquibase.sqlgenerator.ext;

import java.util.Collection;
import java.util.regex.Pattern;

import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AbstractSqlGenerator;
import liquibase.statement.SqlStatement;
import liquibase.structure.DatabaseObject;

/**
 * @author fra
 */
public abstract class AbstractFormatSQLGenerator<T extends SqlStatement> extends
        AbstractSqlGenerator<T> {

    protected static Formatter removeIndent(Formatter formatter) {
        return source -> {
            String s = formatter.format(source);
            return Pattern.compile("^[ ]{4}", Pattern.MULTILINE)
                    .matcher(s.replaceFirst("\\s+", ""))
                    .replaceAll("");
        };
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public ValidationErrors validate(
            T statement,
            Database database,
            SqlGeneratorChain sqlGeneratorChain) {
        return sqlGeneratorChain.validate(statement, database);
    }

    @Override
    public Sql[] generateSql(
            T statement,
            Database database,
            SqlGeneratorChain sqlGeneratorChain) {
        Sql[] sqls = sqlGeneratorChain.generateSql(statement, database);
        Formatter formatter = formatter(statement);
        return formatter != null ? wrap(sqls, formatter) : sqls;
    }

    protected Formatter formatter(T statement) {
        return removeIndent(new BasicFormatterImpl());
    }

    private Sql[] wrap(Sql[] sqls, Formatter formatter) {
        Sql[] copy = new Sql[sqls.length];
        for (int i = 0; i < sqls.length; i++) {
            copy[i] = wrap(sqls[i], formatter);
        }
        return copy;
    }

    private Sql wrap(Sql sql, Formatter formatter) {
        return new Sql() {

            @Override
            public String toSql() {
                return formatter.format(sql.toSql());
            }

            @Override
            public String getEndDelimiter() {
                return sql.getEndDelimiter();
            }

            @Override
            public Collection<? extends DatabaseObject> getAffectedDatabaseObjects() {
                return sql.getAffectedDatabaseObjects();
            }

        };
    }

}
