package org.example.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

public class CalculatorContext {
    private static final Logger logger = Logger.getLogger(CalculatorContext.class.getName());

    private final Stack<Double> stack;
    private final Map<String, Double> variables;

    public CalculatorContext() {
        this.stack = new Stack<>();
        this.variables = new HashMap<>();
        logger.info("Контекст калькулятора инициализирован");
    }

    public Stack<Double> getStack() {
        return stack;
    }

    public Map<String, Double> getVariables() {
        return variables;
    }

    public void defineVariable(String name, Double value) {
        variables.put(name, value);
        logger.fine("Переменная определена: " + name + " = " + value);
    }

    public Double getVariable(String name) {
        return variables.get(name);
    }

    public void push(Double value) {
        stack.push(value);
        logger.fine("Значение добавлено в стек: " + value);
    }

    public Double pop() {
        Double value = stack.pop();
        logger.fine("Значение извлечено из стека: " + value);
        return value;
    }

    public Double peek() {
        return stack.peek();
    }

    public int stackSize() {
        return stack.size();
    }

    public boolean isStackEmpty() {
        return stack.isEmpty();
    }
}