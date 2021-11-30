package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.runtime.database.SquirrelExecuterHandler;
import i4gl.runtime.database.SquirrelSession;
import i4gl.runtime.values.I4GLDatabase;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;

public class I4GLSqlNode extends I4GLStatementNode {
    private final String sql;
    @Child
    private I4GLExpressionNode databaseVariableNode;
    @Child
    private InteropLibrary interop;

    public I4GLSqlNode(final I4GLExpressionNode databaseVariableNode, final String sql) {
        this.databaseVariableNode = databaseVariableNode;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.sql = sql;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        final I4GLDatabase database = (I4GLDatabase) databaseVariableNode.executeGeneric(frame);
        database.connect();
        SquirrelSession session = database.getSession();
        SquirrelExecuterHandler sqlExecuterHandlerProxy = new SquirrelExecuterHandler(session);
        SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(session, sql, sqlExecuterHandlerProxy);
        sqlExecuterTask.setExecuteEditableCheck(false);
        sqlExecuterTask.run();
    }
}