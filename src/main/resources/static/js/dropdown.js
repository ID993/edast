function accDropDown() {
    document.getElementById("accDropdown").classList.toggle("show");
}
function reqDropDown() {
    document.getElementById("reqDropdown").classList.toggle("show");
}
function userDropDown() {
    document.getElementById("userDropdown").classList.toggle("show");
}

function reservationDropDown() {
    document.getElementById("reservationDropdown").classList.toggle("show");
}

window.onclick = function(event) {
    if (!event.target.matches('.dropbtn')) {
        const dropdowns = document.getElementsByClassName("dropdown-content");
        let i;
        for (i = 0; i < dropdowns.length; i++) {
            const openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
}

