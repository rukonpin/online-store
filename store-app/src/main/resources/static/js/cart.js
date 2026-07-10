document.addEventListener('DOMContentLoaded', () => {
    document.addEventListener('click', (e) => {
        const btn = e.target.closest('.add-to-cart-btn-overlay');
        if (btn) {
            e.preventDefault();
            handleAddToCart(btn);
        }
    });
});

async function handleAddToCart(btn) {
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
            btn.textContent = '✓';
            btn.style.background = '#1e2d35';
            btn.style.color = '#fff';
            setTimeout(() => {
                btn.textContent = originalText;
                btn.style.background = '';
                btn.style.color = '';
                btn.disabled = false;
            }, 1200);
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

function updateCartBadge(count) {
    const wrapper = document.querySelector('.handbag-wrapper');
    if (!wrapper) return;

    let ellipse = wrapper.querySelector('.ellipse');

    if (count > 0) {
        if (!ellipse) {
            ellipse = document.createElement('div');
            ellipse.className = 'ellipse';
            const span = document.createElement('span');
            span.className = 'count';
            ellipse.appendChild(span);
            wrapper.appendChild(ellipse);
        }
        const countEl = ellipse.querySelector('.count');
        if (countEl) countEl.textContent = count;
        ellipse.style.display = '';
    } else {
        if (ellipse) ellipse.style.display = 'none';
    }
}