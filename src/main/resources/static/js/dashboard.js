// ─────────────────────────────────────────────────────────
// CONFIG
// ─────────────────────────────────────────────────────────
const BASE_URL    = '';
const REFRESH_MS  = 5000;

// ─────────────────────────────────────────────────────────
// CHART.JS — DEFAULTS (segue a paleta da página)
// ─────────────────────────────────────────────────────────
Chart.defaults.color      = 'rgba(232,240,235,0.55)';
Chart.defaults.font.family = "'Montserrat', sans-serif";
Chart.defaults.font.size   = 12;

// Cores base do design
const COR_LIQUIDO   = '#3498db';
const COR_AMBIENTE  = '#e67e22';
const COR_REAL      = '#DEB75B';
const COR_PLANEJADO = '#2d6a4f';
const COR_INFO      = '#3498db';
const COR_AVISO     = '#e67e22';
const COR_PERIGO    = '#d00000';
const COR_SENSOR    = '#999';
const COR_GRADE     = 'rgba(45,106,79,0.25)';

// Escala padrão para todos os gráficos
function escalasPadrao(labelY = '') {
    return {
        x: {
            grid:  { color: COR_GRADE },
            ticks: { color: 'rgba(232,240,235,0.45)' }
        },
        y: {
            grid:  { color: COR_GRADE },
            ticks: { color: 'rgba(232,240,235,0.45)' },
            title: {
                display: !!labelY,
                text: labelY,
                color: 'rgba(232,240,235,0.45)',
                font: { size: 11 }
            }
        }
    };
}

// ─────────────────────────────────────────────────────────
// FETCH HELPER
// ─────────────────────────────────────────────────────────
async function apiFetch(path) {
    const res = await fetch(BASE_URL + path);
    if (!res.ok) throw new Error(`HTTP ${res.status} — ${path}`);
    return res.json();
}

// ─────────────────────────────────────────────────────────
// UTILS
// ─────────────────────────────────────────────────────────
function fmt(val, casas = 2, sufixo = '') {
    if (val === null || val === undefined) return '—';
    return Number(val).toFixed(casas) + sufixo;
}

function fmtHora(isoString) {
    if (!isoString) return '—';
    return new Date(isoString).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
}

function minParaHoras(min) {
    return min != null ? +(min / 60).toFixed(2) : null;
}

// Label legível para o tipo do alerta
const TIPO_LABEL = {
    INFO:        'Info',
    WARNING:     'Aviso',
    CRITICAL:    'Crítico',
    SENSOR_FAIL: 'Sensor'
};

// Tradução de etapas — cobre tanto o enum name (MASHING) quanto getStage() (Mashing)
const STAGE_PT = {
    'MASHING':      'Mostura',
    'BOILING':      'Fervura',
    'FERMENTATION': 'Fermentação',
    'MATURATION':   'Maturação',
    'Mashing':      'Mostura',
    'Boiling':      'Fervura',
    'Fermentation': 'Fermentação',
    'Maturation':   'Maturação'
};

// ─────────────────────────────────────────────────────────
// INSTÂNCIAS DOS GRÁFICOS
// ─────────────────────────────────────────────────────────
let chartHistorico, chartEtapa, chartSeveridade, chartDuracao;

function criarGraficos() {

    // 1. Linha — temperatura 24h (janelas de 1h)
    const labelsHoras = Array.from({length: 24}, (_, i) => String(i).padStart(2, '0') + 'h');

    chartHistorico = new Chart(
        document.getElementById('chart-historico').getContext('2d'),
        {
            type: 'line',
            data: {
                labels: labelsHoras,
                datasets: [
                    {
                        label: 'Líquido °C',
                        data: new Array(24).fill(null),
                        borderColor: COR_LIQUIDO,
                        borderWidth: 2,
                        pointRadius: 2,
                        pointBackgroundColor: COR_LIQUIDO,
                        tension: 0.35,
                        fill: false,
                        spanGaps: true
                    },
                    {
                        label: 'Ambiente °C',
                        data: new Array(24).fill(null),
                        borderColor: COR_AMBIENTE,
                        borderWidth: 2,
                        pointRadius: 2,
                        pointBackgroundColor: COR_AMBIENTE,
                        tension: 0.35,
                        fill: false,
                        spanGaps: true
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: escalasPadrao('°C')
            }
        }
    );

    // 2. Colunas — média por etapa
    chartEtapa = new Chart(
        document.getElementById('chart-etapa').getContext('2d'),
        {
            type: 'bar',
            data: {
                labels: ['Mostura', 'Fervura', 'Fermentação', 'Maturação'],
                datasets: [{
                    label: 'Média em celsius',
                    data: [null, null, null, null],
                    backgroundColor: ['#1b4332', '#3a7e45', '#3498db', '#e67e22'],
                    borderWidth: 1.5,
                    borderRadius: 3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: escalasPadrao('°C')
            }
        }
    );

    // 3. Colunas — alertas por severidade
    chartSeveridade = new Chart(
        document.getElementById('chart-severidade').getContext('2d'),
        {
            type: 'bar',
            data: {
                labels: ['Aviso', 'Crítico', 'Sensor'],
                datasets: [{
                    label: 'Qtd.',
                    data: [0, 0, 0],
                    backgroundColor: ['#e67e22', '#dd0000', '#999999'],
                    borderRadius: 3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: escalasPadrao('Alertas')
            }
        }
    );

    // 4. Barras agrupadas — real vs planejado máx.
    chartDuracao = new Chart(
        document.getElementById('chart-duracao').getContext('2d'),
        {
            type: 'bar',
            data: {
                labels: ['Mostura', 'Fervura', 'Fermentação', 'Maturação'],
                datasets: [
                    {
                        label: 'Tempo total na etapa',
                        data: [null, null, null, null],
                        backgroundColor: '#1b4332',
                        borderRadius: 3
                    },
                    {
                        label: 'Tempo planejado na etapa',
                        data: [null, null, null, null],
                        backgroundColor: '#3a7e45',
                        borderColor: COR_PLANEJADO,
                        borderRadius: 3
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: escalasPadrao('Dias')
            }
        }
    );
}

// ─────────────────────────────────────────────────────────
// FETCH — ETAPA ATUAL (popula o select no header)
// ─────────────────────────────────────────────────────────
async function fetchEtapaAtual() {
    try {
        const stage = await apiFetch('/api/stage');
        const select = document.getElementById('stage-select');
        if (select) select.value = stage;
    } catch(e) { console.error('stage:', e); }
}

// ─────────────────────────────────────────────────────────
// MUDA ETAPA — chamado pelo onchange do select
// ─────────────────────────────────────────────────────────
async function mudarEtapa(novaEtapa) {
    try {
        await fetch('/api/stage', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ stage: novaEtapa })
        });
    } catch(e) { console.error('mudar etapa:', e); }
}

// ─────────────────────────────────────────────────────────
// FETCH — KPIs INDIVIDUAIS
// ─────────────────────────────────────────────────────────
async function fetchKpisIndividuais() {
    // Última leitura
    try {
        const latest = await apiFetch('/api/readings/latest');
        document.getElementById('kpi-liquid').textContent   = fmt(latest.liquidTemp)  + '°C';
        document.getElementById('kpi-ambient').textContent  = fmt(latest.ambientTemp) + '°C';
        document.getElementById('kpi-humidity').textContent = fmt(latest.humidity)    + '%';
    } catch (e) { console.error('latest reading:', e); }

    // Total de alertas
    try {
        const data = await apiFetch('/api/alerts/count');
        document.getElementById('kpi-alerts-total').textContent = data.count;
    } catch (e) { console.error('alerts/count:', e); }

    // Alertas últimas 24h
    try {
        const data = await apiFetch('/api/alerts/recent/count');
        document.getElementById('kpi-alerts-24h').textContent = data.count;
    } catch (e) { console.error('alerts/recent/count:', e); }

    // Desvio padrão
    try {
        const data = await apiFetch('/api/kpis/desvio-padrao');
        document.getElementById('kpi-std').textContent = fmt(data.desvioPadrao) + '°C';
    } catch (e) { console.error('desvio-padrao:', e); }

    // Conformidade
    try {
        const data = await apiFetch('/api/kpis/conformidade');
        document.getElementById('kpi-conformidade').textContent = fmt(data.percentual) + '%';
    } catch (e) { console.error('conformidade:', e); }

    // Energia
    try {
        const data = await apiFetch('/api/kpis/energia');
        document.getElementById('kpi-energia').textContent = fmt(data.consumoKwh, 1) + ' kWh';
    } catch (e) { console.error('energia:', e); }
}

// ─────────────────────────────────────────────────────────
// FETCH — GRÁFICO HISTÓRICO 24H (janelas de 1h)
// ─────────────────────────────────────────────────────────
async function fetchHistorico() {
    try {
        const data = await apiFetch('/api/kpis/temperatura/historico');

        // Backend retorna janela = 0..23 (hora do dia em SP)
        const liquid  = new Array(24).fill(null);
        const ambient = new Array(24).fill(null);

        data.forEach(d => {
            // janela pode vir como string "00h" (legado 4h) ou número 0..23 (novo 1h)
            const idx = typeof d.janela === 'number'
                ? d.janela
                : parseInt(d.janela, 10);
            if (idx >= 0 && idx < 24) {
                liquid[idx]  = d.mediaLiquidTemp  != null ? +d.mediaLiquidTemp.toFixed(2)  : null;
                ambient[idx] = d.mediaAmbientTemp != null ? +d.mediaAmbientTemp.toFixed(2) : null;
            }
        });

        chartHistorico.data.datasets[0].data = liquid;
        chartHistorico.data.datasets[1].data = ambient;
        chartHistorico.update('none');
    } catch (e) { console.error('historico:', e); }
}

// ─────────────────────────────────────────────────────────
// FETCH — MÉDIA POR ETAPA
// ─────────────────────────────────────────────────────────
async function fetchMediaEtapa() {
    try {
        const data = await apiFetch('/api/kpis/temperatura/por-etapa');

        const ordem = ['Mostura', 'Fervura', 'Fermentação', 'Maturação'];
        const mapa  = {};
        data.forEach(d => {
            const pt = STAGE_PT[d.stage] ?? d.stage;
            mapa[pt] = d.media;
        });

        chartEtapa.data.datasets[0].data = ordem.map(s =>
            mapa[s] != null ? +Number(mapa[s]).toFixed(2) : null
        );
        chartEtapa.update('none');
    } catch (e) { console.error('media etapa:', e); }
}

// ─────────────────────────────────────────────────────────
// FETCH — ALERTAS POR SEVERIDADE
// ─────────────────────────────────────────────────────────
async function fetchSeveridade() {
    try {
        const d = await apiFetch('/api/kpis/alertas/por-severidade');
        chartSeveridade.data.datasets[0].data = [
            d.warning    ?? 0,
            d.critical   ?? 0,
            d.sensorFail ?? 0
        ];
        chartSeveridade.update('none');
    } catch (e) { console.error('severidade:', e); }
}

// ─────────────────────────────────────────────────────────
// FETCH — REAL VS PLANEJADO
// ─────────────────────────────────────────────────────────
async function fetchDuracao() {
    try {
        const data = await apiFetch('/api/kpis/etapas/duracao');

        const ordem = ['Mashing', 'Boiling', 'Fermentation', 'Maturation'];
        const mapa  = {};
        data.forEach(d => mapa[d.stage] = d);

        const minParaDias = min => min != null ? +(min / 1440).toFixed(2) : null;

        chartDuracao.data.datasets[0].data = ordem.map(s =>
            mapa[s] ? minParaDias(mapa[s].duracaoRealMinutos) : null
        );
        chartDuracao.data.datasets[1].data = ordem.map(s =>
            mapa[s] ? minParaDias(mapa[s].duracaoMaxPlanejadaMinutos) : null
        );
        chartDuracao.update('none');
    } catch (e) { console.error('duracao:', e); }
}

// ─────────────────────────────────────────────────────────
// FETCH — ALERTAS RECENTES
// ─────────────────────────────────────────────────────────
async function fetchAlertasRecentes() {
    try {
        const data  = await apiFetch('/api/alerts/recent');
        const lista = document.getElementById('alerts-list');
        const titulo = document.getElementById('alertas-titulo');

        titulo.textContent = 'Alertas recentes';

        if (!data.length) {
            lista.innerHTML = '<div class="alerta-carregando">Nenhum alerta nas últimas 24h.</div>';
            return;
        }

        lista.innerHTML = data.slice(0, 30).map(a => {
            const sev   = a.severity ?? 'INFO';
            const hora  = fmtHora(a.createdAt);
            const tipo  = TIPO_LABEL[sev] ?? sev;

            return `
                <div class="alerta-item sev-${sev}">
                    <span class="alerta-hora">${hora}</span>
                    <span class="alerta-tipo">${tipo}</span>
                    <p class="alerta-mensagem">${a.message}</p>
                </div>`;
        }).join('');

    } catch (e) {
        console.error('alertas recentes:', e);
        document.getElementById('alerts-list').innerHTML =
            '<div class="alerta-carregando">Erro ao carregar alertas.</div>';
    }
}

// ─────────────────────────────────────────────────────────
// ATUALIZAÇÃO COMPLETA
// ─────────────────────────────────────────────────────────
async function atualizarTudo() {
    await Promise.allSettled([
        fetchKpisIndividuais(),
        fetchHistorico(),
        fetchMediaEtapa(),
        fetchSeveridade(),
        fetchDuracao(),
        fetchAlertasRecentes()
    ]);
}

// ─────────────────────────────────────────────────────────
// INICIALIZAÇÃO
// ─────────────────────────────────────────────────────────
criarGraficos();
fetchEtapaAtual();   // popula o select uma vez ao carregar
atualizarTudo();
setInterval(atualizarTudo, REFRESH_MS);
