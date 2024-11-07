package org.hhplus.ecommerce.common.redis;

import lombok.NoArgsConstructor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@NoArgsConstructor
public class CustomSpringELParser {
    public static String getDynamicValue(String[] parameterNames, Object[] args,String prefix, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        String resultKey = "\"" + prefix + ":\" + " + key;
        return parser.parseExpression(resultKey).getValue(context, String.class);
    }
}
