import { LitElement, html, css } from 'https://cdn.jsdelivr.net/gh/lit/dist@3/core/lit-core.min.js';
import { fetchOrderDetails } from './api-client.js';

class OrderDetailsView extends LitElement {
    static properties = {
        orderId: { type: String },
        _order: { state: true },
        _error: { state: true }
    };

    static styles = css`
        :host { display: block; color: #222; }
        .card {
            background: #fff;
            border: 1px solid #ddd;
            padding: 16px;
        }
        h2 { margin: 0 0 12px; font-size: 1.1rem; color: #222; }
        .details-table { width: 100%; }
        .details-table td { padding: 8px; border-bottom: 1px solid #eee; }
        .details-table td:first-child { font-weight: 600; color: #555; width: 200px; }
        .back-link {
            display: inline-block;
            margin-bottom: 12px;
            color: #333;
            text-decoration: underline;
        }
        .error-msg { padding: 8px; border: 1px solid #ddd; background: #fafafa; }
    `;

    constructor() {
        super();
        this._order = null;
        this._error = '';
    }

    willUpdate(changed) {
        if (changed.has('orderId') && this.orderId) {
            this._loadOrder();
        }
    }

    async _loadOrder() {
        try {
            this._order = await fetchOrderDetails(this.orderId);
        } catch (e) {
            this._error = 'Błąd ładowania szczegółów zlecenia';
        }
    }

    render() {
        if (this._error) return html`<div class="card"><div class="error-msg">${this._error}</div></div>`;
        if (!this._order) return html`<div class="card">Ładowanie...</div>`;

        const o = this._order;
        return html`
            <div class="card">
                <a class="back-link" href="#orders">← Powrót do listy</a>
                <h2>Szczegóły zlecenia</h2>
                <table class="details-table">
                    <tr><td>Numer zlecenia</td><td>${o.orderId}</td></tr>
                    <tr><td>Status zlecenia</td><td>${o.status === 'Filled' ? 'Zrealizowane' : o.status}</td></tr>
                    <tr><td>ISIN</td><td>${o.isin}</td></tr>
                    <tr><td>Ticker</td><td>${o.ticker}</td></tr>
                    <tr><td>Waluta</td><td>${o.tradeCurrency}</td></tr>
                    <tr><td>Kurs</td><td>${o.executionPrice}</td></tr>
                    <tr><td>Liczba</td><td>${o.quantity}</td></tr>
                    <tr><td>Wartość zlecenia</td><td>${o.orderValue} PLN</td></tr>
                    <tr><td>Data rejestracji</td><td>${o.registrationTime}</td></tr>
                    <tr><td>Data wykonania</td><td>${o.executedTime}</td></tr>
                    <tr><td>Prowizja</td><td>${o.commission} PLN</td></tr>
                </table>
            </div>
        `;
    }
}

customElements.define('order-details-view', OrderDetailsView);

