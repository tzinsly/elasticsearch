// Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
// or more contributor license agreements. Licensed under the Elastic License
// 2.0; you may not use this file except in compliance with the Elastic License
// 2.0.
package org.elasticsearch.xpack.esql.expression.function.scalar.math;

import java.lang.Double;
import java.lang.Override;
import java.lang.String;
import org.elasticsearch.compute.data.Block;
import org.elasticsearch.compute.data.DoubleBlock;
import org.elasticsearch.compute.data.DoubleVector;
import org.elasticsearch.compute.data.Page;
import org.elasticsearch.compute.operator.EvalOperator;
import org.elasticsearch.xpack.ql.expression.Expression;

/**
 * {@link EvalOperator.ExpressionEvaluator} implementation for {@link Round}.
 * This class is generated. Do not edit it.
 */
public final class RoundDoubleNoDecimalsEvaluator implements EvalOperator.ExpressionEvaluator {
  private final EvalOperator.ExpressionEvaluator val;

  public RoundDoubleNoDecimalsEvaluator(EvalOperator.ExpressionEvaluator val) {
    this.val = val;
  }

  static Double fold(Expression val) {
    Object valVal = val.fold();
    if (valVal == null) {
      return null;
    }
    return Round.process(((Number) valVal).doubleValue());
  }

  @Override
  public Block eval(Page page) {
    Block valUncastBlock = val.eval(page);
    if (valUncastBlock.areAllValuesNull()) {
      return Block.constantNullBlock(page.getPositionCount());
    }
    DoubleBlock valBlock = (DoubleBlock) valUncastBlock;
    DoubleVector valVector = valBlock.asVector();
    if (valVector == null) {
      return eval(page.getPositionCount(), valBlock);
    }
    return eval(page.getPositionCount(), valVector).asBlock();
  }

  public DoubleBlock eval(int positionCount, DoubleBlock valBlock) {
    DoubleBlock.Builder result = DoubleBlock.newBlockBuilder(positionCount);
    position: for (int p = 0; p < positionCount; p++) {
      if (valBlock.isNull(p) || valBlock.getValueCount(p) != 1) {
        result.appendNull();
        continue position;
      }
      result.appendDouble(Round.process(valBlock.getDouble(valBlock.getFirstValueIndex(p))));
    }
    return result.build();
  }

  public DoubleVector eval(int positionCount, DoubleVector valVector) {
    DoubleVector.Builder result = DoubleVector.newVectorBuilder(positionCount);
    position: for (int p = 0; p < positionCount; p++) {
      result.appendDouble(Round.process(valVector.getDouble(p)));
    }
    return result.build();
  }

  @Override
  public String toString() {
    return "RoundDoubleNoDecimalsEvaluator[" + "val=" + val + "]";
  }
}
