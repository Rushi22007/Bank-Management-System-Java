// Smooth scroll for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Add animation on scroll
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
        }
    });
}, observerOptions);

// Observe all feature cards and steps
document.addEventListener('DOMContentLoaded', () => {
    const animatedElements = document.querySelectorAll('.feature-card, .step, .tech-item');
    
    animatedElements.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(el);
    });
});

// Add download tracking
document.querySelectorAll('a[download]').forEach(link => {
    link.addEventListener('click', function() {
        console.log('Download initiated: BankManagementSystem.jar');
        // You can add analytics tracking here
    });
});

// Copy demo credentials on click
const demoText = document.querySelector('.demo-text');
if (demoText) {
    demoText.style.cursor = 'pointer';
    demoText.title = 'Click to copy';
    
    demoText.addEventListener('click', function() {
        const text = 'Card: 1234567890123456 | PIN: 1234';
        navigator.clipboard.writeText(text).then(() => {
            const originalText = this.textContent;
            this.textContent = '✓ Copied to clipboard!';
            setTimeout(() => {
                this.textContent = originalText;
            }, 2000);
        });
    });
}
