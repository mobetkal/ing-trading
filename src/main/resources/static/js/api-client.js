const API_BASE = '/api';

export async function fetchTickers() {
    const res = await fetch(`${API_BASE}/tickers`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

export async function fetchPrices() {
    const res = await fetch(`${API_BASE}/prices`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

export async function submitOrder(data) {
    const res = await fetch(`${API_BASE}/orders`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

export async function fetchOrders() {
    const res = await fetch(`${API_BASE}/orders`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

export async function fetchOrderDetails(orderId) {
    const res = await fetch(`${API_BASE}/orders/${orderId}`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

