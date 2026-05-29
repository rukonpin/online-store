document.addEventListener('DOMContentLoaded', function() {
    const sortDropdown = document.querySelector('.sort-dropdown');

    if (!sortDropdown) return;

    sortDropdown.addEventListener('click', function(e) {
        if (e.target.closest('.sort-option')) return;

        e.stopPropagation();
        this.classList.toggle('open');
    });

    document.addEventListener('click', function(e) {
        if (!sortDropdown.contains(e.target)) {
            sortDropdown.classList.remove('open');
        }
    });

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            sortDropdown.classList.remove('open');
        }
    });
});