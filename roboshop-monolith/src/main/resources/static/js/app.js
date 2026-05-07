// RoboShop Monolith - JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Quantity input auto-submit
    document.querySelectorAll('.qty-input').forEach(function(input) {
        input.addEventListener('change', function() {
            this.form.submit();
        });
    });
});
