import { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [saldo, setSaldo] = useState(0);
  const [logs, setLogs] = useState([]);

  const obtenerSaldo = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/cajero/saldo');
      const data = await res.json();
      setSaldo(data);
    } catch (e) {
      console.error("Error al conectar con el backend", e);
    }
  };

  useEffect(() => {
    obtenerSaldo();
    agregarLog("Sistema inicializado. Mutex (ReentrantLock) listo para coordinar la exclusión mutua.");
  }, []);

  const agregarLog = (mensaje) => {
    const hora = new Date().toLocaleTimeString();
    setLogs(prev => [{ hora, mensaje }, ...prev]);
  };

  // Ejecuta un proceso específico asignado por la práctica
  const ejecutarProcesoEspecifico = async (id, cantidad, esDeposito) => {
    const operacionTexto = esDeposito ? `Depósito de $${cantidad}` : `Retiro de $${cantidad}`;

    if (!esDeposito && cantidad > saldo) {
      alert(`Operación rechazada: El Proceso #${id} intenta retirar $${cantidad}, pero el saldo actual es de $${saldo}.`);
      agregarLog(` Proceso #${id} intenta ${operacionTexto} -> Rechazado por saldo insuficiente.`);
      return;
    }

    try {
      const res = await fetch(`http://localhost:8080/api/cajero/operar?id=${id}&cantidad=${cantidad}&esDeposito=${esDeposito}`, {
        method: 'POST'
      });
      const text = await res.text();

      if (!res.ok) {
        agregarLog(` Proceso #${id} [HTTP ${res.status}]: ${text}`);
      } else {
        agregarLog(` Proceso #${id}: ${text}`);
      }
      obtenerSaldo();
    } catch (e) {
      agregarLog(` Error de comunicación al ejecutar el Proceso #${id}`);
    }
  };

  // Lanza los 3 procesos concurrentes de la práctica simultáneamente en el Backend
  const ejecutarSimulacionCompleta = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/cajero/simular-practica', { method: 'POST' });
      const text = await res.text();
      agregarLog(`⚙️ SIMULACIÓN OFICIAL LANZADA: Procesos 1, 2 y 3 ejecutados en paralelo.`);
      setTimeout(obtenerSaldo, 500);
    } catch (e) {
      agregarLog(`❌ Error al conectar con el servidor para la simulación.`);
    }
  };

  return (
    <>
      <section className="panel">
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', width: '100%' }}>
          <span style={{ fontSize: '0.75rem', fontWeight: 'bold', textTransform: 'uppercase', color: 'var(--ink-muted)', letterSpacing: '2px' }}>
            Controles de Procesos del Ejercicio
          </span>
          <div className="actions" style={{ flexWrap: 'wrap', gap: '12px' }}>
            <button onClick={() => ejecutarProcesoEspecifico(1, 300.00, false)} className="btn-danger">
              <span>Proceso 1: Retirar $300</span>
            </button>
            <button onClick={() => ejecutarProcesoEspecifico(2, 250.00, false)} className="btn-danger">
              <span>Proceso 2: Retirar $250</span>
            </button>
            <button onClick={() => ejecutarProcesoEspecifico(3, 200.00, true)} className="btn-success">
              <span>Proceso 3: Depositar $200</span>
            </button>
            <button onClick={async () => {
              const res = await fetch('http://localhost:8080/api/cajero/reiniciar', { method: 'POST' });
              const text = await res.text();
              agregarLog(`🔄 ${text}`);
              obtenerSaldo();
            }} style={{ background: '#64748b', color: 'white', border: 'none', padding: '14px 20px', borderRadius: '3px', fontWeight: 'bold', cursor: 'pointer' }}>
              <span>🔄 Reiniciar Saldo ($1000)</span>
            </button>
          </div>
        </div>
      </section>

      <section className="metricas">
        <div className="card">
          <div className="card-tag">Saldo Actual</div>
          <div className="card-value">${Number(saldo).toFixed(2)}</div>
          <div className="card-label">Recurso compartido protegido</div>
        </div>
        <div className="card">
          <div className="card-tag">Base Inicial</div>
          <div className="card-value">$1000.00</div>
          <div className="card-label">Valor de referencia inicial</div>
        </div>
      </section>

      <section className="log-section">
        <div className="log-header">
          <h2>Bitacora de operaciones concurrentes</h2>
          <span className="card-tag">{logs.length} eventos registrados</span>
        </div>
        <div className="log-container">
          {logs.map((log, index) => (
            <div className="log-entry" key={index}>
              <span className="hora">[{log.hora}]</span>
              <span className="msg">{log.mensaje}</span>
            </div>
          ))}
        </div>
      </section>
    </>
  );
}

export default App;