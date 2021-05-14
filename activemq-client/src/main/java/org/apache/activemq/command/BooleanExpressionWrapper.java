package org.apache.activemq.command;

import org.apache.activemq.filter.BooleanExpression;
import org.apache.activemq.filter.Expression;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.selector.SelectorParser;

import javax.jms.IllegalStateException;
import javax.jms.InvalidSelectorException;
import javax.jms.JMSException;

/**
 * @openwire:marshaller code="91"
 *
 */
public class BooleanExpressionWrapper implements BooleanExpression, DataStructure {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.BOOLEAN_EXPRESSION;

    private transient BooleanExpression expression;

    public BooleanExpressionWrapper() {
    }

    public BooleanExpressionWrapper(final BooleanExpression expression) {
        this.expression = expression;
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public boolean isMarshallAware() {
        return false;
    }

    @Override
    public Object evaluate(final MessageEvaluationContext message) throws JMSException {
        if (expression == null) {
            throw new IllegalStateException("expression is null");
        }

        return expression.evaluate(message);
    }

    @Override
    public boolean matches(final MessageEvaluationContext message) throws JMSException {
        if (expression == null) {
            throw new IllegalStateException("expression is null");
        }

        return expression.matches(message);
    }

    /**
     * @openwire:property version=12
     */
    public String getExpression() {
        if (this.expression == null) {
            return null;
        }

        return expression.toString();
    }

    public void setExpression(final String expression) {
        this.expression = null;
        try {
            this.expression = SelectorParser.parse(expression);
        } catch (InvalidSelectorException e) {
            // we should log this
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        if (expression == null) {
            return null;
        }

        return expression.toString();
    }
}
