package i4gl.nodes.operators;

import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.I4GLLanguage;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.DateType;
import i4gl.runtime.values.Date;

@NodeInfo(shortName = "MDY")
@NodeChild("monthNode")
@NodeChild("dayNode")
@NodeChild("yearNode")
public abstract class MdyNode extends ExpressionNode {
    private static final TruffleLogger LOGGER = I4GLLanguage.getLogger(MdyNode.class);

	protected abstract ExpressionNode getMonthNode();
	protected abstract ExpressionNode getDayNode();
	protected abstract ExpressionNode getYearNode();

    @Specialization
    public Date mdy(int month, int day, int year) {
		if(month < 1 || month > 12) {
			LOGGER.warning("Month " + month + " is not in months (1-12) range");
		}
		if(month < 1 || month > 31) {
			LOGGER.warning("Day " + day + " is not in days (1-31) range");
		}
		if(year < 1 || year > 9999) {
			LOGGER.warning("Year " + year + " is not in years (1-9999) range");
		}
		return Date.valueOf(year, month, day);
	}

    @Override
    public BaseType getReturnType() {
        return DateType.SINGLETON;
    }
}
