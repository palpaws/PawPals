document.addEventListener('DOMContentLoaded', function() {
    const accordionHeaders = document.querySelectorAll('.accordion-header');

    accordionHeaders.forEach(header => {
        header.addEventListener('click', function() {
            const accordionItem = this.closest('.accordion-item');
            const accordion = accordionItem.closest('.accordion');

            // Close all other items in the same accordion group
            accordion.querySelectorAll('.accordion-item.active').forEach(openItem => {
                if (openItem !== accordionItem) {
                    openItem.classList.remove('active');
                    openItem.querySelector('.accordion-header').setAttribute('aria-expanded', 'false');
                }
            });

            // Toggle current item
            const isActive = accordionItem.classList.contains('active');
            accordionItem.classList.toggle('active');
            this.setAttribute('aria-expanded', !isActive);
        });

        // Keyboard support: Enter and Space
        header.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                this.click();
            }
        });
    });
});