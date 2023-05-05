function showPassword() {
    const x = document.getElementById("password");
    const y = document.getElementById("confirmPassword");
    if (x.type === "password") {
        x.type = "text";
        y.type = "text";
    } else {
        x.type = "password";
        y.type = "password";
    }
}