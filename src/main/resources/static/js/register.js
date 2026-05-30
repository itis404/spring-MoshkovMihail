document.addEventListener("DOMContentLoaded", function () {
    var form = document.getElementById("regForm");
    var password = document.getElementById("password");
    var confirmPassword = document.getElementById("confirmPassword");
    var error = document.getElementById("passwordRepeatError");

    if (!form || !password || !confirmPassword) {
        return;
    }

    form.addEventListener("submit", function (event) {
        if (password.value !== confirmPassword.value) {
            event.preventDefault();
            error.textContent = "Passwords do not match";
            confirmPassword.classList.add("input-error");
        } else {
            error.textContent = "";
            confirmPassword.classList.remove("input-error");
        }
    });
});
