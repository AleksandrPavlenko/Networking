import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP
import ch.qos.logback.core.status.NopStatusListener
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import static ch.qos.logback.classic.Level.ALL
import static ch.qos.logback.classic.Level.DEBUG

def LOG_NAME = "SocketServer.log"
def PERFORMANCE_LOG_NAME = "Performance.log"

statusListener(NopStatusListener)

appender("FILE_APPENDER", RollingFileAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%t %d{dd MMM yyyy HH:mm:ss:SSS} %-5p %c: %M - %m%n"
    }
    file=${LOG_NAME}
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${LOG_NAME}-%d{yyyy-MM-dd}.%i.zip"
        minIndex = 1
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = "100MB"
        }
    }
}
appender("PERFORMANCE_APPENDER", RollingFileAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%t %d{dd MMM yyyy HH:mm:ss:SSS} %-5p %c: %M - %m%n"
    }
    file=${PERFORMANCE_LOG_NAME}
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${PERFORMANCE_LOG_NAME}-%d{yyyy-MM-dd}.%i.zip"
        minIndex = 1
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = "100MB"
        }
    }
}

logger("net.pavlenko.networking.server", ALL, ["FILE_APPENDER"])
logger("org.perf4j.TimingLogger", ALL, ["PERFORMANCE_APPENDER"])