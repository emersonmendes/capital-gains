package br.com.nu;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.console.StockOperationConsole;
import br.com.nu.capitalgain.service.StockOperationService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CapitalGainApplication {

    public static void main(String[] args) {
        final var config = new ConfigLoader("config");
        final var stockOperationService = new StockOperationService(config);
        final var stockOperationConsole = new StockOperationConsole(stockOperationService, new ObjectMapper());
        String stdout = stockOperationConsole.start(args);
        System.out.println(stdout);
    }

}