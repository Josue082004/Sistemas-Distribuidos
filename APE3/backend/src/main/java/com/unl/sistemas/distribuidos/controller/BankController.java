package com.unl.sistemas.distribuidos.controller;

import com.unl.sistemas.distribuidos.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cajero")
@CrossOrigin(origins = "http://localhost:5173")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/saldo")
    public ResponseEntity<BigDecimal> obtenerSaldo() {
        return ResponseEntity.ok(bankService.getSaldo());
    }

    @PostMapping("/operar")
    public ResponseEntity<String> realizarOperacion(
            @RequestParam int id, 
            @RequestParam BigDecimal cantidad, 
            @RequestParam boolean esDeposito) {
        
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0 || cantidad.scale() > 2) {
            return ResponseEntity.badRequest().body("Error: Cantidad inválida, debe ser mayor a 0 y con máximo 2 decimales.");
        }

        if (esDeposito) {
            boolean exito = bankService.depositar(id, cantidad);
            if (exito) {
                return ResponseEntity.ok("Depósito de $" + cantidad + " realizado con éxito por Proceso " + id + ".");
            } else {
                return ResponseEntity.badRequest().body("Error al procesar el depósito.");
            }
        } else {
            boolean exito = bankService.retirar(id, cantidad);
            if (exito) {
                return ResponseEntity.ok("Retiro de $" + cantidad + " realizado con éxito por Proceso " + id + ".");
            } else {
                return ResponseEntity.status(422).body("Error: Saldo insuficiente en la cuenta para este retiro.");
            }
        }
    }

    @PostMapping("/simular-practica")
    public ResponseEntity<String> simularPracticaConcurrente() {
        new Thread(() -> bankService.retirar(1, new BigDecimal("300.00"))).start();
        new Thread(() -> bankService.retirar(2, new BigDecimal("250.00"))).start();
        new Thread(() -> bankService.depositar(3, new BigDecimal("200.00"))).start();

        return ResponseEntity.ok("Simulación iniciada: Procesos 1, 2 y 3 ejecutados en paralelo.");
    }

    @PostMapping("/reiniciar")
    public ResponseEntity<String> reiniciarCuenta() {
        bankService.reiniciarSaldo();
        return ResponseEntity.ok("Cuenta reiniciada con éxito. Saldo actual: $1000.00");
    }
}