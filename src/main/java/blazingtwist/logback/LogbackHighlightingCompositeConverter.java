package blazingtwist.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class LogbackHighlightingCompositeConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {
	@Override
	protected String getForegroundColorCode(ILoggingEvent event) {
		Level level = event.getLevel();
		switch(level.toInt()){
			case Level.ERROR_INT:
				return ANSIConstants.RED_FG;
			case Level.WARN_INT:
				return ANSIConstants.YELLOW_FG;
			case Level.DEBUG_INT:
			case Level.TRACE_INT:
				return ANSIConstants.WHITE_FG;
			default:
				return ANSIConstants.DEFAULT_FG;
		}
	}
}
