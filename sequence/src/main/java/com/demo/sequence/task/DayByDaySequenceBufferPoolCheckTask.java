package com.demo.sequence.task;

import com.demo.sequence.impl.DefaultDayByDaySequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DayByDaySequenceBufferPoolCheckTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DayByDaySequenceBufferPoolCheckTask.class);

    private DefaultDayByDaySequence sequence;

    private String appName;

    private Date currentDay;

    public DayByDaySequenceBufferPoolCheckTask(DefaultDayByDaySequence sequence, String appName, Date currentDay) {
        this.sequence = sequence;
        this.appName = appName;
        this.currentDay = currentDay;
    }

    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sequence.getClass().getSimpleName() + "开始load数据");
        }
        this.sequence.nextValue(appName, currentDay);
    }

}
