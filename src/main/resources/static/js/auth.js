/**
 * Auth Form Handler (Registration & Login)
 */

document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('registerForm');

    if (registerForm) {
        registerForm.addEventListener('submit', handleRegisterSubmit);

        // Real-time password match validation
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');

        if (password && confirmPassword) {
            confirmPassword.addEventListener('input', () => {
                validatePasswordMatch(password, confirmPassword);
            });
        }
    }
});

/**
 * Handle registration form submission
 */
async function handleRegisterSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);

    const data = {
        username: formData.get('username'),
        email: formData.get('email'),
        password: formData.get('password'),
        confirmPassword: formData.get('confirmPassword')
    };

    // Client-side validation
    const validationError = validateRegistrationData(data);
    if (validationError) {
        showMessage(validationError, 'error');
        return;
    }

    // Clear previous messages
    clearMessages();

    // Disable submit button
    const submitBtn = form.querySelector('.auth-submit-btn');
    const originalText = submitBtn.textContent;
    submitBtn.disabled = true;
    submitBtn.textContent = 'Создание аккаунта...';

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            showMessage('Аккаунт успешно создан! Перенаправление...', 'success');
            setTimeout(() => {
                window.location.href = '/login';
            }, 1500);
        } else {
            const errorData = await response.json();
            showMessage(errorData.message || 'Ошибка при создании аккаунта', 'error');
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        }
    } catch (error) {
        console.error('Registration error:', error);
        showMessage('Ошибка соединения с сервером', 'error');
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    }
}

/**
 * Validate registration data
 */
function validateRegistrationData(data) {
    // Username validation
    if (!data.username || data.username.trim().length < 2) {
        return 'Имя должно содержать минимум 2 символа';
    }

    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(data.email)) {
        return 'Неверный формат email';
    }

    // Password validation
    if (data.password.length < 6) {
        return 'Пароль должен содержать минимум 6 символов';
    }

    // Password match validation
    if (data.password !== data.confirmPassword) {
        return 'Пароли не совпадают';
    }

    return null;
}

/**
 * Real-time password match validation
 */
function validatePasswordMatch(passwordInput, confirmPasswordInput) {
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    if (confirmPassword.length === 0) {
        confirmPasswordInput.classList.remove('invalid', 'valid');
        return;
    }

    if (password === confirmPassword) {
        confirmPasswordInput.classList.remove('invalid');
        confirmPasswordInput.classList.add('valid');
    } else {
        confirmPasswordInput.classList.remove('valid');
        confirmPasswordInput.classList.add('invalid');
    }
}

/**
 * Show message to user
 */
function showMessage(message, type = 'error') {
    clearMessages();

    const messageDiv = document.createElement('div');
    messageDiv.className = `form-message ${type}`;
    messageDiv.textContent = message;

    const form = document.querySelector('.auth-form');
    form.insertBefore(messageDiv, form.firstChild);

    // Auto-remove success messages after 5 seconds
    if (type === 'success') {
        setTimeout(() => {
            messageDiv.remove();
        }, 5000);
    }
}

/**
 * Clear all messages
 */
function clearMessages() {
    const existingMessages = document.querySelectorAll('.form-message');
    existingMessages.forEach(msg => msg.remove());
}

/**
 * Login form handler
 */
async function handleLoginSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);

    const data = {
        email: formData.get('email'),
        password: formData.get('password')
    };

    // Clear previous messages
    clearMessages();

    // Disable submit button
    const submitBtn = form.querySelector('.auth-submit-btn');
    const originalText = submitBtn.textContent;
    submitBtn.disabled = true;
    submitBtn.textContent = 'Вход...';

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            showMessage('Успешный вход! Перенаправление...', 'success');
            setTimeout(() => {
                window.location.href = '/products';
            }, 1000);
        } else {
            const errorData = await response.json();
            showMessage(errorData.message || 'Неверный email или пароль', 'error');
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        }
    } catch (error) {
        console.error('Login error:', error);
        showMessage('Ошибка соединения с сервером', 'error');
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    }
}

// Attach login handler if login form exists
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLoginSubmit);
    }
});