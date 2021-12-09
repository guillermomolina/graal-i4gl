package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.context.Context;
import i4gl.runtime.database.SquirrelExecuterHandler;
import i4gl.runtime.database.SquirrelSession;
import i4gl.runtime.values.Database;
import i4gl.runtime.values.Record;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;

public class SqlNode extends StatementNode {
    private final String sql;
    @Child
    private ExpressionNode databaseVariableNode;
    @Child
    private InteropLibrary interop;

    public SqlNode(final ExpressionNode databaseVariableNode, final String sql) {
        this.databaseVariableNode = databaseVariableNode;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.sql = sql;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        Record sqlca = Context.get(this).getSqlcaGlobalVariable();
        final Database database = (Database) databaseVariableNode.executeGeneric(frame);
        database.connect(sqlca);
        SquirrelSession session = database.getSession();
        SquirrelExecuterHandler sqlExecuterHandlerProxy = new SquirrelExecuterHandler(session);
        SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(session, sql, sqlExecuterHandlerProxy);
        sqlExecuterTask.setExecuteEditableCheck(false);
        sqlExecuterTask.run();
    }
}