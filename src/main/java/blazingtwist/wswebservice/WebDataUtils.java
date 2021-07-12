package blazingtwist.wswebservice;

import blazingtwist.logback.LogbackLoggerProvider;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.Logger;

public class WebDataUtils {
	private static final Logger logger = LogbackLoggerProvider.getLogger(WebDataUtils.class);

	private static XMLGregorianCalendar getNextYearDate() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(Date.from(Instant.now().plus(365, ChronoUnit.DAYS)));
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException e) {
			logger.error("Error during getNextYearDate", e);
			return null;
		}
	}

	public static XMLGregorianCalendar getXmlCalendar(Instant instant){
		try{
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(Date.from(instant));
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException e) {
			logger.error("Error during getXmlCalendar", e);
			return null;
		}
	}
}
