/**
 * User Menu Handler - Logout functionality
 */

/**
 * Handle user logout
 */
async function handleLogout(event) {
    event.preventDefault();

    // Confirm logout
    const confirmLogout = confirm('Вы уверены, что хотите выйти?');
    if (!confirmLogout) {
        return;
    }

    try {
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            // Redirect to home page after successful logout
            window.location.href = '/products';
        } else {
            console.error('Logout failed');
            alert('Ошибка при выходе из аккаунта');
        }
    } catch (error) {
        console.error('Logout error:', error);
        alert('Ошибка соединения с сервером');
    }
}

/**
 * Close dropdown when clicking outside
 */
document.addEventListener('click', (event) => {
    const dropdown = document.querySelector('.user-avatar-dropdown');
    if (dropdown && !dropdown.contains(event.target)) {
        // Optional: Add 'open' class management if needed
        // For now, CSS handles hover state
    }
});

/**
 * Prevent dropdown from closing when clicking inside
 */
const dropdownMenu = document.querySelector('.user-dropdown-menu');
if (dropdownMenu) {
    dropdownMenu.addEventListener('click', (event) => {
        // Allow links to work normally
        if (event.target.tagName !== 'BUTTON') {
            return;
        }
        event.stopPropagation();
    });
}

/**
 * Keyboard navigation for accessibility
 */
document.addEventListener('DOMContentLoaded', () => {
    const avatar = document.querySelector('.user-avatar');
    const dropdown = document.querySelector('.user-dropdown-menu');

    if (avatar && dropdown) {
        // Make avatar keyboard accessible
        avatar.setAttribute('tabindex', '0');
        avatar.setAttribute('role', 'button');
        avatar.setAttribute('aria-haspopup', 'true');
        avatar.setAttribute('aria-expanded', 'false');

        // Toggle dropdown on Enter/Space
        avatar.addEventListener('keydown', (event) => {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                dropdown.classList.toggle('show');
                avatar.setAttribute('aria-expanded', dropdown.classList.contains('show'));
            }
        });

        // Close dropdown on Escape
        document.addEventListener('keydown', (event) => {
            if (event.key === 'Escape' && dropdown.classList.contains('show')) {
                dropdown.classList.remove('show');
                avatar.setAttribute('aria-expanded', 'false');
                avatar.focus();
            }
        });
    }
});