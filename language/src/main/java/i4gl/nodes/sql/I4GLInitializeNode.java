package i4gl.nodes.sql;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.exceptions.NotImplementedException;
import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.nodes.variables.write.I4GLAssignResultsNode;
import i4gl.runtime.values.I4GLNull;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class I4GLInitializeNode extends I4GLStatementNode {
    private TableColumnInfo[] tableColumnInfoArray;
    private final I4GLAssignResultsNode assignResultsNode;
    @Child
    private InteropLibrary interop;

    public I4GLInitializeNode(
            final I4GLAssignResultsNode assignResultsNode, final TableColumnInfo[] tableColumnInfoArray) {
        this.tableColumnInfoArray = tableColumnInfoArray;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.assignResultsNode = assignResultsNode;
    }

    Object[] getDefaults() {
        List<Object> defaults = new ArrayList<>();
        for (var tableColumnInfo : tableColumnInfoArray) {
            String defaultValue = tableColumnInfo.getDefaultValue();
            if (defaultValue == null) {
                defaults.add(I4GLNull.SINGLETON);
            } else {
                switch (tableColumnInfo.getDataType()) {
                    case Types.CHAR:
                    case Types.NCHAR:
                    case Types.VARCHAR:
                    case Types.NVARCHAR:
                        assert defaultValue.startsWith("'") || defaultValue.startsWith("\"");
                        assert defaultValue.endsWith("'") || defaultValue.endsWith("\"");
                        defaults.add(defaultValue.substring(1, defaultValue.length() - 1));
                        break;
                    case Types.INTEGER:
                        defaults.add(Integer.valueOf(defaultValue));
                        break;
                    case Types.BIGINT:
                        defaults.add(Long.valueOf(defaultValue));
                        break;
                    case Types.REAL:
                        defaults.add(Float.valueOf(defaultValue));
                        break;
                    case Types.DECIMAL:
                    case Types.FLOAT:
                        defaults.add(Double.valueOf(defaultValue));
                        break;
                    default:
                        throw new NotImplementedException();
                }
            }
        }
        return defaults.toArray(new Object[0]);
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        assignResultsNode.setResults(getDefaults());
        assignResultsNode.executeVoid(frame);
    }
}