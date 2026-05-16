document.addEventListener('DOMContentLoaded', function() {
    initializeCartButtons();
});

function initializeCartButtons() {
    const buttons = document.querySelectorAll('.add-to-cart-btn-overlay');

    buttons.forEach(button => {
        button.addEventListener('click', async function(e) {
            e.preventDefault();
            e.stopPropagation(); // Не срабатывает клик по ссылке

            const uuid = this.getAttribute('data-product-uuid');
            const name = this.getAttribute('data-product-name');

            await addToCart(uuid, name);
        });
    });
}

async function addToCart(uuid, name) {
    try {
        const response = await fetch('/api/cart/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productUuid: uuid, quantity: 1 })
        });

        if (response.ok) {
            console.log(`Added ${name} to cart`);
            updateCartCount();
        } else if (response.status === 401) {
            // Редирект на логин
            window.location.href = '/login';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function updateCartCount() {
    const response = await fetch('/api/cart/count');
    const data = await response.json();

    const countElement = document.querySelector('.count');
    if (countElement) {
        countElement.textContent = data.count;
    }
}