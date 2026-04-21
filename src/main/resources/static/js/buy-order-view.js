import {css, html, LitElement} from 'https://cdn.jsdelivr.net/gh/lit/dist@3/core/lit-core.min.js';
import {fetchPrices, fetchTickers, submitOrder} from './api-client.js';

class BuyOrderView extends LitElement {
    static properties = {
        _tickers: {state: true},
        _prices: {state: true},
        _selectedIsin: {state: true},
        _quantity: {state: true},
        _orderType: {state: true},
        _limitPrice: {state: true},
        _expiresAt: {state: true},
        _currentPrice: {state: true},
        _message: {state: true},
        _error: {state: true},
        _loading: {state: true},
        _searchTerm: {state: true}
    };

    static styles = css`
        :host { display: block; }
        .card {
            background: #fff;
            border: 1px solid #ddd;
            padding: 16px;
        }
        h2 { margin: 0 0 12px; font-size: 1.1rem; color: #222; }
        .form-group { margin-bottom: 12px; }
        label { display: block; font-weight: 600; margin-bottom: 4px; font-size: 0.9rem; }
        input, select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 2px;
            box-sizing: border-box;
            font: inherit;
        }
        input:focus, select:focus { outline: 1px solid #999; }
        .required::after { content: ' *'; color: red; }
        button {
            background: #f2f2f2;
            color: #222;
            border: 1px solid #bbb;
            padding: 8px 12px;
            border-radius: 2px;
            font: inherit;
            cursor: pointer;
        }
        button:disabled { color: #777; cursor: not-allowed; }
        .message {
            margin-top: 12px;
            padding: 8px;
            border: 1px solid #ddd;
            background: #fafafa;
        }
        .success { color: #222; }
        .error-msg { color: #222; }
        .price-display {
            padding: 8px;
            border: 1px solid #ddd;
            background: #fafafa;
        }
        .ticker-list {
            max-height: 200px;
            overflow-y: auto;
            border: 1px solid #ccc;
        }
        .ticker-item {
            padding: 8px 12px; cursor: pointer; border-bottom: 1px solid #eee;
        }
        .ticker-item.selected { background: #f3f3f3; font-weight: 600; }
        .ticker-info { font-size: 0.85em; color: #666; }
        .ticker-item.selected .ticker-info { color: #666; }
    `;

    constructor() {
        super();
        this._tickers = [];
        this._prices = [];
        this._selectedIsin = '';
        this._quantity = '';
        this._orderType = 'LMT';
        this._limitPrice = '';
        this._expiresAt = '';
        this._currentPrice = null;
        this._message = '';
        this._error = '';
        this._loading = false;
        this._searchTerm = '';
    }

    connectedCallback() {
        super.connectedCallback();
        this._loadData();
    }

    async _loadData() {
        try {
            const [tickers, prices] = await Promise.all([fetchTickers(), fetchPrices()]);
            this._tickers = tickers;
            this._prices = prices;
        } catch (e) {
            this._error = 'Błąd ładowania danych rynkowych';
        }
    }

    _selectTicker(isin) {
        this._selectedIsin = isin;
        const price = this._prices.find(p => p.isin === isin);
        this._currentPrice = price ? price.price : null;
    }

    get _filteredTickers() {
        const term = this._searchTerm.toLowerCase();
        if (!term) return this._tickers;
        return this._tickers.filter(t =>
            t.name.toLowerCase().includes(term) ||
            t.ticker.toLowerCase().includes(term) ||
            t.isin.toLowerCase().includes(term)
        );
    }

    async _submit() {
        this._message = '';
        this._error = '';

        if (!this._selectedIsin) {
            this._error = 'Wybierz papier wartościowy';
            return;
        }
        if (!this._quantity || this._quantity <= 0) {
            this._error = 'Podaj prawidłową liczbę';
            return;
        }
        if (this._orderType === 'LMT' && (!this._limitPrice || this._limitPrice <= 0)) {
            this._error = 'Podaj limit ceny dla zlecenia LMT';
            return;
        }

        this._loading = true;
        try {
            const data = {
                isin: this._selectedIsin,
                quantity: parseInt(this._quantity),
                orderType: this._orderType,
                limitPrice: this._orderType === 'LMT' ? parseFloat(this._limitPrice) : null,
                expiresAt: this._expiresAt ? Math.floor(new Date(this._expiresAt).getTime() / 1000) : null
            };
            const result = await submitOrder(data);
            this._message = `Zlecenie złożone! Nr: ${result.orderId}, Status: ${result.status}`;
            this._quantity = '';
            this._limitPrice = '';
            this._expiresAt = '';
        } catch (e) {
            this._error = 'Błąd składania zlecenia: ' + e.message;
        } finally {
            this._loading = false;
        }
    }

    render() {
        return html`
            <div class="card">
                <h2>Kupno papieru wartościowego</h2>

                <div class="form-group">
                    <label class="required">Wyszukaj papier</label>
                    <input type="text" placeholder="Szukaj po nazwie, tickerze lub ISIN..."
                           .value="${this._searchTerm}"
                           @input="${e => this._searchTerm = e.target.value}">
                    <div class="ticker-list">
                        ${this._filteredTickers.map(t => html`
                            <div class="ticker-item ${this._selectedIsin === t.isin ? 'selected' : ''}"
                                 @click="${() => this._selectTicker(t.isin)}">
                                <strong>${t.ticker}</strong> - ${t.name}
                                <div class="ticker-info">ISIN: ${t.isin} | ${t.tradeCurrency} | ${t.mic}</div>
                            </div>
                        `)}
                    </div>
                </div>

                <div class="form-group">
                    <label class="required">Liczba do zakupu</label>
                    <input type="number" min="1" .value="${this._quantity}"
                           @input="${e => this._quantity = e.target.value}">
                </div>

                <div class="form-group">
                    <label class="required">Typ zlecenia</label>
                    <select .value="${this._orderType}" @change="${e => this._orderType = e.target.value}">
                        <option value="LMT">LMT - z limitem ceny</option>
                        <option value="PKC">PKC - po każdej cenie</option>
                        <option value="PCR">PCR - po cenie rynkowej</option>
                    </select>
                </div>

                ${this._orderType === 'LMT' ? html`
                    <div class="form-group">
                        <label class="required">Limit ceny (PLN)</label>
                        <input type="number" step="0.01" min="0.01" .value="${this._limitPrice}"
                               @input="${e => this._limitPrice = e.target.value}">
                    </div>
                ` : ''}

                <div class="form-group">
                    <label>Zlecenie ważne do</label>
                    <input type="datetime-local" .value="${this._expiresAt}"
                           @input="${e => this._expiresAt = e.target.value}">
                </div>

                <div class="form-group">
                    <label>Aktualna cena</label>
                    <div class="price-display">
                        ${this._currentPrice != null ? `${this._currentPrice} PLN` : 'Wybierz papier'}
                    </div>
                </div>

                <button @click="${this._submit}" ?disabled="${this._loading}">
                    ${this._loading ? 'Składanie...' : 'Złóż zlecenie kupna'}
                </button>

                ${this._message ? html`
                    <div class="message success">${this._message}</div>` : ''}
                ${this._error ? html`
                    <div class="message error-msg">${this._error}</div>` : ''}
            </div>
        `;
    }
}

customElements.define('buy-order-view', BuyOrderView);

