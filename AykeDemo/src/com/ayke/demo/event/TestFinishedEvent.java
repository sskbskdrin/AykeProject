package com.ayke.demo.event;

public class TestFinishedEvent {

    public final Test test;
    public final boolean isLastEvent;

    public TestFinishedEvent(Test test, boolean isLastEvent) {
        this.test = test;
        this.isLastEvent = isLastEvent;
    }
}
