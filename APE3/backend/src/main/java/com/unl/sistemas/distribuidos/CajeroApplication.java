package com.unl.sistemas.distribuidos;

import com.unl.sistemas.distribuidos.service.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class CajeroApplication implements CommandLineRunner {

    private final BankService bankService;

    public CajeroApplication(BankService bankService) {
        this.bankService = bankService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CajeroApplication.class, args);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        System.out.println("=== SIMULACIÓN CONCURRENTE DEL CAJERO (EJERCICIO 2) ===");
        System.out.println("Saldo inicial: $" + bankService.getSaldo());

        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Uso de BigDecimal.valueOf(...) para evitar el error de tipos incomatibles
        executor.submit(() -> bankService.retirar(1, BigDecimal.valueOf(300.00)));
        executor.submit(() -> bankService.retirar(2, BigDecimal.valueOf(250.00)));
        executor.submit(() -> bankService.depositar(3, BigDecimal.valueOf(200.00)));

        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);

        System.out.println("Saldo final obtenido: $" + bankService.getSaldo());
        System.out.println("Saldo final esperado: $650.00");
        System.out.println("=====================================================");
    }
}