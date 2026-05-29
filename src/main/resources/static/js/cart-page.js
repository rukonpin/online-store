async function handleUpdateQuantity(btn) {
    const itemUuid = btn.dataset.itemUuid;
    const quantity = parseInt(btn.dataset.quantity, 10);

    if (isNaN(quantity) || quantity < 1) return;

    try {
        const res = await fetch(`/api/cart/items/${itemUuid}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ quantity })
        });

        if (res.status === 401) {
            window.location.href = '/login';
            return;
        }

        if (res.ok) {
            window.location.reload();
        }
    } catch (err) {
        console.error('Failed to update quantity:', err);
    }
}

async function handleRemoveItem(btn) {
    const itemUuid = btn.dataset.itemUuid;

    try {
        const res = await fetch(`/api/cart/items/${itemUuid}`, {
            method: 'DELETE'
        });

        if (res.status === 401) {
            window.location.href = '/login';
            return;
        }

        if (res.ok) {
            window.location.reload();
        }
    } catch (err) {
        console.error('Failed to remove item:', err);
    }
}

async function handleClearCart() {
    try {
        const res = await fetch('/api/cart', {
            method: 'DELETE'
        });

        if (res.status === 401) {
            window.location.href = '/login';
            return;
        }

        if (res.ok) {
            window.location.reload();
        }
    } catch (err) {
        console.error('Failed to clear cart:', err);
    }
}

function handleCheckout() {
    window.location.href = '/checkout';
}