package blazingtwist.wswebservice.functions;

import blazingtwist.config.JsonDefaultConstructor;
import blazingtwist.sod.AvatarDisplayData;
import blazingtwist.wswebservice.WebServiceFunction;
import com.sun.net.httpserver.HttpExchange;
import java.io.StringWriter;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class GetAvatarDisplayData extends WebServiceFunction {
	@JsonDefaultConstructor
	public GetAvatarDisplayData() {
		super(GetAvatarDisplayData.class.getSimpleName());
	}

	@Override
	public void handle(HttpExchange exchange, Map<String, String> params) {
		// TODO

		try {
			JAXBContext contextObject = JAXBContext.newInstance(AvatarDisplayData.class);
			Marshaller marshaller = contextObject.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			StringWriter writer = new StringWriter();
			marshaller.marshal(null, writer);
			super.respond(exchange, 200, writer.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
			super.respond(exchange, 500, "Exception while handling the request.");
		}

	}
}
