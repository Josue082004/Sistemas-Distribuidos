package com.unl.sistemas.distribuidos.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BankService {
    private BigDecimal saldo = new BigDecimal("1000.00").setScale(2, RoundingMode.HALF_UP);
    private final Lock mutexSaldo = new ReentrantLock();

    public boolean retirar(int id, BigDecimal cantidad) {
        mutexSaldo.lock();
        try {
            if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0 || cantidad.scale() > 2) {
                return false;
            }
            
            cantidad = cantidad.setScale(2, RoundingMode.HALF_UP);
            System.out.println("Proceso " + id + " intenta retirar $" + cantidad);
            
            if (saldo.compareTo(cantidad) >= 0) {
                saldo = saldo.subtract(cantidad).setScale(2, RoundingMode.HALF_UP);
                System.out.println("-> Retiro exitoso. Saldo actual: $" + saldo);
                return true;
            } else {
                System.out.println("-> Saldo insuficiente.");
                return false;
            }
        } finally {
            mutexSaldo.unlock();
        }
    }

    public boolean depositar(int id, BigDecimal cantidad) {
        mutexSaldo.lock();
        try {
            if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0 || cantidad.scale() > 2) {
                return false;
            }
            cantidad = cantidad.setScale(2, RoundingMode.HALF_UP);
            System.out.println("Proceso " + id + " deposita $" + cantidad);
            
            saldo = saldo.add(cantidad).setScale(2, RoundingMode.HALF_UP);
            System.out.println("-> Depósito exitoso. Saldo actual: $" + saldo);
            return true;
        } finally {
            mutexSaldo.unlock();
        }
    }

    public BigDecimal getSaldo() {
        mutexSaldo.lock();
        try {
            return saldo.setScale(2, RoundingMode.HALF_UP);
        } finally {
            mutexSaldo.unlock();
        }
    }
    public void reiniciarSaldo() {
        mutexSaldo.lock();
        try {
            saldo = new BigDecimal("1000.00").setScale(2, RoundingMode.HALF_UP);
            System.out.println("-> Saldo reiniciado a su valor inicial: $1000.00");
        } finally {
            mutexSaldo.unlock();
        }
    }
}