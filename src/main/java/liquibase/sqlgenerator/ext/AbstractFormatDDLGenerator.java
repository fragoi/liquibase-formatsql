package liquibase.sqlgenerator.ext;

import org.hibernate.engine.jdbc.internal.DDLFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

import liquibase.statement.SqlStatement;

/**
 * @author fra
 */
public abstract class AbstractFormatDDLGenerator<T extends SqlStatement> extends
        AbstractFormatSQLGenerator<T> {

    @Override
    protected Formatter formatter(T statement) {
        return removeIndent(new DDLFormatterImpl());
    }

}
