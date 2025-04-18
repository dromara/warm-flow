/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.flow.core.keygen;

/**
 * warm 生成15位有序id
 *
 * @author HUANGJIANSHISHENG
 * @see <a href="https://warm-flow.dromara.org/master/primary/idGen.html">文档地址</a>
 */
public class SnowFlakeId15 implements KenGen {
    // 开始时间戳 (任意设置，建议为项目的开始时间)
    private final long epoch = 1609459200000L; // 2021-01-01 00:00:00

    // 各部分的位数
    private final long sequenceBits = 6L;   // 序列号占用6位
    private final long machineIdBits = 4L;  // 机器ID占用4位

    // 各部分的最大值
    private final long maxMachineId = -1L ^ (-1L << machineIdBits);
    private final long maxSequence = -1L ^ (-1L << sequenceBits);

    // 各部分的偏移量
    private final long sequenceShift = 0;
    private final long machineIdShift = sequenceBits;
    private final long timestampShift = machineIdBits + sequenceBits;

    private long machineId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowFlakeId15(long machineId) {
        if (machineId > maxMachineId || machineId < 0) {
            throw new IllegalArgumentException("Machine ID is out of bounds.");
        }
        this.machineId = machineId;
    }

    @Override
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id.");
        }

        if (timestamp == lastTimestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 生成ID并保证其为15位
        long id = ((timestamp - epoch) << timestampShift)
            | (machineId << machineIdShift)
            | sequence;

        return id % 10_000_000_000_000_000L; // 保证生成的ID为15位
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

}
