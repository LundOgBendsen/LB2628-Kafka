
package com.lb.kafkademo;


import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class DelayMessageProcessor implements Processor<String, Long, String, Long> {
    private ProcessorContext context = null;

    public DelayMessageProcessor() {}

    Map<String, Long> store = new HashMap<>();

    @Override
    public void init(ProcessorContext context) {
        this.context = context;

        this.context.schedule(Duration.ofMillis(TimeUnit.SECONDS.toMillis(3)),
                PunctuationType.WALL_CLOCK_TIME, (l) -> {
                    this.punctuate(l);
                });
    }

    @Override
    public void process(Record<String, Long> record) {
        store.put(record.key(),record.value());
    }


    public void punctuate(long timestamp) {
        for(Map.Entry<String, Long> entry : store.entrySet()) {
            context.forward(new Record(entry.getKey(), entry.getValue(), System.currentTimeMillis()));
        }
        context.commit();
    }

    @Override
    public void close() {
    }

}