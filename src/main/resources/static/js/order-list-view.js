import { LitElement, html, css } from 'https://cdn.jsdelivr.net/gh/lit/dist@3/core/lit-core.min.js';
import { fetchOrders } from './api-client.js';

class OrderListView extends LitElement {
    static properties = {
        _orders: { state: true },
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
        table { width: 100%; border-collapse: collapse; }
        th {
            background: #fafafa;
            border-bottom: 1px solid #ddd;
            padding: 8px;
            text-align: left;
            font-size: 0.9rem;
            font-weight: 600;
        }
        td { padding: 8px; border-bottom: 1px solid #eee; }
        a { color: #333; text-decoration: underline; }
        .status { font-weight: 600; }
        .empty { text-align: center; padding: 40px; color: #999; }
        .error-msg { padding: 8px; border: 1px solid #ddd; background: #fafafa; margin-bottom: 12px; }
    `;

    constructor() {
        super();
        this._orders = [];
        this._error = '';
        this._interval = null;
    }

    connectedCallback() {
        super.connectedCallback();
        this._loadOrders();
        this._interval = setInterval(() => this._loadOrders(), 10000);
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        if (this._interval) clearInterval(this._interval);
    }

    async _loadOrders() {
        try {
            this._orders = await fetchOrders();
        } catch (e) {
            this._error = 'Błąd ładowania zleceń';
        }
    }

    _statusClass() {
        return 'status';
    }

    _statusLabel(status) {
        if (status === 'Filled') return 'Zrealizowane';
        if (status === 'Submitted') return 'Oczekujące';
        if (status === 'Expired') return 'Wygasłe';
        return status;
    }

    render() {
        return html`
            <div class="card">
                <h2>Moje zlecenia</h2>
                ${this._error ? html`<div class="error-msg">${this._error}</div>` : ''}
                ${this._orders.length === 0
                    ? html`<div class="empty">Brak zleceń</div>`
                    : html`
                        <table>
                            <thead>
                                <tr>
                                    <th>Numer zlecenia</th>
                                    <th>Status</th>
                                    <th>ISIN</th>
                                    <th>Liczba</th>
                                    <th>Limit</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                ${this._orders.map(o => html`
                                    <tr>
                                        <td>${o.orderId}</td>
                                        <td class="${this._statusClass(o.status)}">${this._statusLabel(o.status)}</td>
                                        <td>${o.isin}</td>
                                        <td>${o.quantity}</td>
                                        <td>${o.limitPrice != null ? o.limitPrice + ' PLN' : '-'}</td>
                                        <td>
                                            ${o.status === 'Filled'
                                                ? html`<a href="#orders/${o.orderId}">Zobacz szczegóły</a>`
                                                : ''}
                                        </td>
                                    </tr>
                                `)}
                            </tbody>
                        </table>
                    `}
            </div>
        `;
    }
}

customElements.define('order-list-view', OrderListView);

