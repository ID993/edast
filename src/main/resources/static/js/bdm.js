const radioButtons = document.getElementsByName('bdmSelection');
const birthFieldsDiv = document.getElementById('birthFields');
const deathFieldsDiv = document.getElementById('deathFields');
const marriageFieldsDiv = document.getElementById('marriageFields');
function showFields() {
    birthFieldsDiv.style.display = 'none';
    deathFieldsDiv.style.display = 'none';
    marriageFieldsDiv.style.display = 'none';

    for (let radioButton of radioButtons) {
        if (radioButton.checked) {
            const selectedValue = radioButton.value;
            console.log("Selected value: " + selectedValue)
            if (selectedValue === 'Birth') {
                birthFieldsDiv.style.display = 'block';
                document.getElementById('dateOfBirth').required = true;
                document.getElementById('placeOfBirth').required = true;
                document.getElementById('dateOfDeath').required = false;
                document.getElementById('dateOfMarriage').required = false;
                document.getElementById('placeOfMarriage').required = false;
            } else if (selectedValue === 'Death') {
                deathFieldsDiv.style.display = 'block';
                document.getElementById('dateOfDeath').required = true;
                document.getElementById('dateOfBirth').required = false;
                document.getElementById('placeOfBirth').required = false;
                document.getElementById('dateOfMarriage').required = false;
                document.getElementById('placeOfMarriage').required = false;
            } else if (selectedValue === 'Marriage') {
                marriageFieldsDiv.style.display = 'block';
                document.getElementById('dateOfMarriage').required = true;
                document.getElementById('placeOfMarriage').required = true;
                document.getElementById('dateOfBirth').required = false;
                document.getElementById('placeOfBirth').required = false;
                document.getElementById('dateOfDeath').required = false;
            }
            break;
        }
    }
}
for (let radioButton of radioButtons) {
    radioButton.addEventListener('change', showFields);
}
showFields();