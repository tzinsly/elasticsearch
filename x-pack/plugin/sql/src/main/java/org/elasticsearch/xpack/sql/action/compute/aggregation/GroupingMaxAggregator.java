/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.sql.action.compute.aggregation;

import org.elasticsearch.xpack.sql.action.compute.data.AggregatorStateBlock;
import org.elasticsearch.xpack.sql.action.compute.data.Block;
import org.elasticsearch.xpack.sql.action.compute.data.DoubleBlock;
import org.elasticsearch.xpack.sql.action.compute.data.Page;

class GroupingMaxAggregator implements GroupingAggregatorFunction {

    private final DoubleArrayState state;
    private final int channel;

    static GroupingMaxAggregator create(int inputChannel) {
        if (inputChannel < 0) {
            throw new IllegalArgumentException();
        }
        return new GroupingMaxAggregator(inputChannel, new DoubleArrayState());
    }

    static GroupingMaxAggregator createIntermediate() {
        return new GroupingMaxAggregator(-1, new DoubleArrayState());
    }

    private GroupingMaxAggregator(int channel, DoubleArrayState state) {
        this.channel = channel;
        this.state = state;
    }

    @Override
    public void addRawInput(Block groupIdBlock, Page page) {
        assert channel >= 0;
        Block valuesBlock = page.getBlock(channel);
        DoubleArrayState state = this.state;
        int len = valuesBlock.getPositionCount();
        for (int i = 0; i < len; i++) {
            int groupId = (int) groupIdBlock.getLong(i);
            state.set(Math.max(state.getOrDefault(groupId, Double.MIN_VALUE), valuesBlock.getDouble(i)), groupId);
        }
    }

    @Override
    public void addIntermediateInput(Block groupIdBlock, Block block) {
        assert channel == -1;
        if (block instanceof AggregatorStateBlock) {
            @SuppressWarnings("unchecked")
            AggregatorStateBlock<DoubleArrayState> blobBlock = (AggregatorStateBlock<DoubleArrayState>) block;
            DoubleArrayState tmpState = new DoubleArrayState();
            blobBlock.get(0, tmpState);
            this.state.addIntermediate(groupIdBlock, tmpState);
        } else {
            throw new RuntimeException("expected AggregatorStateBlock, got:" + block);
        }
    }

    @Override
    public Block evaluateIntermediate() {
        AggregatorStateBlock.Builder<AggregatorStateBlock<DoubleArrayState>, DoubleArrayState> builder = AggregatorStateBlock
            .builderOfAggregatorState(DoubleArrayState.class);
        builder.add(state);
        return builder.build();
    }

    @Override
    public Block evaluateFinal() {
        DoubleArrayState s = state;
        int positions = s.largestIndex + 1;
        double[] result = new double[positions];
        for (int i = 0; i < positions; i++) {
            result[i] = s.get(i);
        }
        return new DoubleBlock(result, positions);
    }
}
