package br.com.nu;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.console.ShareOperationConsole;
import br.com.nu.capitalgain.service.ShareOperationService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CapitalGainApplication {

    public static void main(String[] args) {
        final var config = new ConfigLoader("config");
        final var shareOperationService = new ShareOperationService(config);
        final var shareOperationConsole = new ShareOperationConsole(shareOperationService, new ObjectMapper());
        String stdout = shareOperationConsole.start(args);
        System.out.println(stdout);
    }

}