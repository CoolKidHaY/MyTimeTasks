package com.hay.task.exception;

/**
 * @title: TaskExecutedException
 * @Author HuangYan
 * @Date: 2021/10/21 10:15
 * @Version 1.0
 * @Description: 任务以执行异常
 */
public class TaskExecutedException extends BaseException {
    public TaskExecutedException() {
        super();
    }

    public TaskExecutedException(String message) {
        super(message);
    }
}
