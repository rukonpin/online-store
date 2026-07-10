document.addEventListener('DOMContentLoaded', () => {
    const balanceEl = document.getElementById('user-balance');
    if (!balanceEl) return;

    fetch('/api/balance')
        .then(res => res.ok ? res.json() : Promise.reject())
        .then(data => {
            if (data.available) {
                balanceEl.textContent = 'Баланс: ' + Number(data.balance).toLocaleString('ru-RU') + ' ₽';
            } else {
                balanceEl.textContent = 'Баланс: недоступен';
            }
        })
        .catch(() => {
            balanceEl.textContent = 'Баланс: недоступен';
        });
});