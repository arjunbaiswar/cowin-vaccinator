package com.arjun.cowin;

import com.arjun.cowin.quartz.QuartzScheduler;
import com.arjun.cowin.config.AppProperties;
import com.arjun.cowin.http.RestClient;
import com.arjun.cowin.service.VaccineLocatorService;
import com.arjun.cowin.utils.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);
    private static String cowinBaseUrl = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public";
    private static List<Runnable> shutDownHook = new ArrayList<>();

    public static void main(String[] args) {
        Application application = new Application();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping application");
            application.stop();
            logger.info("ShutDown complete");
        }));
        application.start();
    }

    private void stop() {
        shutDownHook.forEach(runnable -> runnable.run());
    }

    private void start() {
        AppProperties appProperties = AppProperties.INSTANCE;
        VaccineLocatorService vaccineLocatorService = new VaccineLocatorService(new RestClient(cowinBaseUrl), new EmailService());
        QuartzScheduler scheduler = new QuartzScheduler(vaccineLocatorService);
        shutDownHook.add(() -> safely("Shutting down Quartz Scheduler", scheduler::shutDown));
    }

    private void safely(String desc, RunnableWithException runnable) {
        try {
            logger.info("Executing : {}", desc);
            runnable.run();
            logger.info("Executed : {}", desc);
        } catch (Exception e) {
            logger.error("Error while executing : {}", desc);
        }
    }

    private interface RunnableWithException {
        void run() throws Exception;
    }
}
