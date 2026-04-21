import { LitElement, html, css } from 'https://cdn.jsdelivr.net/gh/lit/dist@3/core/lit-core.min.js';
import './buy-order-view.js';
import './order-list-view.js';
import './order-details-view.js';

class AppShell extends LitElement {
    static properties = {
        _view: { state: true },
        _orderId: { state: true }
    };

    static styles = css`
        :host { display: block; }
        header {
            background: #ff6200;
            color: white;
            padding: 16px 32px;
            font-size: 1.4em;
            font-weight: bold;
            display: flex;
            align-items: center;
            gap: 32px;
        }
        nav a {
            color: white;
            text-decoration: none;
            font-size: 0.7em;
            padding: 6px 16px;
            border-radius: 4px;
            cursor: pointer;
        }
        nav a:hover, nav a.active { background: rgba(255,255,255,0.2); }
        main { max-width: 960px; margin: 24px auto; padding: 0 16px; }
    `;

    constructor() {
        super();
        this._view = 'buy';
        this._orderId = null;
        window.addEventListener('hashchange', () => this._route());
        this._route();
    }

    _route() {
        const hash = location.hash || '#buy';
        if (hash.startsWith('#orders/')) {
            this._view = 'details';
            this._orderId = hash.split('/')[1];
        } else if (hash === '#orders') {
            this._view = 'orders';
        } else {
            this._view = 'buy';
        }
    }

    render() {
        return html`
            <header>
                <span>ING Trading</span>
                <nav>
                    <a href="#buy" class="${this._view === 'buy' ? 'active' : ''}">Kup papier</a>
                    <a href="#orders" class="${this._view === 'orders' ? 'active' : ''}">Moje zlecenia</a>
                </nav>
            </header>
            <main>
                ${this._view === 'buy' ? html`<buy-order-view></buy-order-view>` : ''}
                ${this._view === 'orders' ? html`<order-list-view></order-list-view>` : ''}
                ${this._view === 'details' ? html`<order-details-view .orderId="${this._orderId}"></order-details-view>` : ''}
            </main>
        `;
    }
}

customElements.define('app-shell', AppShell);

