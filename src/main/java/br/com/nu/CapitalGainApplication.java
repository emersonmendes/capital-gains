package br.com.nu;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.console.JsonMapper;
import br.com.nu.capitalgain.console.ShareOperationConsole;
import br.com.nu.capitalgain.processor.ShareOperationProcessorFactory;
import br.com.nu.capitalgain.service.ShareOperationService;

public class CapitalGainApplication {

    public static void main(String[] args) {
        final var config = new ConfigLoader("config");
        final var shareOperationProcessorFactory = new ShareOperationProcessorFactory(config);
        final var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);
        final var shareOperationConsole = new ShareOperationConsole(shareOperationService, new JsonMapper());
        shareOperationConsole.start(args);
    }

}