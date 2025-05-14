package kr.hhplus.be.server.common.lock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class LockKeyGenerator {

    private static final String LOCK_PREFIX = "lock:";

    public String generateKey(String[] parameterNames, Object[] args, String key, LockType lockType) {

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        String parseKey = parser.parseExpression(key).getValue(context, String.class);

        return LOCK_PREFIX + lockType.createKey(parseKey);
    }
}
