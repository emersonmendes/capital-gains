package br.com.nu;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.console.ShareOperationConsole;
import br.com.nu.capitalgain.processor.ShareOperationProcessorFactory;
import br.com.nu.capitalgain.service.ShareOperationService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CapitalGainApplication {

    public static void main(String[] args) {
        final var config = new ConfigLoader("config");
        final var shareOperationProcessorFactory = new ShareOperationProcessorFactory(config);
        final var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);
        final var objectMapper = new ObjectMapper();
        final var shareOperationConsole = new ShareOperationConsole(shareOperationService, objectMapper);
        String stdout = shareOperationConsole.start(args);
        System.out.println(stdout);
    }

}