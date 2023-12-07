
package com.lb.kafkademo;


import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class MessageProcessor implements Processor<String, String, String, String> {
    private ProcessorContext context = null;

    public MessageProcessor() {}

    List<Record> store = new ArrayList<>();

    @Override
    public void init(ProcessorContext context) {
        this.context = context;

        this.context.schedule(Duration.ofMillis(TimeUnit.SECONDS.toMillis(1)),
                PunctuationType.WALL_CLOCK_TIME, (l) -> {
                    this.punctuate(l);
                });
    }

    @Override
    public void process(Record<String, String> record) {
        store.add(record);
    }


    public void punctuate(long timestamp) {
        String result = store.stream().map(r -> r.value().toString()).collect(Collectors.joining("-"));

        store.clear();

        context.forward(new Record(result,result, System.currentTimeMillis()));

        context.commit();
    }

    @Override
    public void close() {
    }

}