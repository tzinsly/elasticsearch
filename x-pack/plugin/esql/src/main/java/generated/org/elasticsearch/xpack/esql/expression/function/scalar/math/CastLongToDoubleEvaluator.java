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
import org.elasticsearch.compute.data.LongBlock;
import org.elasticsearch.compute.data.LongVector;
import org.elasticsearch.compute.data.Page;
import org.elasticsearch.compute.operator.EvalOperator;
import org.elasticsearch.xpack.ql.expression.Expression;

/**
 * {@link EvalOperator.ExpressionEvaluator} implementation for {@link Cast}.
 * This class is generated. Do not edit it.
 */
public final class CastLongToDoubleEvaluator implements EvalOperator.ExpressionEvaluator {
  private final EvalOperator.ExpressionEvaluator v;

  public CastLongToDoubleEvaluator(EvalOperator.ExpressionEvaluator v) {
    this.v = v;
  }

  static Double fold(Expression v) {
    Object vVal = v.fold();
    if (vVal == null) {
      return null;
    }
    return Cast.castLongToDouble(((Number) vVal).longValue());
  }

  @Override
  public Block eval(Page page) {
    Block vUncastBlock = v.eval(page);
    if (vUncastBlock.areAllValuesNull()) {
      return Block.constantNullBlock(page.getPositionCount());
    }
    LongBlock vBlock = (LongBlock) vUncastBlock;
    LongVector vVector = vBlock.asVector();
    if (vVector == null) {
      return eval(page.getPositionCount(), vBlock);
    }
    return eval(page.getPositionCount(), vVector).asBlock();
  }

  public DoubleBlock eval(int positionCount, LongBlock vBlock) {
    DoubleBlock.Builder result = DoubleBlock.newBlockBuilder(positionCount);
    position: for (int p = 0; p < positionCount; p++) {
      if (vBlock.isNull(p) || vBlock.getValueCount(p) != 1) {
        result.appendNull();
        continue position;
      }
      result.appendDouble(Cast.castLongToDouble(vBlock.getLong(vBlock.getFirstValueIndex(p))));
    }
    return result.build();
  }

  public DoubleVector eval(int positionCount, LongVector vVector) {
    DoubleVector.Builder result = DoubleVector.newVectorBuilder(positionCount);
    position: for (int p = 0; p < positionCount; p++) {
      result.appendDouble(Cast.castLongToDouble(vVector.getLong(p)));
    }
    return result.build();
  }

  @Override
  public String toString() {
    return "CastLongToDoubleEvaluator[" + "v=" + v + "]";
  }
}
