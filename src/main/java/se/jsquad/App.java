package se.jsquad;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.jsquad.property.AppProperty;
import se.jsquad.rest.GetClientInformationRestController;


public class App {
    private static Logger logger = LogManager.getLogger(App.class.getName());

    public static void main(String[] arguments) {
        logger.log(Level.INFO, "main(arguments: {})", arguments);

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(App.class.getResource(
                "/META-INF/applicationContext.xml").toString());

        GetClientInformationRestController getClientInformationRESTController = applicationContext
                .getBean("getClientInformationRestControllerImpl",
                        GetClientInformationRestController.class);

        AppProperty appProperty = applicationContext.getBean("appProperty", AppProperty.class);
        appProperty.getVersion();
        appProperty.getName();

        getClientInformationRESTController.getClientInformation("191212");

        applicationContext.close();
    }
}
