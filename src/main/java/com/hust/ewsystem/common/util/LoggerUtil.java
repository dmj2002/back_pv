package com.hust.ewsystem.common.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class LoggerUtil {

    @Value("${algorithm.pythonFilePath}")
    private String pythonFilePath;

    public Logger createTaskLogger(String taskId) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // 1. 创建 RollingFileAppender
        RollingFileAppender appender = new RollingFileAppender();
        appender.setContext(context);
        appender.setName("TASK_LOGGER_" + taskId);
        appender.setAppend(true);

        // 2. 设置文件路径和文件名（带任务ID）
        String logFilePath = pythonFilePath + "/task_logs/" + taskId + "/" + taskId + ".log";
        appender.setFile(logFilePath);

        // 3. 配置滚动策略
        SizeAndTimeBasedRollingPolicy rollingPolicy = new SizeAndTimeBasedRollingPolicy();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(appender);
        rollingPolicy.setFileNamePattern("logs/task_" + taskId + ".%d{yyyy-MM-dd}.%i.log.zip");
        rollingPolicy.setMaxFileSize(FileSize.valueOf("100MB"));
        rollingPolicy.setMaxHistory(60);
        rollingPolicy.start();

        appender.setRollingPolicy(rollingPolicy);

        // 4. 设置日志格式
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("[%d{yyyy-MM-dd HH:mm:ss.SSS}] %-5level [%thread] %logger{35}.%method:%L - %msg%n");
        encoder.start();
        appender.setEncoder(encoder);
        appender.start();

        // 5. 创建 Logger 并添加 appender
        Logger logger = context.getLogger("TASK_LOGGER_" + taskId);
        logger.addAppender(appender);
        logger.setAdditive(false); // 不向父 logger 传递
        return logger;
    }
}
