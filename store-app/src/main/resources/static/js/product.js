async function handleProductPageAddToCart(btn) {
    const productUuid = btn.dataset.productUuid;
    if (!productUuid) return;

    btn.disabled = true;
    const originalText = btn.textContent;
    btn.textContent = '...';

    try {
        const res = await fetch('/api/cart/items', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productUuid, quantity: 1 })
        });

        if (res.status === 401) {
            window.location.href = '/login';
            return;
        }

        if (res.ok) {
            const cart = await res.json();
            updateCartBadge(cart.items ? cart.items.length : 0);
            btn.textContent = '✓ Добавлено';
            btn.style.background = '#1e2d35';
            setTimeout(() => {
                btn.textContent = originalText;
                btn.style.background = '';
                btn.disabled = false;
            }, 1500);
        } else {
            btn.textContent = originalText;
            btn.disabled = false;
        }
    } catch (err) {
        console.error('Failed to add to cart:', err);
        btn.textContent = originalText;
        btn.disabled = false;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const tabs = document.querySelectorAll('.product-tab');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.product-tab-content').forEach(c => c.classList.add('hidden'));

            tab.classList.add('active');
            const target = document.getElementById('tab-' + tab.dataset.tab);
            if (target) target.classList.remove('hidden');
        });
    });
});